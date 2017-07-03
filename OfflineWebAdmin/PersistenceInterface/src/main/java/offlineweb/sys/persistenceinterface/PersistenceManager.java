/*
 *  We will decide later!
 */
package offlineweb.sys.persistenceinterface;

import java.util.List;

/**
 * Public API for persistence layer
 * 
 * @author uditabose
 */
public interface PersistenceManager {
    
    <T> List<T> findAll(Class<T> clazz) throws PersistenceException;
    
    <T, M> T findById(Class<T> clazz, M id) throws PersistenceException;

    <T> void saveOrUpdate(T entity) throws PersistenceException;
    
    <T> void saveOrUpdate(List<T> entities) throws PersistenceException;
    
    <T> List<T> execute(Class<T> clazz, String sql) throws PersistenceException;
    
    void execute(String sql) throws PersistenceException;
    
}