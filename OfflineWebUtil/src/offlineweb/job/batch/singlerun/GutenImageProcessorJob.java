
package offlineweb.job.batch.singlerun;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import offlineweb.job.common.Job;
import offlineweb.job.util.FileNameUtil;
import offlineweb.job.util.LoggerUtil;
import offlineweb.job.util.UnzipUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * /media/papa/offline/OfflineWebData/media/guten-image
 * @author papa
 */
public class GutenImageProcessorJob extends Job {
    private static final LoggerUtil logger = 
            LoggerUtil.getLoggerUtil(GutenImageProcessorJob.class);
    private static final String IMG_TEMPLATE = "{{image_url}}";
    
    @Override
    public boolean runJob() {
        String dirPath = FileNameUtil.getFileDirPath(jobFileId, 
                this.jobConfig.config("basedir"));
        File gutenDir = new File(dirPath + File.separator + jobFileId);
        if (gutenDir.exists() && gutenDir.isDirectory()) {
            
            // flatten the archive
            List<String> unzippedFileList = flattenArchive(gutenDir);
            
            // all files
            File[] gutenFiles = gutenDir.listFiles();
            for (File gutenFile : gutenFiles) {
                if (gutenFile.getName().endsWith("html")
                        || gutenFile.getName().endsWith("htm")) {
                    
                    cleanHTMLAndProcessImage(gutenDir, gutenFile, unzippedFileList);
                    logger.log("File", gutenFile.getAbsolutePath());
                }
            }
        }   
        return true;
    }

    private void cleanHTMLAndProcessImage(File gutenDir, File gutenFile, List<String> unzippedFileList) {
        Document htmlDoc = null;
        
        try {
            htmlDoc = Jsoup.parse(gutenFile, "UTF-8");
        } catch (IOException ex) {
            logger.log("Can not read html file", gutenFile.getAbsolutePath(), ex);
            return;
        }
        
        updateImageURLsInPage(htmlDoc);

        BufferedWriter htmlWriter = null;
        try {

            htmlWriter = new BufferedWriter(new FileWriter(gutenFile, false));
            htmlWriter.write(onlyBody(htmlDoc));
            htmlWriter.close();
            
            logger.log("File saved", gutenFile.getAbsolutePath());
        } catch (IOException ex) {
            logger.log("File name encoding went wrong", ex);
        } catch (Exception ex) {
            logger.log("File name encoding went wrong", ex);
        }
    }
    
    private void updateImageURLsInPage(Document htmlDoc) {
        Elements allImageElements = htmlDoc.getElementsByTag("img");
        
        if (allImageElements == null || allImageElements.isEmpty()) {
            return;
        }
        
        for(Element imageElem :  allImageElements) {
            String imgName = imageElem.attr("src");
            int lastIdx = imgName.lastIndexOf("/");
            if (lastIdx >= 0) {
                imgName = imgName.substring(lastIdx + 1);
            }
            imageElem.attr("src", 
                    FileNameUtil.getFileDirPath(jobFileId, IMG_TEMPLATE) + imgName);
            
        }
    }

    private String onlyBody(Document htmlDoc) {
        return htmlDoc.body().html();
    }

    private List<String> flattenArchive(File gutenDir) {
        File[] archiveFiles = gutenDir.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith("zip");
            }
        });
        
        List<String> allUnZippedFiles = new ArrayList<>();
        if (archiveFiles != null && archiveFiles.length > 0) {
            String imageDirPath = 
                    FileNameUtil.getFileDirPath(jobFileId, 
                            this.jobConfig.config("mediadir"));
            logger.log("Image dir", imageDirPath);
            
            for (File archiveFile : archiveFiles) {
                try {
                    allUnZippedFiles.addAll(UnzipUtil.flattenZip(archiveFile, imageDirPath,
                            "jpg", "gif", "png", "svg")) ;
                } catch (IOException ex) {
                    logger.log("Can not unzip", archiveFile.getAbsolutePath(), ex);
                }
            }
        }
        
        return allUnZippedFiles;
    }

    
}
