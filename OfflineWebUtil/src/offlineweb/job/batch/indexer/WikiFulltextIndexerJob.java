
package offlineweb.job.batch.indexer;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import offlineweb.job.batch.updater.WikiHTMLRepoJob;
import offlineweb.job.common.Job;
import offlineweb.job.util.FileNameUtil;
import offlineweb.job.util.IndexerUtil;
import offlineweb.job.util.LoggerUtil;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;

/**
 *
 * @author papa
 */
public class WikiFulltextIndexerJob extends Job {
    
    private static final String CONTENT_SOURCE_FIELD = "source";
    private static final String CONTENT_SOURCE = "wiki";
    private static final String CONTENT_TYPE_FIELD = "type";
    private static final String CONTENT_TYPE = "article";
    private static final String TITLE = "title";
    private static final String CONTENT = "content";
    private static final String DOCKEY = "docKey";
    private static final String TITLEKEY = "titleKey";
    private static final String ABSTRACT = "abstract";

    private static final LoggerUtil logger = LoggerUtil.getLoggerUtil(WikiHTMLRepoJob.class);
    @Override
    public boolean runJob() {
        logger.log("Running job with", toString());
        IndexWriter indexWriter = IndexerUtil.getIndexWriter(this.jobConfig.config("indexdir"));
        
        if (indexWriter == null) {
            logger.log("Index writer not found");
            return false;
        }
        
        String textContent = 
                IndexerUtil.readFile(FileNameUtil.getTextFileName(jobFileId, 
                        jobFileName, this.jobConfig.config("textdir")));
        
        if (textContent == null) {
            textContent = "";
        }
        //logger.log(toString(), textContent);
        Document wikiDoc = new Document();
        
        wikiDoc.add(new TextField(TITLE, jobFileName, Field.Store.YES));
        wikiDoc.add(new StringField(TITLEKEY, jobFileName.replaceAll(" ", "").toLowerCase(), Field.Store.YES));
        wikiDoc.add(new StringField(DOCKEY, jobFileId, Field.Store.YES));
        wikiDoc.add(new TextField(CONTENT, textContent, Field.Store.NO));
        
        if (textContent.length() >= 160) {
            wikiDoc.add(new StringField(ABSTRACT, textContent.substring(0, 160), Field.Store.YES));
        } else {
            wikiDoc.add(new StringField(ABSTRACT, textContent, Field.Store.YES));
        }
        
        wikiDoc.add(new StringField(CONTENT_SOURCE_FIELD, CONTENT_SOURCE, Field.Store.YES));
        wikiDoc.add(new StringField(CONTENT_TYPE_FIELD, CONTENT_TYPE, Field.Store.YES));
        
        try {
            indexWriter.addDocument(wikiDoc);
        } catch (IOException ex) {
            logger.log("could not add document to index", toString(), ex.getMessage());
            return false;
        }
        
        logger.log("Document added to index", toString());
        return true;
    }
    
}
