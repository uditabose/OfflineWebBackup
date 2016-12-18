
package offlineweb.job.batch.updater;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import offlineweb.job.util.FileNameUtil;
import offlineweb.job.common.Job;
import offlineweb.job.util.LoggerUtil;

/**
 *
 * @author papa2
 */
public class WikiUnavailablePageJob extends Job {
    private LoggerUtil logger;

    public WikiUnavailablePageJob() {
        super();
    }

    @Override
    public boolean runJob() {
        
        logger = LoggerUtil.getLoggerUtil(WikiHTMLRepoJob.class, 
                jobConfig.config("logdir") + File.separator + "job-" + jobId + ".log");
        logger.log("Running job with", toString());
        //logger.logStatus(JobStatus.COMMENCED, toString());
        
        File pageFile = 
                new File(FileNameUtil.getHTMLFileName(jobFileId, jobFileName, 
                        jobConfig.config("pagedir")));
        
        if (pageFile.exists()) {
            logger.log("File exist", pageFile.getAbsolutePath());
            //logger.logStatus(JobStatus.SUCEEDED, toString());
            return true;
        }
        
        File unavailableMapFile = 
                new File(this.jobConfig.config("datadir") 
                        + File.separator + "nametoid-n" + jobFileName.charAt(0));
        
        BufferedWriter mapperWriter = null;
        try {
            mapperWriter = new BufferedWriter(new FileWriter(unavailableMapFile, true));
            mapperWriter.append(jobFileName)
                    .append("=")
                    .append(jobFileId);
            mapperWriter.newLine();
            mapperWriter.flush();
        } catch (IOException ex) {
            logger.log("can not write to mapper", ex.getMessage());
        } catch (Exception ex) {
            logger.log("can not write to mapper", ex.getMessage());
        } finally {
            if (mapperWriter != null) {
                try {
                    mapperWriter.close();
                } catch (Exception e) {
                    // nothing to do
                }
            }
        }

        return true;
        
    }
    
    
    
    

}
