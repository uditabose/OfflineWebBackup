
package offlineweb.manager.util.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 *
 * @author papa
 */
public class UnzipGut {
    
    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        unzipSubDirContent("/usr/local/great/workspace/gutendump/html/www.gutenberg.lib.md.us");
    }

    private static void unzipSubDirContent(String resourcePath) {
        File currentFileResource = new File(resourcePath);
        
        if (!currentFileResource.exists()) {
            System.out.println("The directory does not exist : " + resourcePath);
            return;
        }
        
        System.out.println(" Looking into : " + resourcePath);
        if (currentFileResource.isDirectory()) {
            String[] subFiles = currentFileResource.list();
            for (String subFile : subFiles) {
                unzipSubDirContent(resourcePath + File.separator + subFile);
            }
        } else if (currentFileResource.isFile() && resourcePath.endsWith("zip")) {
            ZipInputStream zippedResource = null;
            try {
                zippedResource = new ZipInputStream(new FileInputStream(currentFileResource));
                ZipEntry zippedEntry = null;
                byte[] zipBuffer = new byte[1024];
                do {
                    zippedEntry = zippedResource.getNextEntry();
                    
                    if (zippedEntry == null) {
                        return;
                    }
                    
                    String outName = zippedEntry.getName();
                    if (outName.contains("/")) {
                        outName = outName.substring(outName.indexOf("/") + 1);
                    }
                    FileOutputStream entryOutStream = 
                            new FileOutputStream(currentFileResource.getParent() + File.separator + outName);
                    
                    int readLen = 0;
                    
                    while((readLen = zippedResource.read(zipBuffer)) > 0) {
                        entryOutStream.write(zipBuffer, 0, readLen);
                    }
                    
                    entryOutStream.close();
                    System.out.println("Written : " + currentFileResource.getParent() + File.separator + outName);
                    System.out.println("-----------------------------------");
                } while (true);
                
            } catch (FileNotFoundException ex) {
                Logger.getLogger(UnzipGut.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(UnzipGut.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    zippedResource.closeEntry();
                    zippedResource.close();
                } catch (IOException ex) {
                    Logger.getLogger(UnzipGut.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    Logger.getLogger(UnzipGut.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        System.out.println("All children are visited of " + resourcePath);
        System.out.println("=====================================");
    }
    
}
