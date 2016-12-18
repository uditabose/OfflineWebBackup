package offlineweb.manager.indexer.wikipedia;

import java.io.IOException;
import java.util.List;
import offlineweb.manager.indexer.BaseXMLIndexer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 *
 * @author papa2
 */
public class WikipediaXMLAbstarctIndexer extends BaseXMLIndexer {

    private static final String DOC = "doc";
    private static final String TITLE = "title";
    private static final String ABSTRACT = "abstract";
    private static final String URL = "url";

    private StringBuilder currentContent = null;

    /**
     *
     * @param indexDirPath
     * @param statusLoggerPath
     * @param indexedFileList
     */
    public WikipediaXMLAbstarctIndexer(String indexDirPath, String statusLoggerPath, 
            List<String> indexedFileList) {
        super(indexDirPath, statusLoggerPath, indexedFileList);
        this.contentSource = "wikipedia";
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        currentNode = localName;
        if (DOC.equals(currentNode)) {
            currentDocument = new Document();
        }

        if (TITLE.equals(currentNode)
                || ABSTRACT.equals(currentNode)
                || URL.equals(currentNode)) {
            currentContent = new StringBuilder();
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (TITLE.equals(localName)) {
            this.psrsedTagCount++;
            currentDocument.add(new StringField(TITLE, 
                    currentContent.toString().replaceAll("Wikipedia:", "").trim(), 
                    Field.Store.YES));
        } else if (ABSTRACT.equals(localName)) {
            currentDocument.add(new TextField(ABSTRACT, currentContent.toString()
                    , Field.Store.YES));
        } else if (URL.equals(localName)) {
            currentDocument.add(new StringField(URL, currentContent.toString(),
                Field.Store.YES));
        }

        if (DOC.equals(localName)) {
            addDocumentToIndex(currentDocument);
            currentDocument = null;
            currentNode = null;
        }
        
        if (this.psrsedTagCount % 500 == 0) {
            updateIndexing();
        }
        
        currentStatus = currentNode + ":" + this.psrsedTagCount;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (TITLE.equals(currentNode)
                || ABSTRACT.equals(currentNode)
                || URL.equals(currentNode)) {
            currentContent.append(ch, start, length);
        }
    }

    /**
     *
     */
    @Override
    protected void start() {
        try {
            XMLReader wikiReader = XMLReaderFactory.createXMLReader();
            wikiReader.setContentHandler(this);
            for (String indexedFilePath : indexedFileList) {
                wikiReader.parse(indexedFilePath);
                try {
                    Thread.sleep(5 * 60 * 1000);
                } catch (InterruptedException ex) {
                    // do nothing
                }
            }

        } catch (SAXException ex) {
            log("Can't parse XML", ex.getMessage());
        } catch (IOException ex) {
            log("Can't parse XML", ex.getMessage());
        }
    }

    /**
     *
     * @param lastStatus
     */
    @Override
    protected void resume(String[] lastStatus) {
        // TODO :Think this through
    }
}
