package offlineweb.job.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 *
 * @author papa
 */
public class UnzipUtil {

    /**
     * Size of the buffer to read/write data
     */
    private static final int BUFFER_SIZE = 4096;

    /**
     * Extracts a zip file specified by the zipFilePath to a directory specified
     * by destDirectory (will be created if does not exists)
     *
     * @param zipFilePath
     * @param destDirectory
     * @return list of unzipped files
     * @throws IOException
     */
    public static List<String> unzip(String zipFilePath, String destDirectory) 
            throws IOException {
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
        }

        List<String> unzippedFileList = new ArrayList<>();
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry entry = null;

        // iterates over entries in the zip file
        while ((entry = zipIn.getNextEntry()) != null) {
            String filePath = destDirectory + File.separator + entry.getName();
            // the file unarchived
            unzippedFileList.add(filePath);
            if (!entry.isDirectory()) {
                // if the entry is a file, extracts it
                extractFile(zipIn, filePath);
            } else {
                // if the entry is a directory, make the directory
                File dir = new File(filePath);
                dir.mkdir();
            }
            zipIn.closeEntry();            
        }
        zipIn.close();
        
        return unzippedFileList;
    }
    
    /**
     *
     * @param zipFilePath
     * @param destDirectory
     * @param fileExt
     * @return list of unzipped files
     * @throws IOException
     */
    public static List<String> flattenZip(String zipFilePath, 
            String destDirectory, String... fileExt) throws IOException {
        return flattenZip(new File(zipFilePath), destDirectory, fileExt);
    }
    
    /**
     *
     * @param zipFile
     * @param destDirectory
     * @param fileExt
     * @return list of unzipped files
     * @throws IOException
     */
    public static List<String> flattenZip(File zipFile, 
            String destDirectory, String... fileExt) throws IOException {
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        
        List<String> unzippedFileList = new ArrayList<>();
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFile));
        ZipEntry entry = null;

        while ((entry = zipIn.getNextEntry()) != null) {
            
            if (!entry.isDirectory()) {
                String entryName = entry.getName();
                String[] splittedName = entryName.split("/");
                String filePath = destDirectory + File.separator 
                        + splittedName[splittedName.length -1];
                
                if (fileExt == null || fileExt.length == 0) {
                    extractFile(zipIn, filePath);
                    unzippedFileList.add(filePath);
                } else {
                    for (String ext : fileExt) {
                        if (entryName.toLowerCase().endsWith(ext)) {
                            extractFile(zipIn, filePath);
                            unzippedFileList.add(filePath);
                        }            
                    }
                }
            }

            zipIn.closeEntry();
        }
        zipIn.close();
        return unzippedFileList;
    }

    /**
     * Extracts a zip entry (file entry)
     *
     * @param zipIn
     * @param filePath
     * @throws IOException
     */
    private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }

}
