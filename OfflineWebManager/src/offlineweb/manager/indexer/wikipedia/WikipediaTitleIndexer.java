
package offlineweb.manager.indexer.wikipedia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import offlineweb.manager.indexer.BaseIndexer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;


/**
 *
 * @author papa2
 */
public class WikipediaTitleIndexer extends BaseIndexer {
    
    private static final String TITLE = "title"; 

    /**
     *
     * @param indexDirPath
     * @param statusLoggerPath
     * @param indexedFileList
     */
    public WikipediaTitleIndexer(String indexDirPath, String statusLoggerPath, 
            List<String> indexedFileList) {
        super(indexDirPath, statusLoggerPath, indexedFileList);
        this.contentSource = "wikipedia";
    }
   
    /**
     *
     */
    @Override
    protected void start() {
        if (indexedFileList == null || indexedFileList.isEmpty()) {
            logStatus(INDEX_STATUS.FAILED, "No file to index");
            log("Indexing could not start, please provide a file to index");
            return;
        }
        try {
            Directory indexDir = FSDirectory.open(new File(indexDirPath));
            Set<String> stopWordSet = new HashSet<>();
            stopWordSet.add("_");
            indexWriter = new IndexWriter(indexDir,  
                    new IndexWriterConfig(Version.LUCENE_4_10_3, 
                    new StandardAnalyzer(CharArraySet.copy(stopWordSet))));
        } catch (IOException ex) {
           log("Failed to initiate indexing", ex.getMessage());
           cancelIndexing();
        }
        String indexedFilePath = this.indexedFileList.get(0);
        log("Indexing the file", indexedFilePath);
        BufferedReader indexedFileReader = null;
        try {
            indexedFileReader = new BufferedReader(new FileReader(indexedFilePath));
            String indexedLine = null;
            int lineCount = 0;
            try {
                while((indexedLine = indexedFileReader.readLine()) != null) {
                    lineCount++;
                    addToIndex(indexedLine);
                    if (lineCount % 1000 == 0) {
                        updateIndexing();
                    }
                }
                finishIndexing();
            } catch (IOException ex) {
                log("Something wrong with the line ", ex.getMessage());
            }
            
        } catch (FileNotFoundException ex) {
            log("Cannot find the file to index", ex.getMessage());
        } finally {
            if (indexedFileReader != null) {
                try {
                    indexedFileReader.close();
                } catch (IOException ex) {
                    // do nothing
                }
            }
        }
        
    }

    /**
     *
     * @param lastStatus
     */
    @Override
    protected void resume(String[] lastStatus) {
        // TODO : Think - what is to be done!
    }

    private void addToIndex(String indexedLine) {
        Document titleDoc = new Document();
        titleDoc.add(new TextField(TITLE, indexedLine, Field.Store.YES));     
        addDocumentToIndex(titleDoc);
    }

}
