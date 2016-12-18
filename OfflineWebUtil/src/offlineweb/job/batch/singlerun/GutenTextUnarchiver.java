
package offlineweb.job.batch.singlerun;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import offlineweb.job.common.Job;
import offlineweb.job.util.FileNameUtil;
import offlineweb.job.util.LoggerUtil;
import offlineweb.job.util.UnzipUtil;

/**
 *
 * @author papa
 */
public class GutenTextUnarchiver extends Job {
    private static final LoggerUtil logger = 
            LoggerUtil.getLoggerUtil(GutenTextUnarchiver.class);
    
    @Override
    public boolean runJob() {
        String dirPath = FileNameUtil.getFileDirPath(jobFileId, 
                this.jobConfig.config("basedir"));
        File gutenDir = new File(dirPath + File.separator + jobFileId);
        if (gutenDir.exists() && gutenDir.isDirectory()) {
            logger.log("Dir {}", gutenDir.getAbsolutePath());
            // flatten the archive
            flattenArchive(gutenDir);
        }
        
        return true;
    }
    
    private void flattenArchive(File gutenDir) {
        File[] archiveFiles = gutenDir.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith("zip");
            }
        });

        if (archiveFiles != null && archiveFiles.length > 0) {

            for (File archiveFile : archiveFiles) {
                try {
                    logger.log("Unarchive : {}", archiveFile.getAbsolutePath());
                    logger.log("Unarchive At : {}", gutenDir.getAbsolutePath());
                    UnzipUtil.flattenZip(archiveFile, gutenDir.getAbsolutePath());
                } catch (IOException ex) {
                    logger.log("Can not unzip", archiveFile.getAbsolutePath(), ex);
                }
            }
        }
    }

}
