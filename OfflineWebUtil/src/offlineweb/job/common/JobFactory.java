
package offlineweb.job.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import offlineweb.job.util.LoggerUtil;

/**
 *
 * @author papa2
 */
public class JobFactory {
    
    private static final LoggerUtil logger = LoggerUtil.getLoggerUtil(JobFactory.class);

    private JobFactory() {
    }
    
    public static Job getInstanceOf(Class<? extends Job> jobClass, JobConfig jobConfig, 
            Map.Entry<String, String> idEntry) {
        
        Job returnJob = null;
        try {
            returnJob = jobClass.newInstance();
            returnJob.setJobConfig(jobConfig);
            returnJob.setJobFileName(idEntry.getKey());
            returnJob.setJobFileId(idEntry.getValue());
        } catch (InstantiationException ex) {
            logger.log("Failed to load class", jobClass, ex);
        } catch (IllegalAccessException ex) {
            logger.log("Failed to load class", jobClass, ex);
        }
        return returnJob;
    }

    public static List<Job> getListOfInstanceOf(Class<? extends Job> jobClass, 
            JobConfig jobConfig, Queue<Map.Entry<String, String>> idEntries) {
        List<Job> jobs = new ArrayList<>();
        
        while (!idEntries.isEmpty()) {
            jobs.add(getInstanceOf(jobClass, jobConfig, idEntries.poll()));
        }
        
        return jobs;
    }
    
    public static List<JobRunner> getListOfJobRunner(Class<? extends Job> jobClass, 
            JobConfig jobConfig, Queue<Map.Entry<String, String>> idEntries) {
        List<JobRunner> jobRunners = new ArrayList<>();
        
        while (!idEntries.isEmpty()) {
            jobRunners.add(new JobRunner(getInstanceOf(jobClass, jobConfig, idEntries.poll())));
        }
        
        return jobRunners;
    }
}
