/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package offlineweb.sys.userpersistencemanager;

import javax.persistence.EntityManager;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author uditabose
 */
public class KeyValuePersistenceManagerTest {
    
    public KeyValuePersistenceManagerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of entityManager method, of class KeyValuePersistenceManager.
     */
    @Test
    public void testEntityManager() {
        System.out.println("entityManager");
        KeyValuePersistenceManager instance = new KeyValuePersistenceManager();
        EntityManager result = instance.entityManager();
        assertNotNull(result);
    }
    
}
