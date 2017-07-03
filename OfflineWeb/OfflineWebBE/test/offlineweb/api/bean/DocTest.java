package offlineweb.api.bean;

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
public class DocTest {
    
    public DocTest() {
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
     * Test of getTitle method, of class Doc.
     */
    @Test
    public void testGetTitle() {
        System.out.println("getTitle");
        Doc instance = new Doc();
        String expResult = "";
        String result = instance.getTitle();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setTitle method, of class Doc.
     */
    @Test
    public void testSetTitle() {
        System.out.println("setTitle");
        String title = "";
        Doc instance = new Doc();
        instance.setTitle(title);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getType method, of class Doc.
     */
    @Test
    public void testGetType() {
        System.out.println("getType");
        Doc instance = new Doc();
        String expResult = "";
        String result = instance.getType();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setType method, of class Doc.
     */
    @Test
    public void testSetType() {
        System.out.println("setType");
        String type = "";
        Doc instance = new Doc();
        instance.setType(type);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSource method, of class Doc.
     */
    @Test
    public void testGetSource() {
        System.out.println("getSource");
        Doc instance = new Doc();
        String expResult = "";
        String result = instance.getSource();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setSource method, of class Doc.
     */
    @Test
    public void testSetSource() {
        System.out.println("setSource");
        String source = "";
        Doc instance = new Doc();
        instance.setSource(source);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
