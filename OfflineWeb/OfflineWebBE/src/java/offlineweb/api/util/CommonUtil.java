
package offlineweb.api.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author papa
 */
public class CommonUtil {
    
    private static final Logger logger
            = LoggerFactory.getLogger(CommonUtil.class);
    
    private static final String API_CONFIG = "api.properties";
    private static final Properties CONFIG_PROPERTIES = new Properties();

    private CommonUtil() {
        // private constractor to prevent instantiation
    }
    
    public static String configValue(String configKey) {
        loadConfig();
        return CONFIG_PROPERTIES.getProperty(configKey).trim();
    }
    
    public static List<String> configValues(String startOfKey) {
        loadConfig();
        
        List<String> configValues = new ArrayList<>();
        
        for (String config : CONFIG_PROPERTIES.stringPropertyNames()) {
            if (config.startsWith(startOfKey)) {
                configValues.add(CONFIG_PROPERTIES.getProperty(config).trim());
            }
        }        
        return configValues;
    }
    
    public static Map<String, String> configs(String startOfKey) {
        loadConfig();
        
        Map<String, String> configs = new HashMap<>();
        
        for (String config : CONFIG_PROPERTIES.stringPropertyNames()) {
            if (config.startsWith(startOfKey)) {
                configs.put(config, CONFIG_PROPERTIES.getProperty(config).trim());
            }
        }        
        return configs;
    }
    
    private static void loadConfig() {
        if (CONFIG_PROPERTIES.isEmpty()) {
            try {
                InputStream apiConfigStream
                        = CommonUtil.class
                             .getClassLoader().getResourceAsStream(API_CONFIG);
                if (apiConfigStream != null) {
                    CONFIG_PROPERTIES.load(apiConfigStream);
                }

            } catch (Exception ex) {
                logger.error("Can not load api configs", ex);
            }
        }
    }
}
