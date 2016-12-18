
package offlineweb.manager.indexer;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import offlineweb.manager.util.LoggerUtil;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;

/**
 * Base indexing 
 * @author papa2
 */
public abstract class BaseIndexer {
    
    private static final String CONTENT_SOURCE_FIELD = "source";
    private static final String CONTENT_TYPE_FIELD = "type";
    protected static final String TITLE = "title";
    protected static final String CONTENT = "content";
    protected static final int UPDATE_INTERVAL = 200;

    /**
     * logger util object
     */
    protected LoggerUtil logger = null;

    /**
     * directory path where merged indices are stored
     */
    protected String indexDirPath = null;

    /**
     * lucene index writer
     */
    protected IndexWriter indexWriter = null;

    /**
     * status of current indexing process
     */
    protected String currentStatus = null;

    /**
     * list of files that is to be indexed
     */
    protected List<String> indexedFileList;

    /**
     * source of the content, valid values are 
     * 'wikipedia' 
     * 'gutenberg'
     * 'youtube'
     */
    protected String contentSource = null;
    
    /**
     * type of content, possible values are
     * 'article'
     * 'book'
     * 'video'
     */
    protected String contentType = null;


    /**
     * Possible statuses of the indexing process
     */
    public enum INDEX_STATUS {

        /**
         * started indexing
         */
        STARTED,

        /**
         * indexing continuing, updated at an interval
         */
        UPDATED,

        /**
         * paused, can be restarted
         */
        PAUSED,

        /**
         * restarted after being paused
         */
        RESTARTED,

        /**
         * process canceled by user or process, no new indices should be saved
         */
        CANCELLED,

        /**
         * finished successfully, without optimization
         */
        FINISHED,

        /**
         * failed, error messages should be reported
         */
        FAILED,

        /**
         * finished successfully, with optimization
         */
        SUCEEDED
    } 

    /**
     * no-args constructor for BaseIndexer
     * to aide instantiation through reflection
     */
    public BaseIndexer() {
        // default constructor to aide instantiation through reflection
    }

    /**
     * constructor for BaseIndexer
     *
     * @param indexDirPath directory where index is stored
     * @param statusLoggerPath log file path
     * @param indexedFileList list of files that is to be indexed 
     */
    public BaseIndexer(String indexDirPath, String statusLoggerPath, 
            List<String> indexedFileList) {
        this.logger = LoggerUtil.getLoggerUtil(this.getClass(), statusLoggerPath);
        this.indexDirPath = indexDirPath;
        this.indexedFileList = indexedFileList;
    } 
    
    /**
     * starts the indexing
     * @return true if indexing starts successfully
     *         false otherwise
     */
    public void startIndexing() {
        logStatus(INDEX_STATUS.STARTED, new Date().toString());
        try {
            start();
        } catch (Exception ex) {
            log("Start indexing failed", ex.getMessage());
        }
    }
    
    /**
     * crawls the index files and creates the index
     */
    protected abstract void start();
    
    /**
     * pauses the indexing process
     */
    public void pauseIndexing() {
        logStatus(INDEX_STATUS.PAUSED, "Indexing paused, can be restarted later.");
        updateIndexing();
        try {
            indexWriter.close();
        } catch (IOException ex) {
            log("Pausing indexing failed", ex.getMessage());
        }
    }
    
    /**
     * resumes the indexing, after a pause
     */
    public void resumeIndexing() {
        String[] lastStatus =logger.getLastStatusDetails();
        logStatus(INDEX_STATUS.RESTARTED, new Date().toString());
        resume(lastStatus);
    }
    
    /**
     * continues with resuming indexing process
     * @param lastStatus information regarding last time process was paused
     */
    protected abstract void resume(String[] lastStatus);
    
    /**
     * commits the changes made since last update
     */
    public void updateIndexing() {
        logStatus(INDEX_STATUS.UPDATED, currentStatus); 
        
        try {
            indexWriter.prepareCommit();
            indexWriter.commit();
            log("Updated index");
        } catch (IOException ex) {
            log("Index update failed", ex.getMessage());
        } catch (Exception ex) {
            log("Index update failed", ex.getMessage());
        }
    }
    
    /**
     * cancels the indexing process
     */
    public void cancelIndexing() {
        logStatus(INDEX_STATUS.CANCELLED, new Date().toString());
        try {
            indexWriter.rollback();
        } catch (IOException ex) {
            log("Indexing cancellation failed", ex.getMessage());
        } catch (Exception ex) {
            log("Indexing cancellation failed", ex.getMessage());
        }
    }
    
    /**
     * flushes all changes and closes the index
     */
    public void finishIndexing() {
        logStatus(INDEX_STATUS.FINISHED, new Date().toString());
        try {
            indexWriter.prepareCommit();
            indexWriter.commit();
            indexWriter.close();
        } catch (IOException ex) {
            log("Indexing completion failed", ex.getMessage());
        }
    }
    
    /**
     * adds a new document to the index
     * additionally adds the content source and type to the document
     * @param indexDocument
     */
    protected void addDocumentToIndex(Document indexDocument) {
        indexDocument.add(new StringField(CONTENT_SOURCE_FIELD, this.contentSource, Field.Store.YES));
        indexDocument.add(new StringField(CONTENT_TYPE_FIELD, this.contentType, Field.Store.YES));
        try {
            this.indexWriter.addDocument(indexDocument);
        } catch (Exception ex) {
            log("Adding document toindex", ex.getMessage());
        }       
    }
    
    /**
     * convenience method to log messages
     * @param messages 
     */
    protected void log(String... messages) {
        logger.log(messages);
    }

    /**
     * convenience method write status
     * @param index_status
     * @param currentStatus 
     */
    protected void logStatus(INDEX_STATUS index_status, String currentStatus) {
        logger.logStatus(index_status, currentStatus);
    }

    /**
     * sets the index directory
     * @param indexDirPath directory for storing the indices
     */
    public void setIndexDirPath(String indexDirPath) {
        this.indexDirPath = indexDirPath;
    }

    /**
     * sets the input files
     * @param indexedFileList input files set
     */
    public void setIndexedFileList(List<String> indexedFileList) {
        this.indexedFileList = indexedFileList;
    }

    /**
     * initiates the logger
     * @param statusLoggerPath status logger file
     */
    public void initiateLogger(String statusLoggerPath) {
        this.logger = LoggerUtil.getLoggerUtil(this.getClass(), statusLoggerPath);
    }
    
}
