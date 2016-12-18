
package offlineweb.manager.crawler;

/**
 *
 * @author papa2
 */
public abstract class BaseCrawler {
    
    /**
     *
     */
    protected String crawlerBaseFilePath = null;

    /**
     *
     * @param crawlerBaseFilePath
     */
    protected BaseCrawler(String crawlerBaseFilePath) {
        this.crawlerBaseFilePath = crawlerBaseFilePath;
    }

    /**
     *
     */
    public abstract void crawl();

}
