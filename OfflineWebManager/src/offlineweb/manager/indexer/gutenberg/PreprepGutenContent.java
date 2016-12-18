package offlineweb.manager.indexer.gutenberg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * moving guten content for uniform access pattern
 *
 * @author papa2
 */
public class PreprepGutenContent {

    public static void main(String[] args) {
        String baseDirPath = "/media/papa2/great/workspace/OfflineWebData/"
                + "gutendump/txt/www.gutenberg.lib.md.us";
        File baseDir = new File(baseDirPath);

        FilenameFilter etextFilter = new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.contains("etext05");
            }
        };

        FilenameFilter txtFilter = new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith("txt");
            }
        };

        File[] etextDirs = baseDir.listFiles(etextFilter);

        for (File etextDir : etextDirs) {
            File[] txtFiles = etextDir.listFiles(txtFilter);
            BufferedReader txtReader = null;
            for (File txtFile : txtFiles) {
                try {
                    txtReader = new BufferedReader(new FileReader(txtFile));
                    String testLine = null;
                    
                    while ((testLine = txtReader.readLine()) != null) {
                        if (testLine.contains("[Etext #") || testLine.contains("[EBook #")) {
                             StringBuilder subDirPath = null;
                            try {
                                int lidx = testLine.lastIndexOf("]");
                                int fidx = testLine.lastIndexOf("[Etext #");
                                if (fidx < 0) {
                                    fidx = testLine.lastIndexOf("[EBook #") + "[EBook #".length();
                                } else {
                                    fidx += "[Etext #".length();
                                }

                                System.out.println(fidx + " - " + lidx);

                                String testPart = testLine.substring(fidx, lidx).trim();
                                subDirPath = new StringBuilder(baseDirPath);
                                subDirPath.append(File.separator);
                                for (int i = 0; i < testPart.length() - 1; i++) {
                                    subDirPath.append(testPart.charAt(i)).append(File.separator);
                                }
                                subDirPath.append(testPart);
                                new File(subDirPath.toString()).mkdirs();
                                
                                subDirPath.append(File.separator)
                                .append(testPart)
                                .append(".txt");

                                System.out.println(txtFile.getAbsolutePath());
                                System.out.println(subDirPath);

                                txtFile.renameTo(new File(subDirPath.toString()));
                            } catch (Exception e) {
                                System.out.println("===============START======================");
                                System.out.println(txtFile.getAbsolutePath());
                                System.out.println(subDirPath);
                                System.out.println("=================END=======================");
                            } finally {
                                txtReader.close();
                            }

                            break;

                        }
                    }
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

    }

}
