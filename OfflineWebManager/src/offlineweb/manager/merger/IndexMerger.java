
package offlineweb.manager.merger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import offlineweb.manager.util.LoggerUtil;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 * merges the indexes newly created or updated
 * @author papa2
 */
public class IndexMerger {

    private static final String TASK_OUTPUT_KEY = "index.task.out.%d";
    private static final String INDEX_OUTPUT_KEY = "index.complete.out";
    
    private static final LoggerUtil logger = 
            LoggerUtil.getLoggerUtil(IndexMerger.class, null);
    
    public void mergAllIndices(Properties mergeConfigs) {
        
        int taskIndex = 1;
        List<Directory> directories = new ArrayList<>();

        while(mergeConfigs.containsKey(String.format(TASK_OUTPUT_KEY, taskIndex))) {
            Directory directory;
            try {
                directory = FSDirectory.open(new File(mergeConfigs.getProperty(
                        String.format(TASK_OUTPUT_KEY, taskIndex))));
                directories.add(directory);
            } catch (IOException ex) {
                logger.log("can not open index directory" ,ex.getMessage());
                return;
            }
            taskIndex++;
        }
        
        try {    
            Directory mergedDirectory = FSDirectory.open(new File(mergeConfigs.
                    getProperty(INDEX_OUTPUT_KEY)));
            IndexWriter mergedIndexWriter = new IndexWriter(mergedDirectory,
                    new IndexWriterConfig(Version.LUCENE_4_10_3, new StandardAnalyzer()));
            
            mergedIndexWriter.addIndexes((Directory[]) directories.toArray());
            
            mergedIndexWriter.prepareCommit();
            mergedIndexWriter.commit();
            
            mergedIndexWriter.close();
        } catch (IOException ex) {
            logger.log("can not create merged index directory" ,ex.getMessage());
            return;
        }
        logger.log("Successfully merged different indices in one index");
        
    }
}
