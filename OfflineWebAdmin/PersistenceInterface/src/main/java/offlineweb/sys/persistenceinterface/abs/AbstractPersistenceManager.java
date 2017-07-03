/*
 *  We will decide later!
 */
package offlineweb.sys.persistenceinterface.abs;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import offlineweb.sys.persistenceinterface.PersistenceException;
import offlineweb.sys.persistenceinterface.PersistenceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  
 * @author uditabose
 */
public abstract class AbstractPersistenceManager implements PersistenceManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPersistenceManager.class);
    protected abstract EntityManager entityManager();
    
    @Override
    public <T> List<T> findAll(Class<T> clazz) throws PersistenceException {
        LOGGER.debug("List of {}", clazz.getName());
        EntityManager entityManager = entityManager();
        entityManager.clear();
        CriteriaQuery<T> findAllQuery = entityManager.getCriteriaBuilder()
                .createQuery(clazz);
        Root<T> root = findAllQuery.from(clazz);
        findAllQuery.select(root);
        TypedQuery allQuery = entityManager.createQuery(findAllQuery);
        return allQuery.getResultList();
    }
    
    @Override
    public <T, M> T findById(Class<T> clazz, M id) throws PersistenceException {
        LOGGER.debug("Type of {} with id {}", clazz.getName(), id);
        EntityManager entityManager = entityManager();
        entityManager.clear();
        return entityManager.find(clazz, id);
    }

    @Override
    public <T> void saveOrUpdate(T entity) throws PersistenceException {
        LOGGER.debug("Persisting {}", entity);
        EntityManager entityManager = entityManager();
        entityManager.getTransaction().begin();
        if (entityManager.contains(entity)) {
            entityManager.merge(entity);
        } else {
            entityManager.persist(entity);
        }
        entityManager.flush();
        entityManager.getTransaction().commit();
    }

    @Override
    public <T> void saveOrUpdate(List<T> entities) throws PersistenceException {
        LOGGER.debug("Persisting type {} no of {}", 
                entities.get(0).getClass(),
                entities.size());
        EntityManager entityManager = entityManager();
        entityManager.getTransaction().begin();
        for (T entity : entities) {
            if (entityManager.contains(entity)) {
                entityManager.merge(entity);
            } else {
                entityManager.persist(entity);
            }
        }
        entityManager.flush();
        entityManager.getTransaction().commit();        
    }

    @Override
    public <T> List<T> execute(Class<T> clazz, String sql) throws PersistenceException {
        LOGGER.debug("Execute for type {} with SQL {}", clazz.getName(), sql);
        EntityManager entityManager = entityManager();
        entityManager.clear();
        Query query = entityManager.createQuery(sql);
        return query.getResultList();
    }
    
    @Override
    public void execute(String sql) {
        LOGGER.debug("Execute  with SQL {}", sql);
        EntityManager entityManager = entityManager();
        entityManager.clear();
        Query query = entityManager.createQuery(sql);
        entityManager.getTransaction().begin();
        query.executeUpdate();
        entityManager.getTransaction().commit();

    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        EntityManager entityManager = entityManager();
        if (entityManager.isOpen()) {
            entityManager().flush();
            entityManager.close();
        }
        
    }
    
    
}
