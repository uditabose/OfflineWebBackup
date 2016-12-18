
package offlineweb.manager.indexer.gutenberg;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import offlineweb.manager.indexer.BaseIndexer;
import static offlineweb.manager.indexer.util.IndexerUtil.*;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
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
public class GutenbergIndexer extends BaseIndexer {

    private static final String DOCKEY = "docKey";
    private static final String TITLEKEY = "titleKey";
    private static final String ABSTRACT = "abstract";
    
    public GutenbergIndexer() {
        super();
        this.contentSource = "gutenberg";
        this.contentType = "book";
    }

    public GutenbergIndexer(String indexDirPath, String statusLoggerPath, List<String> indexedFileList) {
        super(indexDirPath, statusLoggerPath, indexedFileList);
        this.contentSource = "gutenberg";
        this.contentType = "book";
    }

    @Override
    protected void start() {
        File[] idMapperFile = findMapperFiles(indexedFileList.get(0));
        
        if (idMapperFile == null || idMapperFile.length == 0 ) {
            log("No mapping files");
            logStatus(INDEX_STATUS.CANCELLED, "invalidMapperFile");
            return;
        }
        
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
        
        indexFullText(idMapperFile[0]);
        finishIndexing();
    }

    @Override
    protected void resume(String[] lastStatus) {
        
    }

    private void indexFullText(File idMapperFile) {
        Queue<Map.Entry<String, String>> idMappings = getIdMapping(idMapperFile);
        
        Document document = null;
        int indexCounter = 0;
        while (!idMappings.isEmpty()) {
            Map.Entry<String, String> idEntry = idMappings.poll();
            String fileSubPath = getFilePath(idEntry.getValue());
            log(" indexing : ", fileSubPath);
            String content = readFile(fileSubPath);
            if (content == null || content.length() == 0) {
                log("file is not available", idEntry.getKey(), 
                    idEntry.getValue());
                continue;
            }
            document = new Document();
            document.add(new TextField(TITLE, idEntry.getKey(), Field.Store.YES));
            document.add(new StringField(TITLEKEY, idEntry.getKey().replaceAll(" ", "").toLowerCase(), Field.Store.YES));
            document.add(new StringField(DOCKEY, idEntry.getValue(), Field.Store.YES));
            
            document.add(new TextField(CONTENT, content, Field.Store.NO));
            if (content.length() >= 500) {
                document.add(new StringField(ABSTRACT, content.substring(500, 660), Field.Store.YES));
            } else if(content.length() > 200 && content.length() <= 360) { 
                document.add(new StringField(ABSTRACT, content.substring(200, 360), Field.Store.YES));
            } else {
                document.add(new StringField(ABSTRACT, content, Field.Store.YES));
            }
            
            
            addDocumentToIndex(document); 
            document = null;
            
            if (indexCounter++ % UPDATE_INTERVAL == 0) {
                currentStatus = String.format("%s#%s", 
                        idEntry.getKey(), idEntry.getValue());
                updateIndexing();
            }

        }
    }

    private String getFilePath(String fileId) {
        StringBuilder subDirPath = new StringBuilder(indexedFileList.get(0))
                .append("/txt/www.gutenberg.lib.md.us")
                .append(File.separator);
        for (int i = 0; i < fileId.length() - 1; i++) {
            subDirPath.append(fileId.charAt(i)).append(File.separator);
        }
        subDirPath.append(fileId)
            .append(File.separator)
            .append(fileId)
            .append(".txt");
        return subDirPath.toString();
    }

}
