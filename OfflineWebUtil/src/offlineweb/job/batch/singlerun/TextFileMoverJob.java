
package offlineweb.job.batch.singlerun;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import offlineweb.job.common.Job;
import offlineweb.job.util.FileNameUtil;
import offlineweb.job.util.LoggerUtil;

/**
 *
 * @author papa
 */
public class TextFileMoverJob extends Job {
    
    private static final LoggerUtil logger = 
            LoggerUtil.getLoggerUtil(TextFileMoverJob.class);

    @Override
    public boolean runJob() {

        File fromFile = new File(FileNameUtil.getTextFileName(jobFileId, jobFileName, 
            this.jobConfig.config("textdir")));

        File toFile = new File(FileNameUtil.getTextFileName(jobFileId, jobFileName, 
            this.jobConfig.config("datadir")));

        File toDir = new File(FileNameUtil.getFileDirPath(jobFileId, jobFileName, 
                this.jobConfig.config("datadir")));
        
        if (!toDir.exists()) {
            try {
                toDir.mkdirs();
            } catch (Exception ex) {
                logger.log("Can't create dir", ex.getMessage());
                return false;
            } 
        }

        BufferedReader fromFileReader = null;
        BufferedWriter toFileWriter = null;
        try {
            fromFileReader = new BufferedReader(new FileReader(fromFile));
            toFileWriter = new BufferedWriter(new FileWriter(toFile));
            String fromLine = null;
            
            while((fromLine = fromFileReader.readLine()) != null) {
                toFileWriter.write(fromLine);
                toFileWriter.newLine();
            }
            toFileWriter.flush();
            logger.log("file copied to", toFile.getAbsolutePath());
        } catch (FileNotFoundException ex) {
            logger.log("Can not find file", ex.getMessage());
        } catch (IOException ex) {
            logger.log("Can not write file", ex.getMessage());
        } finally {
            if (fromFileReader != null) {
                try {
                    fromFileReader.close();
                } catch (IOException ex) {
                   // nothing to do
                }
            }
            if (toFileWriter != null) {
                try {
                    toFileWriter.close();
                } catch (IOException ex) {
                   // nothing to do
                }
            }
        }

        return true;
    }
    
}
