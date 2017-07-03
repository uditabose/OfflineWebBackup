package offlineweb.api.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;
import java.util.HashSet;
import offlineweb.api.bean.Content;
import static offlineweb.api.util.ConfigConstants.*;
import org.slf4j.LoggerFactory;

/**
 *
 * @author papa
 */
public class ContentUtil {
    
    private static final org.slf4j.Logger logger
            = LoggerFactory.getLogger(ContentUtil.class);

    private ContentUtil() {
        // private constructor to prevent instantiation
    }

    public static Content getWikiContent(String articleName, String... articleId) {
        String thisArticleId = null;

        if (articleId != null && articleId.length > 0) {
            thisArticleId = articleId[0];
        }

        if (thisArticleId == null || thisArticleId.trim().equals(BLANK)) {
            String wikiMapperBaseDir
                = new StringBuilder(CommonUtil.configValue(Repo.WIKI_MAPPER_BASE))
                    .append(File.separator)
                    .append(MAPPER_FILE).append("-").append(articleName.charAt(0))
                .toString();
            
            logger.info("Reading mapper file {}", wikiMapperBaseDir);
            
            thisArticleId = findDocId(wikiMapperBaseDir, articleName);
        }
        
        logger.info("Wiki ID {}", thisArticleId);
        if (NO_ID.equals(thisArticleId)) {
            return NO_CONTENT;
        }
        
        StringBuilder wikiFilePath 
            = getContentDirPathBuilder(
                 CommonUtil.configValue(Repo.WIKI_CONTENT_BASE)
                 + File.separator
                 + articleName.charAt(0),
             thisArticleId);

        wikiFilePath.append(thisArticleId).append("-h");
        
        logger.info("Wiki page path {}", wikiFilePath);
        
        String wikiContent = htmlContent(wikiFilePath.toString());
        wikiContent = wikiContent.replaceAll(IMAGE_URL_PLACEHOLDER, 
            CommonUtil.configValue(Media.WIKI_IMAGE));

        return new Content(wikiContent, thisArticleId);
    }

    public static Content getGutenContent(String bookName, String... bookId) {
        String thisBookId = null;

        if (bookId != null && bookId.length > 0) {
            thisBookId = bookId[0];
        }

        if (thisBookId == null || thisBookId.trim().equals(BLANK)) {
            String gutenMapperBaseDir
                = new StringBuilder(CommonUtil.configValue(Repo.GUTEN_MAPPER_BASE))
                .append(File.separator)
                .append(MAPPER_FILE)
                .toString();
            
            logger.info("Reading mapper file {}", gutenMapperBaseDir);
            
            thisBookId = findDocId(gutenMapperBaseDir, bookName);
        }
        
        logger.info("Book ID {}", thisBookId);
        if (NO_ID.equals(thisBookId)) {
            return NO_CONTENT;
        }
        
        StringBuilder gutenDirPath = getContentDirPathBuilder(
                  CommonUtil.configValue(Repo.GUTEN_HTML_BASE), thisBookId)
                  .append(thisBookId).append(File.separator);

        logger.info("Guten page path {}", gutenDirPath);
        
        String gutenContent = null;
        String gutenFilePath = findFileOfType(gutenDirPath, "htm", "html");
        if (gutenFilePath == null) {
            // no html file found
            gutenDirPath = getContentDirPathBuilder(
                  CommonUtil.configValue(Repo.GUTEN_TEXT_BASE), thisBookId)
                  .append(thisBookId).append(File.separator);
                
            gutenFilePath = findFileOfType(gutenDirPath, "txt");
            
            if (gutenFilePath == null) {
                return NO_CONTENT;
            }
            
            gutenContent = textContent(gutenFilePath);
            logger.info("Text reteieved for {}", thisBookId);

        } else {
            gutenContent = htmlContent(gutenFilePath);
            
            gutenContent = gutenContent.replaceAll(IMAGE_URL_PLACEHOLDER, 
                CommonUtil.configValue(Media.GUTEN_IMAGE));
            logger.info("HTML reteieved for {}", thisBookId);
        }

        return new Content(gutenContent, thisBookId);
    }

    public static Content getYoutubeLink(String videoName, String... videoId) {
        if (videoId == null || videoId.length == 0) {
            return NO_CONTENT;
        }
        
        if (videoId[0] == null || videoId[0].trim().equals(BLANK)) {
            return NO_CONTENT;
        }
        String youtubeVideoUrl = CommonUtil.configValue(Media.YOUTUBE_VIDEO)
                + videoId[0] + ".mp4";
        
        logger.info("Youtube URL {}", youtubeVideoUrl);
        
        return new Content(youtubeVideoUrl, videoId[0]);
    }

    private static String findDocId(String mapperFilePath, String articleName) {
        File mapperFile = new File(mapperFilePath);
        if (!mapperFile.exists() || !mapperFile.isFile()) {
            return NO_ID;
        }
        
        String idLine = null;
        BufferedReader mapperReader = null;
        try {
            mapperReader = new BufferedReader(new FileReader(mapperFile));

            while ((idLine = mapperReader.readLine()) != null) {
                if (idLine.startsWith(articleName + "=")) {
                    break;
                }
            }
            
        } catch (FileNotFoundException ex) {
            logger.error("Could not read mapper file", ex);
        } catch (IOException ex) {
            logger.error("Could not read mapper file", ex);
        } finally {
            try {
                mapperReader.close();
            } catch (IOException ex) {
                logger.error("Could not close mapper file", ex);
            }
        }

        if (idLine == null) {
            return NO_ID;
        }
        
        String docId = idLine.substring(idLine.lastIndexOf("=") + 1);
        return docId;

    }
    
    private static String htmlContent(String contentFilePath) {
        File contentFile = new File(contentFilePath);
        
        if (!contentFile.exists() || !contentFile.isFile()) {
            return BLANK;
        }
        
        StringBuilder contentBuilder = new StringBuilder();
        BufferedReader contentReader = null;
        try {
            contentReader = new BufferedReader(new FileReader(contentFile));
            String line = null;
            while ((line = contentReader.readLine()) != null) {
                contentBuilder.append(line)
                        .append("\n");
            }
            
        } catch (FileNotFoundException ex) {
            logger.error("Could not read content file", ex);
        } catch (IOException ex) {
            logger.error("Could not read content file", ex);
        } finally {
            try {
                contentReader.close();
            } catch (IOException ex) {
                logger.error("Could not close content file", ex);
            }
        }
        
        return contentBuilder.toString();
    }

    private static String textContent(String contentFilePath) {
        File contentFile = new File(contentFilePath);
        
        if (!contentFile.exists() || !contentFile.isFile()) {
            return BLANK;
        }
        
        StringBuilder contentBuilder = new StringBuilder("<p>");
        BufferedReader contentReader = null;
        try {
            contentReader = new BufferedReader(new FileReader(contentFile));
            String line = null;
            while ((line = contentReader.readLine()) != null) {
                contentBuilder.append(line)
                        .append("<br>");
            }
            
        } catch (FileNotFoundException ex) {
            logger.error("Could not read content file", ex);
        } catch (IOException ex) {
            logger.error("Could not read content file", ex);
        } finally {
            try {
                contentReader.close();
            } catch (IOException ex) {
                logger.error("Could not close content file", ex);
            }
        }
        contentBuilder.append("</p>");
        return contentBuilder.toString();
    }
    
    private static StringBuilder getContentDirPathBuilder(String basePath, String fileId) {
        StringBuilder filePathBuilder 
                = new StringBuilder(basePath)
                .append(File.separator);
        
        for (int charCnt = 0; charCnt < fileId.length() - 1; charCnt++) {
            filePathBuilder.append(fileId.charAt(charCnt))
                    .append(File.separator);
        }
        
        return filePathBuilder;
    }
    
    private static String findFileOfType(StringBuilder dirPath, String... fileType) {
        File baseDir = new File(dirPath.toString());
        
        if (!baseDir.exists() || !baseDir.isDirectory()) {
            return null;
        }
        Set<String> types = new HashSet<String>();
        for (String fType : fileType) {
            types.add(fType);
        }
        
        for (File child : baseDir.listFiles()) {
            String fExt = child.getName();
            fExt = fExt.substring(fExt.lastIndexOf(".") + 1);
            if (types.contains(fExt)) {
                return child.getAbsolutePath();
            }
        }
        
        return null;
    }
}
