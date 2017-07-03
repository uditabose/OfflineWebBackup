/*
 * We will decide later!
 */
package offlineweb.sys.parser;

/**
 * Makes a HTTP connection to a web service and parses the output. 
 * 
 * @author uditabose
 */
public interface ServiceParser {
    /**
     * 
     * @param <T> object to represent the output
     * @param wsURL web service URL
     * @param clazz class type of output object
     * @return output object of type <T>
     */
    <T> T parse(String wsURL, Class<T> clazz) ;
    
}
