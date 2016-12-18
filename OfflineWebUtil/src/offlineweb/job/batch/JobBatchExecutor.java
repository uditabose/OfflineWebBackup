
package offlineweb.job.batch;

import java.util.HashMap;
import java.util.Map;
import offlineweb.job.batch.indexer.WikiFulltextIndexerJob;
import offlineweb.job.batch.indexer.IndexerJobBatcher;
import offlineweb.job.batch.singlerun.GutenEpubMover;
import offlineweb.job.batch.singlerun.GutenImageProcessorJob;
import offlineweb.job.batch.singlerun.GutenTextUnarchiver;
import offlineweb.job.batch.singlerun.TextFileMoverJob;
import offlineweb.job.batch.updater.WikiHTMLRepoJob;
import offlineweb.job.batch.updater.WikiUnavailablePageJob;

/**
 *
 * @author papa2
 */
public class JobBatchExecutor {
    
    private static final Map<String, Class> JOB_KEY = new HashMap<>();
    private static final Map<String, Class> BATCHER_KEY = new HashMap<>();
    
    static {
        JOB_KEY.put("wiki-update", WikiHTMLRepoJob.class);
        JOB_KEY.put("wiki-unavailable", WikiUnavailablePageJob.class);
        JOB_KEY.put("temp-text-move", TextFileMoverJob.class);
        JOB_KEY.put("wiki-index", WikiFulltextIndexerJob.class);
        JOB_KEY.put("guten-image", GutenImageProcessorJob.class);
        JOB_KEY.put("guten-epub", GutenEpubMover.class);
        JOB_KEY.put("guten-unarchive", GutenTextUnarchiver.class);
        
        BATCHER_KEY.put("wiki-update", JobBatcher.class);
        BATCHER_KEY.put("wiki-unavailable", JobBatcher.class);
        BATCHER_KEY.put("temp-text-move", JobBatcher.class);
        BATCHER_KEY.put("guten-image", JobBatcher.class);
        BATCHER_KEY.put("wiki-index", IndexerJobBatcher.class);
        BATCHER_KEY.put("guten-epub", JobBatcher.class);
        BATCHER_KEY.put("guten-unarchive", JobBatcher.class);
    }
    
    public static void main(String[] args) throws InstantiationException, IllegalAccessException {
        
        if (args == null || args.length < 1) {
            System.out.println("Wrong number of arguements");
        }
        
        Class batcherClass = BATCHER_KEY.get(args[0]);
        JobBatcher jobBatcher = (JobBatcher) batcherClass.newInstance();
        jobBatcher.setJobClass(JOB_KEY.get(args[0]));
        
        if (args.length > 1) {
            jobBatcher.initJobConfig(args[1]);
        } else {
            jobBatcher.initJobConfig();
        }
        
        jobBatcher.runJobBatch();
    }

}
