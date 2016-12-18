
package offlineweb.job.common;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import offlineweb.job.util.LoggerUtil;

/**
 *
 * @author papa2
 */
public class JobConfig {
    
    private static final String CONFIG_PREFIX = "offline.job";
    private static final String DEFAULT_CONFIG_FILE = "job-config.properties";
    
    private static final LoggerUtil logger = 
            LoggerUtil.getLoggerUtil(JobConfig.class);
    
    private final Properties jobConfigs = new Properties();
    
    private JobConfig() {
        // prevent instantiation
    }
    
    public static JobConfig load() {
        JobConfig config = new JobConfig();
        try {
            config.jobConfigs.load(JobConfig.class.getClassLoader()
                    .getResourceAsStream(DEFAULT_CONFIG_FILE));
        } catch (IOException ex) {
            logger.log("Failed to load config file", ex);
        }
        return config;
    }
    
    public static JobConfig load(String configFilePath) {
        JobConfig config = new JobConfig();
        try {
            config.jobConfigs.load(new FileReader(configFilePath));
        } catch (IOException ex) {
            logger.log("Failed to load config file", ex);
        }
        return config;
    }
    
    public String config(String... configKey) {
        if (jobConfigs.isEmpty()) {
            logger.log("Config not loaded");
            return null;
        }
        
        StringBuilder confKey = new StringBuilder(CONFIG_PREFIX);
        for (int i = 0; i < configKey.length; i++) {
            confKey.append(".").append(configKey[i]); 
        }

        return this.jobConfigs.getProperty(confKey.toString(), "");
    }

}
