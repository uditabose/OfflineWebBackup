
package offlineweb.manager.util;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import offlineweb.manager.crawler.wikipedia.WikipediaHtmlNTextCrawler;

import static offlineweb.manager.indexer.util.IndexerUtil.*;


/**
 *
 * @author papa2
 */
public class WikiFileDistributor {
    
    private static int MAX_WORKER = 200;
    
    private static final Properties WIKI_CRAWL_PROPS;
    private static final String CRAWL_PROPS_FILE = "wiki-crawl.properties";

    static {
        WIKI_CRAWL_PROPS = new Properties();
        try {
            InputStream crawlConfigStream = 
                    WikipediaHtmlNTextCrawler.class
                            .getClassLoader().getResourceAsStream(CRAWL_PROPS_FILE);
            WIKI_CRAWL_PROPS.load(crawlConfigStream);
        } catch (Exception ex) {
            ex.printStackTrace();
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
            = LoggerUtil.getLoggerUtil(WikiFileDistributor.class, WIKI_LOG);
    
    
    
    public static void main(String[] args) {
        startDistributorExecution();
    }

    private static void startDistributorExecution() {
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
        ExecutorService distributorExecutor = Executors.newFixedThreadPool(MAX_THREAD); // for storing htmls
        List<Callable<Boolean>> distributors = new ArrayList<>(); // list of html task thread
        while (!idMappings.isEmpty()) {

            Map.Entry<String, String> idEntry = idMappings.poll(); //

            if (idEntry != null) {
                distributors.add(new Distributor(idEntry.getValue(), idEntry.getKey()));
                taskCounter++;
            }

            if (taskCounter % MAX_WORKER == 0 || idMappings.isEmpty()) {
                startExecutorService(distributorExecutor, distributors);
                logger.log("Files moved", taskCounter);

                distributors.clear();
                distributorExecutor = Executors.newSingleThreadExecutor();
                logger.log("ROLLING OVER");
            }
        }
    }

    private static void startExecutorService(ExecutorService distributorExecutor, List<Callable<Boolean>> distributors) {
        try {
            if (distributorExecutor != null) {
                distributorExecutor.invokeAll(distributors);
                distributorExecutor.shutdown();
                distributorExecutor.awaitTermination(WAIT_TIME, TimeUnit.MINUTES);
            }
        } catch (InterruptedException ex) {
            // do nothing
        }
    }
    
    static class Distributor implements Callable<Boolean> {

        private String fileId;
        private String fileName;


        public Distributor(String fileId, String fileName) {
            this.fileId = fileId;
            this.fileName = fileName;
        }

        @Override
        public Boolean call() throws Exception {
            char firstChar = this.fileName.charAt(0);
            String baseFilePath = new StringBuilder(CONTENT_DIR)
                    .append(File.separator)
                    .append(firstChar)
                    .append(File.separator)
                    .append(fileId).toString();
            
            String wikiType = baseFilePath;
            String htmlType = String.format("%s-h", baseFilePath);
            String textType = String.format("%s-t", baseFilePath);

            StringBuilder moveToDirPath = new StringBuilder(CONTENT_DIR)
                    .append(File.separator)
                    .append(firstChar)
                    .append(File.separator);
            
            for (int i = 0; i < fileId.length() - 1; i++) {
                moveToDirPath.append(fileId.charAt(i))
                    .append(File.separator);
            }

            String newWikiType = String.format("%s%s", moveToDirPath.toString(), fileId);
            String newHtmlType = String.format("%s%s-h", moveToDirPath.toString(), fileId);
            String newTextType = String.format("%s%s-t", moveToDirPath.toString(), fileId);

            File moveDir = new File(moveToDirPath.toString());
            if (!moveDir.exists()) {
                moveDir.mkdirs();
            }
            
            File wikiFile = new File(wikiType);
            File htmlFile = new File(htmlType);
            File textFile = new File(textType);

            if (wikiFile.exists()) {
                logger.log(wikiType, newWikiType);
                wikiFile.renameTo(new File(newWikiType));
            }
            
            if (htmlFile.exists()) {
                logger.log(htmlType, newHtmlType);
                htmlFile.renameTo(new File(newHtmlType));
            }
            
            if (textFile.exists()) {
                logger.log(textType, newTextType);
                textFile.renameTo(new File(newTextType));
            }

            return true;
        }
        
    }

}
