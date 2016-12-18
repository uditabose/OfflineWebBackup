
package offlineweb.manager.indexer;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * base class for creating index from XML file
 * @author papa2
 */
public abstract class BaseXMLIndexer extends BaseIndexer implements ContentHandler {

    /**
     * current node that is being parsed
     */
    protected String currentNode = null;

    /**
     * no. of tags parsed
     */
    protected int psrsedTagCount = 0;

    /**
     * current index document (lucene)
     */
    protected Document currentDocument = null;

    /**
     *
     * @param indexDirPath
     * @param statusLoggerPath
     * @param indexedFileList
     */
    public BaseXMLIndexer(String indexDirPath, String statusLoggerPath, 
            List<String> indexedFileList) {
        super(indexDirPath, statusLoggerPath, indexedFileList);
    }
    
    @Override
    public void setDocumentLocator(Locator locator) {
        // nothing to do
    }

    @Override
    public void startDocument() throws SAXException {
        try {
            Directory indexDir = FSDirectory.open(new File(indexDirPath));
            indexWriter = new IndexWriter(indexDir,  
                    new IndexWriterConfig(Version.LUCENE_4_10_3, 
                    new StandardAnalyzer()));
        } catch (IOException ex) {
           log("Failed to initiate indexing", ex.getMessage());
           cancelIndexing();
        }
    }

    @Override
    public void endDocument() throws SAXException {
        finishIndexing();
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        // nothing to do
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        // nothing to do
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        // nothing to do
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        // nothing to do
    }

    @Override
    public void skippedEntity(String name) throws SAXException {
        // nothing to do
    }
    
}
