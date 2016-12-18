package offlineweb.manager.crawler.wikipedia;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import offlineweb.manager.indexer.BaseIndexer;

import static offlineweb.manager.indexer.util.IndexerUtil.*;
import offlineweb.manager.util.LoggerUtil;

/**
 * Utility to download HTML and plain text version of all English (for now)
 * pages. Additionally it formats the link href and image source URLs to point
 * to application URL rather than Wikipedia related URL
 *
 * NB : Commented log statements are kept for debugging purpose
 *
 * @author papa
 */
public class WikipediaHtmlNTextCrawler {
    
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

    // api for html content
    private static final String HTML_API = "http://en.wikipedia.org/w/api.php?"
            + "format=json&action=parse&prop=text&page=";

    // api for text content
    private static final String TEXT_API = "http://en.wikipedia.org/w/api.php?"
            + "format=json&action=query&prop=extracts&explaintext="
            + "&titles=";

    // max no of jobs invoked before a executor shutsdown, 
    // necessary to prevent OutOfMemory error 
    private static final int MAX_WORKER = 200;

    // wait time, necessary to let the thread to complete execution
    private static final int WAIT_TIME = 15;

    private static final String WIKI_LOG
            = String.format(WIKI_CRAWL_PROPS.getProperty("wiki.status.log"), 
                    (WIKI_CRAWL_PROPS.getProperty("wiki.crwal.size", "").isEmpty()) ?
                            WIKI_CRAWL_PROPS.getProperty("wiki.crwal.start") :
                            WIKI_CRAWL_PROPS.getProperty("wiki.crwal.size", "").toUpperCase());

    private static final LoggerUtil logger
            = LoggerUtil.getLoggerUtil(WikipediaHtmlNTextCrawler.class, WIKI_LOG);

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        // start crawling
        crawlContentMapDir();
    }

    private static void crawlContentMapDir() {

        File[] idMapperFiles = 
                (WIKI_CRAWL_PROPS.getProperty("wiki.crwal.size", "").isEmpty()) ?
                findMapperFiles(MAP_DIR, new String[]{WIKI_CRAWL_PROPS.getProperty("wiki.crwal.start")}) :
                findMapperFiles(MAP_DIR, FSIZE.valueOf(WIKI_CRAWL_PROPS.getProperty("wiki.crwal.size").toUpperCase()), null); 
                // going character by character

        if (idMapperFiles == null || idMapperFiles.length == 0) {
            logger.log("No mapping files");
            return;
        }

        // start crawler for html and text
        crawlForHtmlAndText(idMapperFiles);
    }

    /**
     * executes the crawler with valid mapping files
     *
     * @param idMapperFiles list of name-to-id files
     */
    private static void crawlForHtmlAndText(File[] idMapperFiles) {

        for (File idMapFile : idMapperFiles) {
            logger.log("Starting : ", idMapFile.getAbsolutePath());
            logger.logStatus(BaseIndexer.INDEX_STATUS.STARTED, idMapFile.getAbsolutePath());
            Queue<Map.Entry<String, String>> idMappings = getIdMapping(idMapFile);

            if (!idMappings.isEmpty() && idMappings.size() != 0) {
                logger.log("Total docs", idMappings.size());
                executeForMapFile(idMappings);
            }
            logger.log("Completed : ", idMapFile.getAbsolutePath());
            logger.logStatus(BaseIndexer.INDEX_STATUS.FINISHED, idMapFile.getAbsolutePath());
        }
    }

    /**
     * manages 2 thread executors, one for html other for text files
     *
     * @param idMappings name-to-id mappings for a mapping file/first character
     */
    private static void executeForMapFile(Queue<Map.Entry<String, String>> idMappings) {
        if (idMappings.isEmpty() || idMappings.size() == 0) {
            return;
        }

        int taskCounter = 0; // no. of tasks executed
        ExecutorService htmlParserService = Executors.newSingleThreadExecutor(); // for storing htmls
        ExecutorService textParserService = Executors.newSingleThreadExecutor(); // for storing texts

        List<Callable<Boolean>> htmlParsers = new ArrayList<>(); // list of html task thread
        List<Callable<Boolean>> textParsers = new ArrayList<>(); // list of text task thread

        while (!idMappings.isEmpty()) {

            Map.Entry<String, String> idEntry = idMappings.poll(); //

            if (idEntry != null) {
                htmlParsers.add(new HtmlParserThread(idEntry.getKey(), idEntry.getValue()));
                textParsers.add(new TextParserThread(idEntry.getKey(), idEntry.getValue()));
            }

            taskCounter = htmlParsers.size();

            if (taskCounter % MAX_WORKER == 0 || idMappings.isEmpty()) {
                startExecutorService(htmlParserService, htmlParsers);
                logger.log("HTMLs are saved", taskCounter + "");

                startExecutorService(textParserService, textParsers);
                logger.log("TEXTs are saved", taskCounter + "");

                htmlParserService = Executors.newSingleThreadExecutor();
                textParserService = Executors.newSingleThreadExecutor();
                htmlParsers.clear();
                textParsers.clear();
                logger.log("ROLLING OVER");
                logger.logStatus(BaseIndexer.INDEX_STATUS.UPDATED, taskCounter);
            }
        }
    }

    private static void startExecutorService(ExecutorService executorService,
            List<Callable<Boolean>> contentparsers) {
        try {
            if (executorService != null) {
                executorService.invokeAll(contentparsers);
                executorService.shutdown();
                executorService.awaitTermination(WAIT_TIME, TimeUnit.MINUTES);
            }
        } catch (InterruptedException ex) {
            // do nothing
        }

    }

    static class ParserBase {

        final String fileName;
        final String fileId;
        String contentUrl;
        String filePath;

        ParserBase(String fileName, String fileId,
                String contentUrl, char fileType) {
            this.fileName = fileName;
            this.fileId = fileId;
            try {
                this.contentUrl = contentUrl + URLEncoder.encode(fileName, "UTF-8");
            } catch (Exception ex) {
                this.contentUrl = null;
            }
            
            char firstChar = this.fileName.charAt(0);
            this.filePath = new StringBuilder(CONTENT_DIR)
                    .append(File.separator)
                    .append(firstChar)
                    .append(File.separator)
                    .append(fileId)
                    .append("-")
                    .append(fileType).toString();
        }

        String getContent() {
            if (this.contentUrl == null) {
                logger.log(fileName, " Could not be encoded");
                return null;
            }
            String content = null;
            BufferedReader contentReader = null;
            try {
                logger.log(this.contentUrl);

                URL contentURL = new URL(this.contentUrl);
                URLConnection apiConnection = contentURL.openConnection();
                apiConnection.setReadTimeout(WAIT_TIME * 1000);
                apiConnection.connect();

                contentReader = new BufferedReader(
                        new InputStreamReader(apiConnection.getInputStream()));

                String readLine = null;
                StringBuilder contentBuilder = new StringBuilder();

                int counter = 0;
                while (counter++ < 5) {
                    if (((HttpURLConnection) apiConnection).getResponseCode() == 200) {
                        //logger.log("READING STARTS----------------------------");
                        while ((readLine = contentReader.readLine()) != null) {
                            contentBuilder.append(readLine);
                        }
                        //logger.log("READING ENDS----------------------------");
                        break;
                    } else {
                        try {
                            //logger.log("READING SLEEPS----------------------------");
                            Thread.sleep(5 * 1000);
                        } catch (InterruptedException ex) {
                            // do nothing
                        }
                    } 
                    
                    
                }
                content = contentBuilder.toString();
            } catch (MalformedURLException ex) {
                logger.log("FAILED : ", fileName, "##", fileId);
            } catch (IOException ex) {
                logger.log("FAILED : ", fileName, "##", fileId);
            } finally {
                if (contentReader != null) {
                    try {
                        contentReader.close();
                    } catch (IOException ex) {
                        // do nothing
                    }
                }
            }
            //logger.log(content.length() + "");
            return content;
        }

        boolean isExistingFile() {
            File saveFile = new File(this.filePath);
            return saveFile.exists();
        }
    }

    static class HtmlParserThread implements Callable<Boolean> {

        private final ParserBase parserBase;

        public HtmlParserThread(String fileName, String fileId) {
            this.parserBase = new ParserBase(fileName, fileId,
                    HTML_API, 'h');
        }

        @Override
        public Boolean call() {
            logger.log(parserBase.fileName, "#", parserBase.fileId);
            parseAndSaveHtmlContent();
            return true;
        }

        private void parseAndSaveHtmlContent() {

            if (parserBase.isExistingFile()) {
                logger.log("No more parsing, file exists!");
                return;
            }
            String htmlContent = getValuefOfKey();

            if (htmlContent == null || htmlContent.length() == 0) {
                logger.log("No HTML content for :: ", parserBase.fileName, "#", parserBase.fileId);
                return;
            }

            htmlContent = htmlContent.replaceAll("wiki/File:", "/wikipedia/image?f=File:");

            String imagePatternType
                    = "((https:|http:)?\\/\\/upload\\.wikimedia\\.org\\/wikipedia"
                    + "\\/(commons|en)\\/(thumb)?\\/[a-z0-9]\\/[a-z0-9]{2})";
            htmlContent = htmlContent.replaceAll(imagePatternType, "/wikipedia/image?f=File:");
            htmlContent = htmlContent.replaceAll("\\/wiki\\/", "/wikipedia/fetch?t=");

            BufferedWriter htmlWriter = null;
            try {
                htmlWriter = new BufferedWriter(new FileWriter(parserBase.filePath));
                htmlWriter.write(htmlContent);
                logger.log("WRITEN : ", parserBase.filePath);
            } catch (IOException ex) {
                logger.log("WRITE FAIL : ", parserBase.fileName);
            } finally {
                if (htmlWriter != null) {
                    try {
                        htmlWriter.flush();
                        htmlWriter.close();
                    } catch (IOException ex) {
                        logger.log("FAILED WRITE : ", parserBase.fileName);
                    }
                }
            }
        }

        String getValuefOfKey() {
            String jsonContent = parserBase.getContent();
            if (jsonContent == null || jsonContent.length() == 0) {
                logger.log("No content for "
                        , parserBase.fileName, "#", parserBase.fileId);
                return null;
            }
            Gson contentParser = new Gson();
            LinkedTreeMap<?, ?> contentMap = contentParser.fromJson(jsonContent,
                    LinkedTreeMap.class);

            //logger.info(contentMap.toString());
            String jsonValue = null;
            Map tempMap = null;
            try {
                tempMap = (Map) contentMap.get("parse");
                tempMap = (Map) tempMap.get("text");
                jsonValue = (String) tempMap.get("*");
            } catch (Exception ex) {
                logger.log("FAIL JSON : ", parserBase.fileName, "#", parserBase.fileId);
            }

            //logger.info(jsonValue);
            //logger.info("==========================");
            return jsonValue;
        }

    }

    static class TextParserThread implements Callable<Boolean> {

        private final ParserBase parserBase;

        public TextParserThread(String fileName, String fileId) {
            this.parserBase = new ParserBase(fileName, fileId,
                    TEXT_API, 't');
        }

        @Override
        public Boolean call() {
            logger.log( parserBase.fileName,"#", parserBase.fileId);
            parseAndSaveTextContent();
            return true;
        }

        private void parseAndSaveTextContent() {
            if (parserBase.isExistingFile()) {
                logger.log( "No more parsing, file exists!");
                return;
            }

            String textContent = getValuefOfKey();

            if (textContent == null || textContent.length() == 0) {
                logger.log("No TEXT content for :: ", parserBase.fileName, "#", parserBase.fileId);
                return;
            }

            BufferedWriter textWriter = null;
            try {
                textWriter = new BufferedWriter(new FileWriter(parserBase.filePath));
                textWriter.write(textContent);
                logger.log("WRITEN : " , parserBase.filePath);
            } catch (IOException ex) {
                logger.log("WRITE : " , parserBase.fileName);
            } finally {
                if (textWriter != null) {
                    try {
                        textWriter.flush();
                        textWriter.close();
                    } catch (IOException ex) {
                        logger.log("FAILED WRITE : ", parserBase.fileName);
                    }
                }
            }
        }

        String getValuefOfKey() {
            String jsonContent = parserBase.getContent();
            if (jsonContent == null || jsonContent.length() == 0) {
                logger.log("  No content for " , parserBase.fileName , "#" , parserBase.fileId);
                return null;
            }
            Gson contentParser = new Gson();
            LinkedTreeMap<?, ?> contentMap = contentParser.fromJson(jsonContent,
                    LinkedTreeMap.class);

            //logger.info(contentMap.toString());
            String jsonValue = null;
            Map tempMap = null;
            Object tempObject = null;
            try {

                tempMap = (Map) contentMap.get("query");
                tempMap = (Map) tempMap.get("pages");
                tempObject = tempMap.values().toArray()[0];

                if (tempObject instanceof Map) {
                    tempMap = (Map) tempObject;
                    if (tempMap.containsKey("extract")) {
                        jsonValue = (String) tempMap.get("extract");
                    }
                }

                tempMap = null;
                tempObject = null;
            } catch (Exception ex) {
                logger.log("FAIL JSON : " , parserBase.fileName , "#" , parserBase.fileId);
            }

            //logger.info(jsonValue);
            //logger.info("==========================");
            return jsonValue;
        }
    }
}
