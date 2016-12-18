package offlineweb.manager.realscratch;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import static offlineweb.manager.indexer.util.IndexerUtil.*;
import org.apache.commons.codec.binary.Hex;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author papa2
 */
public class JustNewYork {

    private static final String[] NYC_TITLES = new String[]{
        "New York City",
        "New York City FC",
        "History of New York City",
        "List of cities in New York",
        "Portal:New York City",
        "New York metropolitan area",
        "Media in New York City",
        "Education in New York City",
        "New York",
        ".nyc",
        "Manhattan",
        "Transportation in New York City",
        "Architecture of New York City",
        "Borough (New York City)",
        "Demographics of New York City",
        "Lists of New York City Landmarks",
        "Tourism in New York City",
        "Mayor of New York City",
        "List of National Historic Landmarks in New York City",
        "New York City Subway",
        "City of Greater New York",
        "Mass transit in New York City",
        "Brooklyn",
        "New York-style pizza",
        "New York Fashion Week",
        "New York Life Building",
        "New York Knicks",
        "New York City Comptroller",
        "New York City Department of Transportation",
        "NYC Hudson",
        "Queens"
    };

    private static final String CONTENT_DIR = "/media/papa2/great/workspace/"
            + "OfflineWebData/wikidump/pages/";
    private static final String IMG_DIR = "/media/papa2/great/workspace/"
            + "OfflineWebData/wikidump/images/wikipedia/en";
    private static final String CONTENTID = "/media/papa2/great/workspace/"
            + "OfflineWebData/wikidump/contentid/nametoid-%s";
    private static final String HTML_API = "http://en.wikipedia.org/w/api.php?"
            + "format=json&action=parse&prop=text&page=%s";
    private static final Pattern HASH_PAT = Pattern.compile("\\/[a-z0-9]\\/[a-z0-9]{2}\\/");

    private static MessageDigest _messageDigest;

    // initialize the message digest for MD5 algorithm
    static {
        try {
            _messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ex) {
            _messageDigest = null;
        }
    }

    public static void main(String[] args) {
        // get file id
        String[] fileIds = getFileIds();
        System.out.println(Arrays.deepToString(fileIds));

        // download file
        downloadAndSave(fileIds);
    }

    private static String[] getFileIds() {
        String[] fileIds = new String[NYC_TITLES.length];
        Map<Character, Queue<Map.Entry<String, String>>> firsts = new HashMap<>();

        Queue<Map.Entry<String, String>> ids = null;
        Character fChar = '-';
        int fIdCnt = 0;
        for (String title : NYC_TITLES) {
            fChar = title.charAt(0);
            if (firsts.containsKey(fChar)) {
                ids = firsts.get(fChar);
            } else {
                ids = getIdMapping(new File(String.format(CONTENTID, fChar)));
                firsts.put(fChar, ids);
            }

            for (Map.Entry<String, String> id : ids) {
                if (id.getKey().equals(title)) {
                    fileIds[fIdCnt++] = id.getValue();
                    break;
                }
            }
        }

        firsts = null;
        return fileIds;
    }

    private static void downloadAndSave(String[] fileIds) {
        Document htmlDoc = null;
        int fileCnt = 0;
        for (String title : NYC_TITLES) {
            try {
                htmlDoc = Jsoup.parse(getValuefOfKey(title));
                updateImageURL(fileIds[fileCnt++], title.charAt(0), htmlDoc);
            } catch (Exception ex) {
                Logger.getLogger(JustNewYork.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private static String getValuefOfKey(String title) {
        String jsonContent = getContent(title);
        if (jsonContent == null || jsonContent.length() == 0) {
            return null;
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
            //
        }

        return jsonValue;
    }

    private static String getContent(String title) {

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
            System.err.println(ex);
        } catch (IOException ex) {
            System.err.println(ex);
        } finally {
            if (contentReader != null) {
                try {
                    contentReader.close();
                } catch (IOException ex) {
                    // do nothing
                }
            }
        }
        return content;
    }

    private static void updateImageURL(String fileId, char firstChar, Document htmlDoc) {
        updateImageSrc(htmlDoc);
        updateImageLink(htmlDoc);
        updateSimpleLink(htmlDoc);
        saveFormattedHtml(htmlDoc, firstChar, fileId);
    }

    private static void updateImageSrc(Document htmlDoc) {
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

    private static void updateImageLink(Document htmlDoc) {
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
    
    
    private static void updateSimpleLink(Document htmlDoc) {
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

    private static Map<String, String> parseImageSrc(final String imgSrc) {

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
        parsedMap.put(DIR, IMG_DIR + hashGr);
        parsedMap.put(FILE, IMG_DIR + hashGr + fileName + "." + extn);
        try {
            parsedMap.put(REPLACE, IMG_TEMPLATE + hashGr + URLEncoder.encode(fileName, "UTF-8") + "." + extn);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(JustNewYork.class.getName()).log(Level.SEVERE, null, ex);
        }

        return parsedMap;
    }

    private static void saveImageFile(Map<String, String> parsedValues) {
        File imageFile = new File(parsedValues.get(FILE));

        if (imageFile.exists()) {
            return;
        }
        
        File imgDir = new File(parsedValues.get(DIR));
        
        if (!imgDir.exists()) {
            imgDir.mkdirs();
        }

        try {
            String url = parsedValues.get(URL);
            if (!url.startsWith("http:")) {
                url = "http://" + url;
            }
            BufferedImage bufImage = ImageIO.read(new URL(url));
            boolean hasSaved = ImageIO.write(bufImage, parsedValues.get(EXTN), imageFile);
            parsedValues.put(FILE, imageFile.getName());
        } catch (MalformedURLException ex) {
            Logger.getLogger(JustNewYork.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(JustNewYork.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static void formatHtml(Document htmlDoc, Element image, String attr, Map<String, String> parsedValues) {
        image.attr(attr, parsedValues.get(REPLACE));
    }

    private static void saveFormattedHtml(Document htmlDoc, char firstChar, String fileId) {
        StringBuilder moveToDirPath = new StringBuilder(CONTENT_DIR)
                .append(File.separator)
                .append(firstChar)
                .append(File.separator);

        for (int i = 0; i < fileId.length() - 1; i++) {
            moveToDirPath.append(fileId.charAt(i))
                    .append(File.separator);
        }

        String newHtmlType = String.format("%s%s-h", moveToDirPath.toString(), fileId);
        System.out.println(newHtmlType);
        File htmlFile = new File(newHtmlType);
        
        BufferedWriter htmlWriter = null;
        try {
            //System.out.println(htmlDoc.outerHtml());
            htmlWriter = new BufferedWriter(new FileWriter(htmlFile, false));
            htmlWriter.write(htmlDoc.outerHtml());
            htmlWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(JustNewYork.class.getName()).log(Level.SEVERE, null, ex);
        }

    }


}
