
package offlineweb.job.batch.indexer;

import java.io.IOException;
import java.util.Map;
import java.util.Queue;
import offlineweb.job.batch.JobBatcher;
import offlineweb.job.common.Job;
import offlineweb.job.util.IndexerUtil;
import org.apache.lucene.index.IndexWriter;

/**
 *
 * @author papa
 */
public class IndexerJobBatcher extends JobBatcher {

    private IndexWriter indexWriter = null;
    @Override
    protected void doAfterInitBatcherConfig() {
       indexWriter = IndexerUtil.getIndexWriter(this.jobConfig.config("indexdir"));
       if (indexWriter == null) {
            logger.log("Index writer not created");
            logger.logStatus(Job.JobStatus.FAILED);
            System.exit(-1);
       }
    }

    @Override
    protected void doAfterParkJobsForNextShard(Queue<Map.Entry<String, String>> nextShard) {
        try {
            indexWriter.prepareCommit();
            indexWriter.commit();
            logger.log("Flushed shards", indexWriter.numDocs(), indexWriter.numRamDocs());
        } catch (IOException ex) {
            logger.log("Could add documents to index", ex.getMessage());
            logger.logStatus(Job.JobStatus.FAILED, "Could add documents to index");
        }
    }

    @Override
    protected void doAfterStartJobsOnQueue() {
        try {
            indexWriter.prepareCommit();
            indexWriter.commit();
            indexWriter.close();
        } catch (IOException ex) {
            
        }
    }
}
