
package offlineweb.manager.indexer.youtube;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import offlineweb.manager.indexer.BaseIndexer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
/**
 * Indexes Youtube video
 * @author papa2
 */
public class YoutubeDescriptionIndexer extends BaseIndexer {
    
    private static final String DESCRIPTION = "description";
    private static final String DOCKEY = "docKey";
    private static final String TITLEKEY = "titleKey";
    private static final String ABSTRACT = "abstract";

    public YoutubeDescriptionIndexer() {
        this.contentSource = "youtube";
        this.contentType = "video";
    }

    public YoutubeDescriptionIndexer(String indexDirPath, String statusLoggerPath, 
            List<String> indexedFileList) {
        super(indexDirPath, statusLoggerPath, indexedFileList);
        this.contentSource = "youtube";
        this.contentType = "video";
    }

    @Override
    protected void start() {
        File youtubeDescBaseDir = new File(this.indexedFileList.get(0));
        
        if (!youtubeDescBaseDir.exists() || !youtubeDescBaseDir.isDirectory()) {
            log("Invalid base youtube directory");
            logStatus(INDEX_STATUS.CANCELLED, "invalidBaseDir");
            return;
        }
        
        FilenameFilter youtubeDescFileFilter = new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.contains("youtube-desc-");
            }
        };
        
        File[] youtubeDescFiles = youtubeDescBaseDir.listFiles(youtubeDescFileFilter);
        
        if (youtubeDescFiles == null || youtubeDescFiles.length == 0) {
            log("No files with video description", this.indexedFileList.get(0));
            logStatus(INDEX_STATUS.CANCELLED, "emptyBaseDir");
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
        
        indexYoutubeDesc(youtubeDescFiles);
        finishIndexing();
    }

    @Override
    protected void resume(String[] lastStatus) {
        
    }

    private void indexYoutubeDesc(File[] youtubeDescFiles) {
        Document document = null;
        int indexCounter = 0;
        for (File descFile : youtubeDescFiles) {
            Map<String, String> descriptions = getDescs(descFile);
            
            if (descriptions == null || descriptions.isEmpty()) {
                log("file is not available", descFile.getAbsolutePath());
                continue;
            }
            
            document = new Document();
            document.add(new TextField(TITLE, descriptions.get(TITLE), Field.Store.YES));
            document.add(new StringField(TITLEKEY, descriptions.get(TITLE).replaceAll(" ", "").toLowerCase(), Field.Store.YES));
            document.add(new StringField(DOCKEY, descriptions.get(DOCKEY), Field.Store.YES));
            
            document.add(new TextField(CONTENT, descriptions.get(DESCRIPTION), Field.Store.NO));
            document.add(new StringField(ABSTRACT, descriptions.get(DESCRIPTION), Field.Store.YES));
            
            addDocumentToIndex(document);
            log("added document to", document.toString());
            if (indexCounter++ % UPDATE_INTERVAL == 0) {
                currentStatus = String.format("%s", descFile.getAbsolutePath());
                updateIndexing();
            }
        }
    }

    private Map<String, String> getDescs(File descFile) {
        BufferedReader descriptionReader = null;
        Map<String, String> descMap = null;
        try {
            descriptionReader = new BufferedReader(new FileReader(descFile));
            String descLine = null;
            descMap = new HashMap<>();
            String fileName = descFile.getName();
            fileName = fileName.replaceAll("youtube-desc-", "");
            descMap.put(DOCKEY, fileName);
            
            while ((descLine = descriptionReader.readLine()) != null) {
                if (descLine.startsWith(TITLE)) {
                    descMap.put(TITLE, descLine.replaceFirst(TITLE + "=", ""));
                } else if (descLine.startsWith(DESCRIPTION)) {
                    descMap.put(DESCRIPTION, descLine.replaceFirst(DESCRIPTION + "=", ""));
                }
                
            }
            
        } catch (FileNotFoundException ex) {
            log("can not read youtube description file", 
                    descFile.getAbsolutePath(), ex.getMessage());
        } catch (IOException ex) {
            log("can not read youtube description file", 
                    descFile.getAbsolutePath(), ex.getMessage());
        }
        
        return descMap;
    }
}
