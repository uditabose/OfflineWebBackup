
package offlineweb.manager.util.xml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import org.codehaus.stax2.XMLInputFactory2;

/**
 *
 * @author papa
 */
public class WikipediaPagesXMLSplitter {
    
    /**
     *
     * @param args
     * @throws FileNotFoundException
     * @throws XMLStreamException
     * @throws IOException
     */
    public static void main(String[] args) throws FileNotFoundException, XMLStreamException, IOException {
        args = new String[]{"/usr/local/great/workspace/wikidump/enwiki-20141208-pages-articles.xml",
        "/usr/local/great/workspace/wikidump/pages",
        "/usr/local/great/workspace/wikidump/contentid/nametoid"};
        XMLInputFactory xmlif = XMLInputFactory2.newInstance();
        XMLEventReader xmlef = xmlif.createXMLEventReader(new FileInputStream(new File(args[0])));
        
        Map<String, String> subDirectoryMap = new HashMap<String, String>();
        StringBuilder currentTitle = null;
        StringBuilder currentText = null;
        StringBuilder currentId = null;
        String currentNode = null;
        BufferedWriter indexWriter = new BufferedWriter(new FileWriter(args[2] ));
        while(xmlef.hasNext()) {
            XMLEvent event = xmlef.nextEvent();
            if (event.getEventType() == XMLEvent.START_ELEMENT) {
                currentNode = event.asStartElement().getName().getLocalPart();
                if ("page".equals(currentNode)) {
                    currentText = new StringBuilder();
                    currentTitle = new StringBuilder();
                    currentId = new StringBuilder();
                } 
            } else if (event.getEventType() == XMLEvent.END_ELEMENT) {
                String localName = event.asEndElement().getName().getLocalPart();
                if ("page".equals(localName)) {
                    String subDir = currentTitle.substring(0, 1);
                    String targetDirPath = subDirectoryMap.get(subDir);
                    
                    if (targetDirPath == null) {
                        targetDirPath = args[1] + File.separator + subDir;
                        subDirectoryMap.put(subDir, targetDirPath);
                        
                    }
                    File targetDir = new File(targetDirPath);
                    if (!targetDir.exists()) {
                        targetDir.mkdirs();
                    }
                    
                    BufferedWriter wikiWriter = new BufferedWriter(new FileWriter(
                            new File(targetDir, currentId.toString().trim())));
                    wikiWriter.write(currentText.toString());
                    wikiWriter.close(); 
                    
                    indexWriter.write(currentTitle.toString().trim() + "=" + currentId.toString().trim());
                    indexWriter.newLine();
                    
                } 
            } else if (event.getEventType() == XMLEvent.CHARACTERS) {
                if ("title".equals(currentNode)) {
                    currentTitle.append(event.asCharacters().getData());
                } else if ("text".equals(currentNode)) {
                    currentText.append(event.asCharacters().getData());
                } else if ("id".equals(currentNode) && currentId.length() == 0) {
                    currentId.append(event.asCharacters().getData());
                }
            }
        }
        indexWriter.close();
                
    }
    
}
