package offlineweb.job.batch;

import offlineweb.job.common.JobFactory;
import offlineweb.job.common.JobConfig;
import offlineweb.job.common.Job;
import java.io.File;
import java.io.FileFilter;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import offlineweb.job.common.JobSharder;
import offlineweb.job.util.LoggerUtil;

/**
 *
 * @author papa2
 */
public class JobBatcher {

    protected int maxBatchedTasks = 0;
    protected int maxWaitTime = 0;
    protected JobConfig jobConfig = null;
    protected Class<? extends Job> jobClass = null;
    protected Queue<JobSharder> jobSharderQueue = null;

    protected LoggerUtil logger;

    public JobBatcher() {
    }

    public void setJobClass(Class<? extends Job> jobClass) {
        this.jobClass = jobClass;
    }

    public void setJobConfig(JobConfig jobConfig) {
        this.jobConfig = jobConfig;
        initBatcherConfig();
    }

    public void initJobConfig() {
        setJobConfig(JobConfig.load());
    }
    
    public void initJobConfig(String configPath) {
        setJobConfig(JobConfig.load(configPath));
    }

    public void runJobBatch() {
        
        Queue<String> mapperFileQueue = initMapperFiles();
        if (mapperFileQueue == null) {
            logger.log("Could not initiate mapper files");
            //logger.logStatus(Job.JobStatus.FAILED, "Error in mapper files");
            return;
        }

        initSharderQueue(mapperFileQueue);
        mapperFileQueue = null;
        startJobsOnQueue();

    }

    private void initSharderQueue(Queue<String> mapperFileQueue) {
        this.jobSharderQueue = new LinkedList<>();
        while (!mapperFileQueue.isEmpty()) {
            try {
                this.jobSharderQueue.add(new JobSharder(maxBatchedTasks,
                        mapperFileQueue.poll()));
            } catch (Exception ex) {
                logger.log("Error at sharder creation", ex.getMessage());
            }
        }
        
        doAfterInitSharderQueue(mapperFileQueue);
    }
    
    protected void doAfterInitSharderQueue(Queue<String> mapperFileQueue) {
        // may be
    }

    private void initBatcherConfig() {
        this.maxBatchedTasks = Integer.parseInt(this.jobConfig.config("batcher", "maxjob"));
        this.maxWaitTime = Integer.parseInt(this.jobConfig.config("batcher", "waittime"));
        
        logger = LoggerUtil.getLoggerUtil(this.getClass(),
                this.jobConfig.config("logdir") 
                        + File.separator 
                        + JobBatcher.class.getName() 
                        + "-batcher.log");
        
        doAfterInitBatcherConfig();
    }
    
    protected void doAfterInitBatcherConfig() {
        // may be
    }

    private Queue<String> initMapperFiles() {
        String mapperDirpath = this.jobConfig.config("mapperdir");
        String mapperFileNames = this.jobConfig.config("mappers");

        File mapperDir = new File(mapperDirpath);
        if (!mapperDir.exists() || !mapperDir.isDirectory()) {
            logger.log("Invalid mapper directory");
            return null;
        }

        final Queue<String> mapperFiles = new LinkedList<>();
        if ("".equals(mapperFileNames)) {
            logger.log("No mapper specified");
            return null;
        } 
        
        if ("all".equals(mapperFileNames)) {
            File[] childMappers = mapperDir.listFiles();
            for (File childMapper : childMappers) {
                mapperFiles.add(childMapper.getAbsolutePath());
            }
        } else {
            for (String mapperFile : mapperFileNames.split(";")) {
                if (!"".equals(mapperFile.trim())) {
                    mapperFiles.add(mapperDirpath + File.separator + "nametoid-" + mapperFile.trim());
                }
            }
        }
        
        return mapperFiles;
    }

    private void startJobsOnQueue() {
        logger.log("Starting jobs with queue of", this.jobSharderQueue.size());

        while (!this.jobSharderQueue.isEmpty()) {
            try {
                parkJobsForSharder(this.jobSharderQueue.poll());
            } catch (Exception ex) {
                logger.log("Error while parking job for the shard", ex.getMessage());
            }
        }
        doAfterStartJobsOnQueue();
        logger.log("Done with this queue of jobs");
    }
    
    protected void doAfterStartJobsOnQueue() {
        
    }

    private void parkJobsForSharder(JobSharder jobSharder) {
        while (jobSharder.isAlive()) {
            logger.log("Parking jobs for", jobSharder);
            //logger.logStatus(Job.JobStatus.COMMENCED, jobSharder);
            try {
                parkJobsForNextShard(jobSharder.nextShard());
                logger.log("Done batching shard for", jobSharder);
                //logger.logStatus(Job.JobStatus.UPDATED, jobSharder);
            } catch (Exception ex) {
                ex.printStackTrace();
                logger.log("Error while parking job for the new shard", jobSharder, ex.getMessage());
            }

        }
        doAfterParkJobsForSharder(jobSharder);
        logger.log("Done jobs for", jobSharder);
        //logger.logStatus(Job.JobStatus.SUCEEDED, jobSharder);
    }
    
    protected void doAfterParkJobsForSharder(JobSharder jobSharder) {
        
    }

    private void parkJobsForNextShard(Queue<Map.Entry<String, String>> nextShard) {
        if (nextShard == null || nextShard.size() == 0) {
            logger.log("Empty shard, quitting job");
            //logger.logStatus(Job.JobStatus.UPDATED, "Empty shard");
            return;
        }

        logger.log("Execution of jobs for ", nextShard.size());
        //logger.logStatus(Job.JobStatus.UPDATED, "Started with", nextShard.size());
        
        ExecutorService jobExecutor = null;
        try {
            jobExecutor = Executors.newFixedThreadPool(nextShard.size());
            jobExecutor.invokeAll(JobFactory.getListOfJobRunner(jobClass, jobConfig, nextShard));
            jobExecutor.shutdown();
            jobExecutor.awaitTermination(maxWaitTime, TimeUnit.SECONDS);
            //logger.logStatus(Job.JobStatus.UPDATED, "Finished");
        } catch (InterruptedException ex) {
            logger.log("Parking jobs for the shard failed", ex.getMessage());
            //logger.logStatus(Job.JobStatus.FAILED, "Failed");
            terminateExecutor(jobExecutor);
        } catch (Exception ex) {
            logger.log("Parking jobs for the shard failed", ex.getMessage());
            //logger.logStatus(Job.JobStatus.FAILED, "Failed");
            terminateExecutor(jobExecutor);
        }
        
        doAfterParkJobsForNextShard(nextShard);

        // if the job takes a lot resource it makes sense to add a bit of wait to 
        // let the threads to finish.
        if ("yes|true".contains(this.jobConfig.config("batcher", "towait").toLowerCase())) {
            try {
                // waiting for all to finish
                Thread.sleep(Long.parseLong(this.jobConfig.config("batcher", "addwait")));
            } catch (InterruptedException ex) {
                // nothing to do here
            }
        }

    }
    
    protected void doAfterParkJobsForNextShard(Queue<Map.Entry<String, String>> nextShard) {
        
    }


    private void terminateExecutor(ExecutorService jobExecutor) {
        try {
            jobExecutor.shutdownNow();
        } catch (Exception e) {
            // do nothing
        }
    }

}
