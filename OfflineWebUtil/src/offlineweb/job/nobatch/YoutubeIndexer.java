
package offlineweb.job.nobatch;

import offlineweb.job.common.JobConfig;
import offlineweb.job.util.LoggerUtil;


/**
 *
 * @author papa
 */
public class YoutubeIndexer {
    
    private static final LoggerUtil logger = LoggerUtil.getLoggerUtil(YoutubeIndexer.class);
    private static final String INDEX_CONFIG_PATH = 
            "/usr/local/great/workspace/OfflineWeb/OfflineWebUtil/src/youtube-index.properties";
    
    public static void main(String[] args) {
        logger.log("Starting youtube indexing");
        if (args == null || args.length < 1) {
            logger.log("Please provide a config ");
        }
        indexYoutubeContent(JobConfig.load(INDEX_CONFIG_PATH));
        logger.log("Finished youtube indexing");
        
    }
    
    private static void indexYoutubeContent(JobConfig jobConfig) {
        
    }
}
