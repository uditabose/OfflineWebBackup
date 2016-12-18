
package offlineweb.util;

import offlineweb.job.common.JobSharder;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Queue;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author papa2
 */
public class JobSharderTest {

    public JobSharderTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() throws FileNotFoundException {
        
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of nextShard method, of class JobSharder.
     */
    @Test
    public void testNextShard() throws FileNotFoundException {
        System.out.println("nextShard");
        JobSharder instance = new JobSharder(520, "/media/papa2/great/workspace"
                + "/OfflineWebData/wikidump/contentid/nametoid-(");

        Queue<Map.Entry<String, String>> result = null;
        int totalJobs = 0;

        while(true) {
            result = instance.nextShard();
            if (result != null) {
                totalJobs += result.size(); 
            } else {
                break;
            }
        }
        
        assertEquals(7772, totalJobs);
        assertFalse(instance.isAlive());

    }

    /**
     * Test of destroySharder method, of class JobSharder.
     */
    @Test
    public void testDestroySharder() throws FileNotFoundException {
        System.out.println("destroySharder");
        JobSharder instance = new JobSharder(500, "/media/papa2/great/workspace"
                + "/OfflineWebData/wikidump/contentid/nametoid-(");
        instance.destroySharder();
        assertEquals(false, instance.isAlive());
        assertNull(instance.nextShard());
    }
    
}
