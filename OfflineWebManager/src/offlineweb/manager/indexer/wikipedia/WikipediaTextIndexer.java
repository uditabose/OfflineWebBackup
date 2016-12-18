package offlineweb.manager.indexer.wikipedia;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import offlineweb.manager.indexer.BaseIndexer;
import static offlineweb.manager.indexer.util.IndexerUtil.*;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 * full-text indexing of Wikipedia articles
 *
 * @author papa2
 */
public class WikipediaTextIndexer extends BaseIndexer {

    private static final int WAIT_TIME = 5;

    private File contentDir;

    public WikipediaTextIndexer() {
        super();
        this.contentSource = "wikipedia";
        this.contentType = "article";
    }

    public WikipediaTextIndexer(String indexDirPath, String statusLoggerPath,
            List<String> indexedFileList) {
        super(indexDirPath, statusLoggerPath, indexedFileList);
        this.contentSource = "wikipedia";
        this.contentType = "article";
    }

    @Override
    protected void start() {
        File[] idMapperFiles = findMapperFiles(indexedFileList.get(0),
                //FSIZE.SMALL, null);
        new String[]{"D", "R", "L", "B"});

        if (idMapperFiles == null || idMapperFiles.length == 0) {
            log("No mapping files");
            logStatus(INDEX_STATUS.CANCELLED, "invalidMapperDir");
            return;
        }

        File tempDir = new File(indexedFileList.get(0));
        tempDir = tempDir.getParentFile();

        if (!tempDir.exists()) {
            log("Content directory not found");
            logStatus(INDEX_STATUS.CANCELLED, "invalidContentDir");
            return;
        }

        tempDir = new File(tempDir, "pages");
        if (!tempDir.exists()) {
            log("Content directory not found", tempDir.getName());
            logStatus(INDEX_STATUS.CANCELLED, "invalidContentDir");
            return;
        }

        this.contentDir = tempDir;

        Directory indexDir;
        try {
            indexDir = FSDirectory.open(new File(indexDirPath));
            this.indexWriter = new IndexWriter(indexDir,
                    new IndexWriterConfig(Version.LUCENE_4_10_3, new StandardAnalyzer()));
        } catch (IOException ex) {
            log("Can not open the index directory", this.indexDirPath);
            logStatus(INDEX_STATUS.CANCELLED, "invalidIndexDir");
            return;
        }

        indexFullText(idMapperFiles);
        finishIndexing();
    }

    @Override
    protected void resume(String[] lastStatus) {

    }

    private void indexFullText(File[] idMapperFiles) {
        log("Initiating full text search");
        for (File idMapFile : idMapperFiles) {
            log("Indexing Started", idMapFile.getAbsolutePath());
            Queue<Map.Entry<String, String>> idMappings = getIdMapping(idMapFile);

            if (idMappings.isEmpty() || idMappings.size() == 0) {
                log("No mapping to index", idMapFile.getAbsolutePath());
                continue;
            }

            indexTexts(idMappings);
            this.currentStatus = idMapFile.getAbsolutePath();
            updateIndexing();
        }
    }

    private void indexTexts(Queue<Map.Entry<String, String>> idMappings) {
        int indexCounter = 0;

        log("Total docs", idMappings.size() + "");
        ExecutorService indexUpdaterService = Executors.newSingleThreadExecutor();

        List<Callable<Boolean>> indexerList = new ArrayList<>();
        
        while (!idMappings.isEmpty()) {

            indexerList.add(new IndexUpdater(idMappings.poll()));

            if (indexCounter++ % UPDATE_INTERVAL == 0) {
                try {
                    indexUpdaterService.invokeAll(indexerList);
                    indexUpdaterService.shutdown();
                    indexUpdaterService.awaitTermination(WAIT_TIME, TimeUnit.MINUTES);

                } catch (InterruptedException ex) {
                    log("Indexing failed", ex.getMessage());
                }

                try {
                    Thread.sleep(WAIT_TIME * 1000); // just for some relief
                } catch (InterruptedException ex) {
                    // do nothing
                }
                
                updateIndexing();
                indexerList.clear();
                indexUpdaterService = Executors.newSingleThreadExecutor();
            }
        }
    }

    class IndexUpdater implements Callable<Boolean> {

        private final Map.Entry<String, String> idMapping;

        public IndexUpdater(Map.Entry<String, String> idMapping) {
            this.idMapping = idMapping;
        }

        @Override
        public Boolean call() throws Exception {
            String title = idMapping.getKey();
            String wId = idMapping.getValue();

            String content = getWikiText(title, wId);

            if (content == null || content.length() == 0) {
                // even if no content is available the document title is indexed
                log("file content is not available", title, wId);
                content = "";
            } else {
                // just DEBUG
                log("Available", title, wId);
            }

            Document document = new Document();
            document.add(new TextField(TITLE, title, Field.Store.YES));
            document.add(new TextField(CONTENT, content, Field.Store.NO));

            addDocumentToIndex(document);
            currentStatus = String.format("%s#%s", title, wId);
            document = null;
            return true;
        }

        private String getWikiText(String fileName, String fileId) {
            char firstChar = fileName.charAt(0);
            String txtFilePath = new StringBuilder(contentDir.getAbsolutePath())
                    .append(File.separator)
                    .append(firstChar)
                    .append(File.separator)
                    .append(fileId)
                    .append("-t").toString();

            String content = readFile(txtFilePath);
            return content;
        }
    }
}
