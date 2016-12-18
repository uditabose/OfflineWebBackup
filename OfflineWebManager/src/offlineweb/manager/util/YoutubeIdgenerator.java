
package offlineweb.manager.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author papa2
 */
public class YoutubeIdgenerator {

    protected static final String TITLE = "title";
    protected static final String CONTENT = "content";
    private static final String DESCRIPTION = "description";
    private static final String ID = "id";
    
    public static void main(String[] args) throws IOException{
        generateYoutubeId("/media/papa2/great/workspace/OfflineWebData/youtubedump");
        
    }

    private static void generateYoutubeId(String youtubeBaseDirPath) throws IOException {
        FilenameFilter youtubeDescFileFilter = new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.contains("youtube-desc-");
            }
        };
        
        File youtubeDescBaseDir = new File(String.format("%s%s%s", 
                youtubeBaseDirPath, File.separator, "abstract"));
        
        if (!youtubeDescBaseDir.exists() || !youtubeDescBaseDir.isDirectory()) {
            System.err.println("Invalid Youtube base dir ");
            System.out.println(youtubeDescBaseDir.getAbsolutePath());
            return;
        }
        
        File youtubeMapFile = new File(String.format("%s%s%s", 
                youtubeBaseDirPath, File.separator, "nametoid")); 
        
        youtubeMapFile.createNewFile();
        
        File[] youtubeDescFiles = youtubeDescBaseDir.listFiles(youtubeDescFileFilter);
        
        if (youtubeDescFiles == null || youtubeDescFiles.length == 0) {
            System.err.println("No files with video description");
            return;
        }
        
        BufferedWriter idMapWriter = new BufferedWriter(new FileWriter(youtubeMapFile, true));
        for (File descFile : youtubeDescFiles) {
            Map<String, String> descriptions = getDescs(descFile);
            idMapWriter.append(String.format("%s=%s", descriptions.get(TITLE), descriptions.get(ID)));
            idMapWriter.newLine();
        }
        
        idMapWriter.flush();
        idMapWriter.close();
    }
    
    private static Map<String, String> getDescs(File descFile) {
        BufferedReader descriptionReader = null;
        Map<String, String> descMap = null;
        try {
            descriptionReader = new BufferedReader(new FileReader(descFile));
            String descLine = null;
            descMap = new HashMap<>();
            descMap.put(ID, descFile.getName().replaceAll("youtube-desc-", "").trim());
            while ((descLine = descriptionReader.readLine()) != null) {
                if (descLine.startsWith(TITLE)) {
                    descMap.put(TITLE, descLine.replaceFirst(TITLE + "=", ""));
                } else if (descLine.startsWith(DESCRIPTION)) {
                    descMap.put(DESCRIPTION, descLine.replaceFirst(DESCRIPTION + "=", ""));
                }
            }
        } catch (FileNotFoundException ex) {
            System.err.println(String.format("can not read youtube description file %s %s"  
                    , descFile.getAbsolutePath(), ex.getMessage()));
        } catch (IOException ex) {
            System.err.println(String.format("can not read youtube description file %s %s", 
                    descFile.getAbsolutePath(), ex.getMessage()));
        }
        
        return descMap;
    }

}
