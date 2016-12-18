
package offlineweb.manager.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import static offlineweb.manager.indexer.util.IndexerUtil.findMapperFiles;
import static offlineweb.manager.indexer.util.IndexerUtil.getIdMapping;
import org.apache.commons.codec.binary.Hex;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author papa2
 */
public class WikiImageLinkUpdater {
    private static int MAX_WORKER = 200;
    private static final String IMAGE_URL_TEMPLATE = "{{image_url}}";
    
    private static final Properties WIKI_CRAWL_PROPS;
    private static final String CRAWL_PROPS_FILE = "wiki-crawl.properties";
    
    static {
        WIKI_CRAWL_PROPS = new Properties();
        try {
            InputStream crawlConfigStream = 
                    WikiImageLinkUpdater.class
                            .getClassLoader().getResourceAsStream(CRAWL_PROPS_FILE);
            WIKI_CRAWL_PROPS.load(crawlConfigStream);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
     private static MessageDigest _messageDigest;
    
    // initialize the message digest for MD5 algorithm
    static {
        try {
            _messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ex) {
            _messageDigest = null;
        } 
    }
    // Directory for name to id mapping files
    private static final String MAP_DIR
            = WIKI_CRAWL_PROPS.getProperty("wiki.dir.map");

    // root directory to store html & text files
    private static final String CONTENT_DIR
            = WIKI_CRAWL_PROPS.getProperty("wiki.dir.content");
    
     // wait time, necessary to let the thread to complete execution
    private static final int WAIT_TIME = 4;
    
    private static final int MAX_THREAD = 10;

    private static final String WIKI_LOG
            = String.format(WIKI_CRAWL_PROPS.getProperty("wiki.status.log"), "all");

    private static final LoggerUtil logger
            = LoggerUtil.getLoggerUtil(WikiImageLinkUpdater.class, WIKI_LOG);
    
    
    
    public static void main(String[] args) {
        if (_messageDigest == null) {
            return;
        }

        startUpdatingLink();
    }

    private static void startUpdatingLink() {
        File[] mapperIdFiles = findMapperFiles(MAP_DIR);
        
        for (File mapperIdFile : mapperIdFiles) {
            logger.log("Starting : ", mapperIdFile.getAbsolutePath());

            Queue<Map.Entry<String, String>> idMappings = getIdMapping(mapperIdFile);

            if (!idMappings.isEmpty() && idMappings.size() != 0) {
                logger.log("Total docs", idMappings.size());
                executeForMapFile(idMappings);
            }
            logger.log("Completed : ", mapperIdFile.getAbsolutePath());           
        }
    }

    private static void executeForMapFile(Queue<Map.Entry<String, String>> idMappings) {
        if (idMappings.isEmpty() || idMappings.size() == 0) {
            return;
        }

        int taskCounter = 0; // no. of tasks executed
        ExecutorService updaterExecutor = Executors.newFixedThreadPool(MAX_THREAD); // for storing htmls
        List<Callable<Boolean>> updater = new ArrayList<>(); // list of html task thread
        while (!idMappings.isEmpty()) {

            Map.Entry<String, String> idEntry = idMappings.poll(); //

            if (idEntry != null) {
                updater.add(new LinkUpdater(idEntry.getValue(), idEntry.getKey()));
                taskCounter++;
            }

            if (taskCounter % MAX_WORKER == 0 || idMappings.isEmpty()) {
                startExecutorService(updaterExecutor, updater);
                logger.log("Files moved", taskCounter);

                updater.clear();
                updaterExecutor = Executors.newSingleThreadExecutor();
                logger.log("ROLLING OVER");
            }
        }
    }

    private static void startExecutorService(ExecutorService updaterExecutor, List<Callable<Boolean>> updater) {
         try {
            if (updaterExecutor != null) {
                updaterExecutor.invokeAll(updater);
                updaterExecutor.shutdown();
                updaterExecutor.awaitTermination(WAIT_TIME, TimeUnit.MINUTES);
            }
        } catch (InterruptedException ex) {
            // do nothing
        }
    }
    
    static class LinkUpdater implements Callable<Boolean> {
        
        private String fileId;
        private String fileName;
        
        private static final String MATCH = "/wikipedia/image?f=File:";
        private static final String PART = "File:";
        private static final char QUOTE = '"';

        public LinkUpdater(String fileId, String fileName) {
            this.fileId = fileId;
            this.fileName = fileName;
        }

        @Override
        public Boolean call() throws Exception {
            char firstChar = this.fileName.charAt(0);
            
            StringBuilder moveToDirPath = new StringBuilder(CONTENT_DIR)
                    .append(File.separator)
                    .append(firstChar)
                    .append(File.separator);
            
            for (int i = 0; i < fileId.length() - 1; i++) {
                moveToDirPath.append(fileId.charAt(i))
                    .append(File.separator);
            }

            String newHtmlType = String.format("%s%s-h", moveToDirPath.toString(), fileId);
            File htmlFile = new File(newHtmlType);
            
            updateLink(htmlFile);
            
            return true;
        }

        private void updateLink(File htmlFile) {
            if (!htmlFile.exists()) {
                return;
            }
            
            try {
                Document htmlDoc = Jsoup.parse(htmlFile, "UTF-8");
                
                updateImageSrc(htmlDoc);
                
                updateImageLink(htmlDoc);
                
                BufferedWriter htmlWriter = new BufferedWriter(new FileWriter(htmlFile, false));
                htmlWriter.write(htmlDoc.outerHtml());
                htmlWriter.close();
                
                logger.log("Updated : ", htmlFile.getAbsolutePath());
                
            } catch (IOException ex) {
                
            }
            
        }

        private void updateImageSrc(Document htmlDoc) {
            Elements allImages = htmlDoc.getElementsByTag("img");

            if (allImages.size() < 1) {
                return;
            }
            
            for (Element image : allImages) {
                image.attr("src", getImageUrl(image.attr("src")));
            }
        }

        private void updateImageLink(Document htmlDoc) {
            Elements allLinks = htmlDoc.getElementsByClass("image");
            
            if (allLinks.size() < 1) {
                return;
            }
            
            for (Element link : allLinks) {
                link.attr("href", getImageUrl(link.attr("href")));
            }
        }

        private String getImageUrl(String oldImageLink) {
            try {
               oldImageLink = URLDecoder.decode(oldImageLink, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                // do nothing
            }
            
            oldImageLink = oldImageLink.substring(oldImageLink.indexOf(PART) + PART.length() + 1);
            String[] linkParts = oldImageLink.split("/");
            
            String pen = linkParts[linkParts.length - 2];
            String end = linkParts[linkParts.length - 1];
            String digest = null;
            String imageName = null;
            
            if (pen.endsWith("svg") || pen.endsWith("jpg") 
                    || pen.endsWith("png") || pen.endsWith("gif")
                    || pen.endsWith("SVG") || pen.endsWith("JPG") 
                    || pen.endsWith("PNG") || pen.endsWith("GIF")) {
                digest = pen;
                imageName = String.format("%s/%s", pen, end);
            } else {
                digest = end;
                imageName = end;
            }
            digest = Hex.encodeHexString(_messageDigest.digest(digest.getBytes()));

            String newImageLink = String.format("%s/%s/%s/%s", IMAGE_URL_TEMPLATE, 
                    digest.charAt(0), "" + digest.charAt(0) + digest.charAt(1), imageName); 

            //System.out.println(newImageLink);
            //System.out.println("-----------------------");
            return newImageLink;
        }
        
    }
}
