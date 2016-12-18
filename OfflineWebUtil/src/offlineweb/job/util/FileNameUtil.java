
package offlineweb.job.util;

import java.io.File;

/**
 *
 * @author papa2
 */
public class FileNameUtil {
    
    public static String getHTMLFileName(String jobFileId, String jobFileName, 
            String baseDirPath) {
        String htmlFileName = String.format("%s%s-h", 
                getFileDirPath(jobFileId, jobFileName, baseDirPath), jobFileId);
        
        return htmlFileName;
    }
    
    public static String getTextFileName(String jobFileId, String jobFileName, String pageDirPath) {
        String htmlFileName = String.format("%s%s-t", 
                getFileDirPath(jobFileId, jobFileName, pageDirPath), jobFileId);
        
        return htmlFileName;
    }
    
    public static String getFileDirPath(String jobFileId, String jobFileName, 
            String baseDirPath) {
        StringBuilder moveToDirPath = new StringBuilder(baseDirPath)
                .append(File.separator)
                .append(jobFileName.charAt(0))
                .append(File.separator);

        for (int i = 0; i < jobFileId.length() - 1; i++) {
            moveToDirPath.append(jobFileId.charAt(i))
                    .append(File.separator);
        }
        
        return moveToDirPath.toString();
    }

    public static String getFileDirPath(String jobFileId, String baseDirPath) {
        StringBuilder moveToDirPath = new StringBuilder(baseDirPath)
                .append(File.separator);

        for (int i = 0; i < jobFileId.length() - 1; i++) {
            moveToDirPath.append(jobFileId.charAt(i))
                    .append(File.separator);
        }
        
        return moveToDirPath.toString();
    }
}
