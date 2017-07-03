/*
 * We will decide later!
 */
package offlineweb.sys.parser;

/**
 *
 * @author uditabose
 */
public class ServiceParserException extends RuntimeException {

    public ServiceParserException() {
        super();
    }
    
    public ServiceParserException(String message) {
        super(message);
    }
    
    public ServiceParserException(Throwable ex) {
        super(ex);
    }
    
    public ServiceParserException(String message, Throwable ex) {
        super(message, ex);
    }
    
}
