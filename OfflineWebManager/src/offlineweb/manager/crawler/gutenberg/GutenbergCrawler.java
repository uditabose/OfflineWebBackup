
package offlineweb.manager.crawler.gutenberg;

import offlineweb.manager.crawler.BaseXMLCrawler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 *
 * @author papa2
 */
public class GutenbergCrawler extends BaseXMLCrawler {

    private String currentNode = null;
    private String currentDocId = null;
    private String currentDocURL = null;
    
    /**
     *
     * @param crawlerBaseFilePath
     */
    public GutenbergCrawler(String crawlerBaseFilePath) {
        super(crawlerBaseFilePath);
    }
    
    /**
     *
     */
    @Override
    public void crawl() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     */
    @Override
    protected void finishDocumentCrawling() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void startDocument() throws SAXException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
