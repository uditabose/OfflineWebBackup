
package offlineweb.job.batch.updater;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import offlineweb.job.util.FileNameUtil;
import offlineweb.job.common.Job;
import offlineweb.job.util.LoggerUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author papa2
 */
public class WikiHTMLRepoJob extends Job {
    private LoggerUtil logger;
    
    private static final String HTML_API = "https://en.wikipedia.org/w/api.php?"
            + "format=json&action=parse&prop=text&page=%s";
    private static final Pattern HASH_PAT = Pattern.compile("\\/[a-z0-9]\\/[a-z0-9]{2}\\/");


    public WikiHTMLRepoJob() {
        super();
    }

    @Override
    public boolean runJob() {
        logger = LoggerUtil.getLoggerUtil(WikiHTMLRepoJob.class, 
                jobConfig.config("logdir") + File.separator + "job-" + jobId + ".log");
        logger.log("Running job with", toString());
        ////logger.logStatus(JobStatus.COMMENCED, toString());
        
        File htmlFile = new File(FileNameUtil.getHTMLFileName(jobFileId, jobFileName, 
                jobConfig.config("pagedir")));
        if (htmlFile.exists() && "no|false".contains(jobConfig.config("batcher", "redo").toLowerCase())) {
            logger.log("File exists", htmlFile.getAbsolutePath());
            ////logger.logStatus(JobStatus.SUCEEDED, "Pre-existing");
            return true;
        }
        
        Document htmlDoc = null;
        try {
            String docContent = getValuefOfKey(jobFileName);
            if (docContent == null || "".equals(docContent)) {
                return true;
            }
            htmlDoc = Jsoup.parse(docContent);
            updateContentAndSave(jobFileId, jobFileName.charAt(0), htmlDoc);
            this.status = JobStatus.SUCEEDED;
            logger.log("Job finished", toString());
            ////logger.logStatus(JobStatus.SUCEEDED, toString());
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            this.status = JobStatus.FAILED;
            logger.log("Job failed", toString(), ex);
            ////logger.logStatus(JobStatus.FAILED, toString());
            return false;
        }
        
    }
    
    private String getValuefOfKey(String title) {
        String jsonContent = getContent(title);
        if (jsonContent == null || jsonContent.length() == 0) {
            logger.log("can not get json", title);
            return "";
        }
        Gson contentParser = new Gson();
        LinkedTreeMap<?, ?> contentMap = contentParser.fromJson(jsonContent,
                LinkedTreeMap.class);

        String jsonValue = null;
        Map tempMap = null;
        try {
            tempMap = (Map) contentMap.get("parse");
            tempMap = (Map) tempMap.get("text");
            jsonValue = (String) tempMap.get("*");
        } catch (Exception ex) {
            logger.log("JSON parsing failed", ex.getMessage());
        }

        return (jsonValue == null) ? "" : jsonValue;
    }

    private String getContent(String title) {

        String content = null;
        BufferedReader contentReader = null;

        try {
            URL contentURL = new URL(String.format(HTML_API, URLEncoder.encode(title, "UTF-8")));
            URLConnection apiConnection = contentURL.openConnection();
            apiConnection.setReadTimeout(5 * 1000);
            apiConnection.connect();

            contentReader = new BufferedReader(
                    new InputStreamReader(apiConnection.getInputStream()));

            String readLine = null;
            StringBuilder contentBuilder = new StringBuilder();

            int counter = 0;
            while (counter++ < 5) {
                if (((HttpURLConnection) apiConnection).getResponseCode() == 200) {
                    while ((readLine = contentReader.readLine()) != null) {
                        contentBuilder.append(readLine);
                    }
                    break;
                } else {
                    try {
                        Thread.sleep(5 * 1000);
                    } catch (InterruptedException ex) {
                        // do nothing
                    }
                }
            }
            content = contentBuilder.toString();
        } catch (MalformedURLException ex) {
            logger.log("bad URL", ex);
        } catch (IOException ex) {
            logger.log("bad URL connection", ex);
        } finally {
            if (contentReader != null) {
                try {
                    contentReader.close();
                } catch (IOException ex) {
                    // do nothing
                }
            }
        }
        return (content == null) ? "" : content;
    }

    private void updateContentAndSave(String fileId, char firstChar, Document htmlDoc) {
        updateImageSrc(htmlDoc);
        updateImageLink(htmlDoc);
        updateSimpleLink(htmlDoc);
        saveFormattedHtml(htmlDoc, firstChar, fileId);
    }

    private void updateImageSrc(Document htmlDoc) {
        Elements allImages = htmlDoc.getElementsByTag("img");

        if (allImages.size() < 1) {
            return;
        }

        Map<String, String> parsedValues = null;
        for (Element image : allImages) {
            parsedValues = parseImageSrc(image.attr("src"));

            if (parsedValues == null) {
                return;
            }

            saveImageFile(parsedValues);
            formatHtml(htmlDoc, image, "src", parsedValues);
        }
    }

    private void updateImageLink(Document htmlDoc) {
        Elements allLinks = htmlDoc.getElementsByClass("image");

        if (allLinks.size() < 1) {
            return;
        }

        Map<String, String> parsedValues = null;
        for (Element link : allLinks) {
            parsedValues = parseImageSrc(link.attr("href"));
            
            if (parsedValues == null) {
                return;
            }
            
            saveImageFile(parsedValues);
            formatHtml(htmlDoc, link, "href", parsedValues);
        }
    }

    private void updateSimpleLink(Document htmlDoc) {
        Elements allLinks = htmlDoc.getElementsByTag("a");
        if (allLinks.size() < 1) {
            return;
        }
        
        String href = null;
        for (Element link : allLinks) {
            href = link.attr("href");
            href = href.replaceAll("\\/wiki\\/", "/wikipedia/fetch?t=");
            link.attr("href", href);
        }
    }

    private static final String URL = "url";
    private static final String FILE = "file";
    private static final String EXTN = "extn";
    private static final String DIR = "dir";
    private static final String REPLACE = "replace";
    private static final String IMG_TEMPLATE = "{{image_url}}";

    private Map<String, String> parseImageSrc(final String imgSrc) {

        String strippedUrl = imgSrc;
        if (strippedUrl.contains("upload")) {
            strippedUrl = strippedUrl.substring(imgSrc.indexOf("upload"), imgSrc.length());
        }
        
        Matcher hashMat = HASH_PAT.matcher(strippedUrl);
        String urlPart = null, filePart = null, hashGr = null;

        if (hashMat.find()) {
            hashGr = hashMat.group();
            urlPart = strippedUrl.substring(0, strippedUrl.indexOf(hashGr));
            filePart = strippedUrl.substring(strippedUrl.indexOf(hashGr) + hashGr.length());
        } else {
            return null;
        }

        String fileName = filePart.split("/")[0];
        String extn = filePart.substring(filePart.lastIndexOf(".") + 1);

        Map<String, String> parsedMap = new HashMap<>();
        
        parsedMap.put(URL, strippedUrl);
        parsedMap.put(EXTN, extn);
        parsedMap.put(DIR, jobConfig.config("imagedir") + File.separator + hashGr);
        try {
            if ((fileName + "." + extn).length() <= 255) {
                parsedMap.put(FILE, jobConfig.config("imagedir") + File.separator + hashGr + fileName + "." + extn);
                parsedMap.put(REPLACE, IMG_TEMPLATE + hashGr + URLEncoder.encode(fileName, "UTF-8") + "." + extn);
            }
        } catch (UnsupportedEncodingException ex) {
            logger.log("File name encoding went wrong", ex.getMessage());
        } catch (Exception ex) {
            logger.log("File name encoding went wrong", ex.getMessage());
        }

        return parsedMap;
    }

    private  void saveImageFile(Map<String, String> parsedValues) {

        if (!parsedValues.containsKey(FILE)) {
            return;
        }
        
        File imageFile = new File(parsedValues.get(FILE));

        File imgDir = new File(parsedValues.get(DIR));
        
        if (!imgDir.exists()) {
            imgDir.mkdirs();
        }

        try {
            String url = parsedValues.get(URL);
            if (!url.startsWith("https:")) {
                url = "https://" + url;
            }
            BufferedImage bufImage = ImageIO.read(new URL(url));
            ImageIO.write(bufImage, parsedValues.get(EXTN), imageFile);
            parsedValues.put(FILE, imageFile.getName());
        } catch (MalformedURLException ex) {
            logger.log("Bad Image URL", ex.getMessage());
        } catch (FileNotFoundException ex) {
            logger.log("Bad Image File Name", ex.getMessage());
        } catch (IOException ex) {
            logger.log("Image saving went wrong", ex.getMessage());
        }  catch (Exception ex) {
            logger.log("Image saving went wrong", ex.getMessage());
        } 

    }

    private  void formatHtml(Document htmlDoc, Element image, String attr, Map<String, String> parsedValues) {
        if (parsedValues.containsKey(REPLACE)) {
            image.attr(attr, parsedValues.get(REPLACE));
        }
    }

    private  void saveFormattedHtml(Document htmlDoc, char firstChar, String fileId) {
        String moveToDirPath = FileNameUtil.getFileDirPath(jobFileId, jobFileName, jobConfig.config("pagedir"));

        String newHtmlType = 
                FileNameUtil.getHTMLFileName(jobFileId, jobFileName, jobConfig.config("pagedir"));
        
        File movDir = new File(moveToDirPath);
        if (!movDir.exists()) {
            movDir.mkdirs();
        }
        
        File htmlFile = new File(newHtmlType);
        BufferedWriter htmlWriter = null;
        try {

            htmlWriter = new BufferedWriter(new FileWriter(htmlFile, false));
            htmlWriter.write(htmlDoc.outerHtml());
            htmlWriter.close();
        } catch (IOException ex) {
            logger.log("File name encoding went wrong", ex);
        } catch (Exception ex) {
            logger.log("File name encoding went wrong", ex);
        }

    }

}
