
package offlineweb.manager.util.xml;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author papa
 */
public class IdSplitter {

    /**
     *
     * @param args
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        String nameToIdFilePath = "/usr/local/great/workspace/wikidump/contentid/nametoid";
        BufferedReader wikiIdReader = new BufferedReader(new FileReader(nameToIdFilePath));
        
        Map<String, String> contentIdMap = new HashMap<>();
        String nameToId = null;
        
        while ((nameToId = wikiIdReader.readLine()) != null) {
            String key = nameToId.charAt(0) + "";
            if (!contentIdMap.containsKey(key)) {
                contentIdMap.put(key, "/usr/local/great/workspace/wikidump/contentid/nametoid-" + key);
            }
            String keyFilePath = contentIdMap.get(key);
            
            BufferedWriter keyFileWriter = new BufferedWriter(new FileWriter(keyFilePath, true));
            keyFileWriter.append(nameToId).append("\n");
            keyFileWriter.close();
        }
    }
}
