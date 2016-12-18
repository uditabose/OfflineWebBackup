
package offlineweb.manager.util;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import offlineweb.manager.indexer.BaseIndexer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author papa2
 */
public class LoggerUtil {
    
    /**
     * logger for indexers
     */
    private Logger logger = null;
    
    /**
     * log file path for status of indexing process
     */
    private String statusLoggerPath = null;

    private LoggerUtil(Class theClass, String statusLoggerPath) {
        // prevent instantiation
        this.logger = LoggerFactory.getLogger(theClass);
        this.statusLoggerPath = statusLoggerPath;
    }
    
    public static LoggerUtil getLoggerUtil(Class theClass, String statusLoggerPath) {
        return new LoggerUtil(theClass, statusLoggerPath);
    }
    
    /**
     * logs the messages
     * @param messages
     */
    public void log (String... messages) {
        if (messages != null && logger != null) {
            logger.info(Arrays.deepToString(messages));
        }
    }
    
    /**
     * logs the messages
     * @param messages
     */
    public void log (Object... messages) {
        if (messages != null && logger != null) {
            logger.info(Arrays.deepToString(messages));
        }
    }
    
    /**
     * logs the status of the process
     * @param status status of the process
     * @param statusMessage message related to the status
     */
    public synchronized void  logStatus (BaseIndexer.INDEX_STATUS status, Object... statusMessage) {
        if (statusMessage != null && statusLoggerPath != null) {
            BufferedWriter statusLogWriter = null;
            try {
                statusLogWriter = new BufferedWriter(new FileWriter(statusLoggerPath));
                statusLogWriter.write("status=" + status.toString());
                statusLogWriter.newLine();
                statusLogWriter.write("time=" + new Date());
                statusLogWriter.newLine();
                statusLogWriter.write("message=" + Arrays.deepToString(statusMessage));
                statusLogWriter.newLine();
            } catch (IOException ex) {
                log("Failed to write status log", ex.getMessage(), ex.getCause().toString());
            } finally {
                if (statusLogWriter != null) {
                    try {
                        statusLogWriter.close();
                    } catch (IOException ex) {
                        // do nothing
                    }
                }
            }
        }   
    }
    
    public String[] getLastStatusDetails() {
        BufferedReader statusLogReader = null;
        String[] lastStatus = null;
        try {
            File statusLogFile = new File(statusLoggerPath);
            
            if (statusLogFile.exists()) {
                statusLogFile.renameTo(
                new File(statusLoggerPath + "." + System.currentTimeMillis()));
                statusLogFile = new File(statusLoggerPath);
            }
            statusLogReader = new BufferedReader(new FileReader(statusLogFile));
            String statusLine = null;
            
            while ((statusLine = statusLogReader.readLine()) != null) {
                if (statusLine.startsWith("message")) {
                    statusLine = statusLine.substring(statusLine.indexOf("="));
                    lastStatus = statusLine.split(":");
                    break;
                } 
            }

        } catch (FileNotFoundException ex) {
            log("Restart indexing failed", ex.getMessage());
        } catch (IOException ex) {
            log("Restart indexing failed", ex.getMessage());
        }  catch (Exception ex) {
            log("Restart indexing failed", ex.getMessage());
        } finally {
            try {
                statusLogReader.close();
            } catch (Exception ex) {
                // do nothing
            }
        }
        
        return lastStatus;
    }
    
}
