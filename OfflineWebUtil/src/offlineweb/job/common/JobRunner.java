package offlineweb.job.common;

import offlineweb.job.common.Job;
import java.util.concurrent.Callable;
import offlineweb.job.util.LoggerUtil;

/**
 *
 * @author papa2
 */
public class JobRunner implements Callable<String> {

    private Job job;
    private static final LoggerUtil logger = LoggerUtil.getLoggerUtil(JobRunner.class);

    public JobRunner(Job job) {
        this.job = job;
    }

    @Override
    public String call() {
        try {
            if (!job.runJob()) {
                logger.log("Job could not run", job);
            } 
        } catch (Exception ex) {
            logger.log("Raised exception while running job", ex.getMessage());
        }
        
        return job.toString();
    }

}
