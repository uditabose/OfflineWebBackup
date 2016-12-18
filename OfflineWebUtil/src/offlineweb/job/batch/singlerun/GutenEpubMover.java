
package offlineweb.job.batch.singlerun;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import offlineweb.job.common.Job;
import offlineweb.job.util.FileNameUtil;
import offlineweb.job.util.LoggerUtil;

/**
 *
 * @author papa
 */
public class GutenEpubMover  extends Job {
    private static final LoggerUtil logger = 
            LoggerUtil.getLoggerUtil(GutenEpubMover.class);
    
    @Override
    public boolean runJob() {
        String toDirPath = FileNameUtil.getFileDirPath(jobFileId, 
                this.jobConfig.config("mediadir"));

        File toDir = new File(toDirPath + jobFileId);
        

        String dirPath = this.jobConfig.config("basedir") + File.separator + jobFileId;
        String epubFilePath = dirPath + File.separator + "pg" + jobFileId + "-images.epub";
        
        logger.log(" TO DIR : ", toDirPath + jobFileId);
        logger.log(" THE FILE : ", epubFilePath);
        
        File epubFile = new File(epubFilePath);
        if (!epubFile.exists()) {
            return false;
        }
        if (!toDir.exists() || !toDir.isDirectory()) {
            toDir.mkdirs();
        }
        
        String command = "cp " + epubFilePath + " " + toDir.getAbsolutePath();
        
        logger.log(" COMMAND : ", command);
        
        try {
            Process p = Runtime.getRuntime().exec(command);
            
            BufferedReader in = new BufferedReader(
                                new InputStreamReader(p.getErrorStream()
                                ));
            String line = null;
            while ((line = in.readLine()) != null) {
                logger.log("ERROR : " ,line);
            }
        } catch (IOException ex) {
            logger.log("Copy exception", ex);
            return false;
//        } catch (InterruptedException ex) {
//            logger.log("Copy exception", ex);
//            return false;
        } catch (                                                                                                                                   Exception ex) {
            logger.log("Copy exception", ex);
            return false;
        }
        
        return true;
       
    }
    
}
