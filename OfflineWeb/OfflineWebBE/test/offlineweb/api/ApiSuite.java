package offlineweb.api;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 *
 * @author papa2
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({offlineweb.api.resource.ResourceSuite.class, offlineweb.api.bean.BeanSuite.class, offlineweb.api.OfflineWebApplicationTest.class})
public class ApiSuite {

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }
    
}
