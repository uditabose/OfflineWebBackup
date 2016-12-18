package offlineweb.manager.crawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author papa2
 */
public class StrangeParse {

    /**
     *
     * @param args
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
//        String filePath = "/media/papa2/great/workspace/gutendump/GUTINDEX-M.ALL";
//        
//        BufferedReader br = new BufferedReader(new FileReader(filePath));
//        
//        String line = null;
//        
//        while((line = br.readLine()) != null) {
//            System.out.println(line);
//            System.out.println(line.length());
//            System.out.println("---------------------------------------");
//        }

        String testLine = "May, 2000  [Etext #2187]";
        if (testLine.contains("[Etext #") || testLine.contains("[EBook #")) {
            int lidx = testLine.lastIndexOf("]");
            int fidx = testLine.lastIndexOf("[Etext #");
            if (fidx < 0) {
                fidx = testLine.lastIndexOf("[EBook #") + "[EBook #".length();
            } else {
                fidx +=  "[Etext #".length();
            }

            String testPart = testLine.substring(fidx, lidx);
            
            StringBuilder subDirPath = new StringBuilder();
            for (int i = 0; i < testPart.length() - 1; i++) {
                subDirPath.append(testPart.charAt(i)).append(File.separator);
            }
            subDirPath.append(testPart)
                    .append(File.separator)
                    .append(testPart)
                    .append(".txt");
            System.out.println(testPart);
            System.out.println(subDirPath);

        }
    }

}
