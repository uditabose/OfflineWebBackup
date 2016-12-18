
package offlineweb.manager.crawler;

import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 *
 * @author papa2
 */
public abstract class BaseXMLCrawler extends BaseCrawler implements ContentHandler {
    
    /**
     *
     * @param crawlerBaseFilePath
     */
    protected BaseXMLCrawler(String crawlerBaseFilePath) {
        super(crawlerBaseFilePath);
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        // nothing to do
    }

    @Override
    public void endDocument() throws SAXException {
        finishDocumentCrawling();
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

    /**
     *
     */
    protected abstract  void finishDocumentCrawling();

}
