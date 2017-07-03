/**
 * We will decide later!
 */
package offlineweb.sys.logmanager.aspects;

import offlineweb.sys.logmanager.annotations.Loggable;
import org.aspectj.lang.JoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The logger object creator
 * 
 * @author uditabose
 */
public aspect LogHolderAspect pertypewithin(@Loggable *) {
    
    private Logger LOGGER;

    after() : staticinitialization(*) {
        LOGGER = LoggerFactory.getLogger(getWithinTypeName());
    }

    public Logger getLogger() {
        return LOGGER;
    }
}