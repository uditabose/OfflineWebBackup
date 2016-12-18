
package offlineweb.job.common;

import java.util.UUID;

/**
 * Base class for all jobs
 * @author papa2
 */
public class Job {
    
    public enum JobStatus {
        COMMENCED,
        UPDATED,
        FAILED,
        SUCEEDED
    }
    
    protected String jobId; // unique job id
    protected JobStatus status;
    protected JobConfig jobConfig;
    protected String jobFileId;
    protected String jobFileName;

    public Job() {
        this.jobId = UUID.randomUUID().toString();
        this.status = JobStatus.COMMENCED;
    }
    
    /**
     * Sub-class should implement it 
     * @return if the job started successfully, false otherwise
     */
    public boolean runJob() {
        return false;
    }

    public String getJobFileId() {
        return jobFileId;
    }

    public void setJobFileId(String jobFileId) {
        this.jobFileId = jobFileId;
    }

    public String getJobFileName() {
        return jobFileName;
    }

    public void setJobFileName(String jobFileName) {
        this.jobFileName = jobFileName;
    }

    public void setJobConfig(JobConfig jobConfig) {
        this.jobConfig = jobConfig;
    }
 
    @Override
    public String toString() {
        return String.format("%s#%s#%s", this.jobId, this.jobFileId, this.jobFileName);
    }
    
}
