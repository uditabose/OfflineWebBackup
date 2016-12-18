package offlineweb.job.common;

import offlineweb.job.util.LoggerUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * This class will shard each nametoid-%s files by maximum 
 * line number till the end of file
 * 
 * @author papa2
 */

public class JobSharder {
    
    private static final LoggerUtil logger = LoggerUtil.getLoggerUtil(JobSharder.class);

    private final int shardAt; // maximum line numbers to shard the file at
    private final String jobInitFilePath; // nametoid-%s file path
    
    private final Queue<String> jobContent; // the init file content by line
    private ShardStatus jobShardStatus; // status of the sharding
    private int shardCount = 0;


    /**
     * Status of the sharding
     */
    public enum ShardStatus {

        /**
         * it's still not the EOF or canceled
         */
        Alive,

        /**
         * EOF or canceled
         */
        Done
    }

    /**
     * constructor to initiate sharder
     * @param shardAt
     * @param jobInitFilePath
     */
    public JobSharder(int shardAt, String jobInitFilePath) {
        this.shardAt = shardAt;
        this.jobInitFilePath = jobInitFilePath;
        this.jobShardStatus = ShardStatus.Alive;
        this.jobContent = new LinkedList<>();
    }

    /**
     * if not EOF, then send back next chunk of job
     * @return
     */
    public Queue<Map.Entry<String, String>> nextShard() {
        if (this.jobShardStatus == ShardStatus.Done) {
            return null;
        }
        
        if (jobContent.isEmpty()) {
            initSharder();
        }

        Queue<Map.Entry<String, String>> shardQueue = new LinkedList<>();
        String idLine = null;
        Map.Entry<String, String> idEntry = null;
        int shardCnt = 0;
        
        while (shardCnt++ < this.shardAt && !jobContent.isEmpty()) {
            idLine = jobContent.poll();
            int lastEqualIdx = idLine.lastIndexOf("=");
            if (lastEqualIdx >= 0) {
                idEntry = new AbstractMap.SimpleEntry<>(idLine.substring(0, lastEqualIdx),
                        idLine.substring(lastEqualIdx + 1));
                shardQueue.add(idEntry);
            }
        }

        // see if that was end of file
        tryDestroySharder(false);
        this.shardCount++;
        return shardQueue;
    }
    
    /**
     * tries to cancel sharding, forcefully
     */
    public void destroySharder() {
        tryDestroySharder(true);
    }
    
    /**
     * is still left content to shard
     * @return true if still content left to shard, false otherwise
     */
    public boolean isAlive() {
        return (this.jobShardStatus == ShardStatus.Alive);
    }
    
    private void initSharder() {
        File jobFile = new File(jobInitFilePath);
        
        if (!jobFile.exists()) {
            logger.log("Job file does not exist");
            return;
        }
        
        BufferedReader jobFileReader = null;
        try {
            jobFileReader = new BufferedReader(new FileReader(jobInitFilePath));
            String idLine = null;
        
            while ((idLine = jobFileReader.readLine()) != null) {
                this.jobContent.add(idLine);
            }
            
        } catch (FileNotFoundException ex) {
           logger.log("failed to read job file", ex);
        } catch (IOException ex) {
            logger.log("failed to read job file", ex);
        }

    }
    
    private void tryDestroySharder(boolean forceDestroy) {
        if (!forceDestroy && this.jobContent.isEmpty()) {
            this.jobShardStatus = ShardStatus.Done;
        }

        if (forceDestroy || this.jobShardStatus == ShardStatus.Done) {
            this.jobShardStatus = ShardStatus.Done;
            this.jobContent.clear();
        }
    }

    @Override
    public String toString() {
        return "JobSharder{" + jobInitFilePath + ", shardCount = " + shardCount + '}';
    }
    
    

}
