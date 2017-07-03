/*
 *  We will decide later!
 */
package offlineweb.sys.userpersistencemanager;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import offlineweb.sys.persistenceinterface.abs.AbstractPersistenceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author uditabose
 */
public class KeyValuePersistenceManager extends AbstractPersistenceManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(KeyValuePersistenceManager.class);
    private static class Holder {
        private static final EntityManager INSTANCE = 
                Persistence.createEntityManagerFactory("offline").createEntityManager();
        
    }

    @Override
    protected EntityManager entityManager() {        
        return Holder.INSTANCE;
    }
    
}
