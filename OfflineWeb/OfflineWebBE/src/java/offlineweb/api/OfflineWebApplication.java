
package offlineweb.api;

import offlineweb.api.filter.CORSResponseFilter;
import offlineweb.api.resource.ContentResource;
import offlineweb.api.resource.SearchResource;
import offlineweb.api.util.SearchUtil;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author papa2
 */
public class OfflineWebApplication extends ResourceConfig {
    
    private static final Logger logger = 
            LoggerFactory.getLogger(OfflineWebApplication.class);

    /**
     * initiates web service
     */
    public OfflineWebApplication() {
        logger.info("starting the web service");
        warmUpIndexReader();
        register(SearchResource.class);
        register(ContentResource.class);
        register(CORSResponseFilter.class);
    }

    private void warmUpIndexReader() {
        SearchUtil.warmUp();
    }
    
}
