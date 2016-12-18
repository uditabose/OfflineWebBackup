
package offlineweb.manager.indexer.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 *
 * @author papa2
 */
public class IndexerUtil {

    private IndexerUtil() {
        // preventing instantiation
    }
    
    /**
     * file size
     */
    public static enum FSIZE {
        SMALL  (512 * 1000, 0l), 
        MEDIUM (8 * 1024 * 1000, 512 * 1000), 
        LARGE  (25 * 1024 * 1000, 8 * 1024 * 1000), 
        XLARGE (0l, 25 * 1024 * 1000);
                
        private final long lessThan;
        private final long moreThan;
        
        FSIZE(long lessThan, long moreThan) {
            this.moreThan = moreThan;
            this.lessThan = lessThan;
        }

        public long getLessThan() {
            return lessThan;
        }

        public long getMoreThan() {
            return moreThan;
        }
        
    };
    
    public static Queue<Map.Entry<String, String>> getIdMapping(File idMapFile) {
        Queue<Map.Entry<String, String>> idMap = null;
        BufferedReader idMapReader = null;
        try {
            idMapReader = new BufferedReader(new FileReader(idMapFile));

            String idLine = null;
            Map.Entry<String, String> idEntry = null;
            idMap = new LinkedList<>();
            while ((idLine = idMapReader.readLine()) != null) {
                int lastEqualIdx = idLine.lastIndexOf("=");
                if (lastEqualIdx >= 0) {
                    idEntry = new AbstractMap.SimpleEntry<>(idLine.substring(0, lastEqualIdx),
                            idLine.substring(lastEqualIdx + 1));
                    idMap.add(idEntry);
                }
                
            }

        } catch (IOException ex) {
            // do nothing
        } finally {
            if (idMapReader != null) {
                try {
                    idMapReader.close();
                } catch (IOException ex) {
                    // do nothing
                }
            }
        }
        return idMap;
    }
    
    public static File[] findMapperFiles(String mapperDirPath, final String... fileKey) {
        File mapperDir = new File(mapperDirPath);

        if (!mapperDir.exists() || !mapperDir.isDirectory()) {
            return null;
        }

        // mapper file name must start with "nametoid-
        FilenameFilter nameToIdFileNameFilter = new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                if ((fileKey == null || fileKey.length == 0) && name.contains("nametoid")) {
                    return true;
                } else {
                    String matchText = Arrays.toString(fileKey);
                    String lastChar = "" + name.charAt(name.length() - 1);

                    if (matchText.contains(lastChar)) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        };

        return mapperDir.listFiles(nameToIdFileNameFilter);
    }
    
    public static File[] findMapperFiles(String mapperDirPath, 
            final FSIZE mapFileType, final String excludes) {
         File mapperDir = new File(mapperDirPath);

        if (!mapperDir.exists() || !mapperDir.isDirectory()) {
            return null;
        }

        FileFilter fileSizeFilter = new FileFilter() {

             @Override
             public boolean accept(File testFile) {
                 char[] fileNameAsChar = testFile.getName().toCharArray();
                 if (excludes != null && !excludes.contains("" + fileNameAsChar[fileNameAsChar.length - 1])) {
                     return false;
                 }
                 long fileLength = testFile.length();
                 switch(mapFileType) {
                     case SMALL :
                         return (fileLength <= mapFileType.lessThan);
                     case MEDIUM :
                         return (fileLength > mapFileType.moreThan 
                                   && fileLength <= mapFileType.lessThan);
                     case LARGE :
                         return (fileLength > mapFileType.moreThan 
                                 && fileLength <= mapFileType.lessThan);
                     case XLARGE :
                         return fileLength > mapFileType.moreThan ;
                     default :
                         return false;
                 }
             }
         };

        return mapperDir.listFiles(fileSizeFilter);
    }
    
    public static String readFile(String filePath) {
        File txtFile = new File(filePath);
        return readFile(txtFile);
    }
    
    public static String readFile(File txtFile) {
        if (!txtFile.exists() || !txtFile.isFile()) {
            return null;
        } 
        
        String content = null;
        BufferedReader contentReader = null;
        try {
            contentReader = new BufferedReader(new FileReader(txtFile));
            StringBuilder contentBuilder = new StringBuilder();
            
            String readLine = null;
            
            while((readLine = contentReader.readLine()) != null) {
                contentBuilder.append(readLine);
                contentBuilder.append("\n");
            }
            
            content = contentBuilder.toString();
        } catch (FileNotFoundException ex) {
            // do nothing
        } catch (IOException ex) {
            // do nothing
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
}
