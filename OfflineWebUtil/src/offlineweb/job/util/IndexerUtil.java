package offlineweb.job.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import offlineweb.job.common.Job;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 *
 * @author papa
 */
public class IndexerUtil {
    
    private static final Map<String, IndexWriter> INDEX_WRITER_MAP = 
            new HashMap<>();

    public static String readFile(String filePath) {
        File txtFile = new File(filePath);
        return readFile(txtFile);
    }

    public static String readFile(File txtFile) {
        if (!txtFile.exists() || !txtFile.isFile()) {
            return null;
        }

        String content = null;
        BufferedReader contentReader = null;
        try {
            contentReader = new BufferedReader(new FileReader(txtFile));
            StringBuilder contentBuilder = new StringBuilder();

            String readLine = null;

            while ((readLine = contentReader.readLine()) != null) {
                contentBuilder.append(readLine);
                contentBuilder.append("\n");
            }

            content = contentBuilder.toString();
        } catch (FileNotFoundException ex) {
            // do nothing
        } catch (IOException ex) {
            // do nothing
        } finally {
            if (contentReader != null) {
                try {
                    contentReader.close();
                } catch (IOException ex) {
                    // do nothing
                }
            }
        }

        return content;
    }
    
    public static IndexWriter getIndexWriter(String indexDirPath) {
        if (INDEX_WRITER_MAP.containsKey(indexDirPath)) {
            return INDEX_WRITER_MAP.get(indexDirPath);
        }
        
        Directory indexDir = null;
        IndexWriter indexWriter = null;
        try {
            File fsIndexDir = new File(indexDirPath);
            
            if (!fsIndexDir.exists()) {
                fsIndexDir.mkdirs();
            }
            
            indexDir = FSDirectory.open(fsIndexDir);
            indexWriter = new IndexWriter(indexDir,
                    new IndexWriterConfig(Version.LUCENE_4_10_3, 
                    new StandardAnalyzer()));
            
            INDEX_WRITER_MAP.put(indexDirPath, indexWriter);
        } catch (IOException ex) {
            // do nothing
        }
        
        return indexWriter;
    }
}
