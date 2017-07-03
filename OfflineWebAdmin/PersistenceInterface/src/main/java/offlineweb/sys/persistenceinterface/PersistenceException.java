/*
 *  We will decide later!
 */
package offlineweb.sys.persistenceinterface;

/**
 *
 * @author uditabose
 */
public class PersistenceException extends RuntimeException {
    
    public PersistenceException() {
        super();
    }
    
    public PersistenceException(String message) {
        super(message);
    }
    
    public PersistenceException(Throwable ex) {
        super(ex);
    }
    
    public PersistenceException(String message, Throwable ex) {
        super(message, ex);
    }
}
