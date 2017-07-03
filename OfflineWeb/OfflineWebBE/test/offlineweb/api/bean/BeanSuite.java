package offlineweb.api.bean;

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
@Suite.SuiteClasses({offlineweb.api.bean.ResultTest.class, offlineweb.api.bean.TitleTest.class, offlineweb.api.bean.DocTest.class})
public class BeanSuite {

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
