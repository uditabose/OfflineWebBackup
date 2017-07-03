/*
 *  We will decide later!
 */
package offlineweb.sys.aspecttester;

import java.util.Date;
import offlineweb.sys.logmanager.annotations.Loggable;

/**
 *
 * @author uditabose
 */
public class LoggedMethod {
    
    @Loggable
    public String getName() {
        return String.format("Today is %s", new Date());
    }
    
    @Loggable
    public long getId() {
        return System.currentTimeMillis();
    }
    
    @Loggable
    public void catchStuff() {
        try {
            throw new Exception("catchStuff");
        } catch (Exception ex) {
            // caught stuff
            //throw ex;
        }
    }

    @Override
    public String toString() {
        return "LoggedMethod{" + '}';
    }
    
    
}
