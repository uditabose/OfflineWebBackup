package offlineweb.api.bean;

import java.util.List;
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
public class ResultTest {
    
    public ResultTest() {
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
     * Test of getSerachResult method, of class Result.
     */
    @Test
    public void testGetSerachResult() {
        System.out.println("getSerachResult");
        Result instance = new Result();
        List<Doc> expResult = null;
        List<Doc> result = instance.getSearchResult();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setSerachResult method, of class Result.
     */
    @Test
    public void testSetSerachResult() {
        System.out.println("setSerachResult");
        List<Doc> serachResult = null;
        Result instance = new Result();
        instance.setSearchResult(serachResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getCurrentPage method, of class Result.
     */
    @Test
    public void testGetCurrentPage() {
        System.out.println("getCurrentPage");
        Result instance = new Result();
        int expResult = 0;
        int result = instance.getCurrentPage();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setCurrentPage method, of class Result.
     */
    @Test
    public void testSetCurrentPage() {
        System.out.println("setCurrentPage");
        int currentPage = 0;
        Result instance = new Result();
        instance.setCurrentPage(currentPage);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTotalResults method, of class Result.
     */
    @Test
    public void testGetTotalResults() {
        System.out.println("getTotalResults");
        Result instance = new Result();
        long expResult = 0L;
        long result = instance.getTotalResults();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setTotalResults method, of class Result.
     */
    @Test
    public void testSetTotalResults() {
        System.out.println("setTotalResults");
        long totalResults = 0L;
        Result instance = new Result();
        instance.setTotalResults(totalResults);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSearchTerm method, of class Result.
     */
    @Test
    public void testGetSearchTerm() {
        System.out.println("getSearchTerm");
        Result instance = new Result();
        String expResult = "";
        String result = instance.getSearchTerm();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setSearchTerm method, of class Result.
     */
    @Test
    public void testSetSearchTerm() {
        System.out.println("setSearchTerm");
        String searchTerm = "";
        Result instance = new Result();
        instance.setSearchTerm(searchTerm);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addDoc method, of class Result.
     */
    @Test
    public void testAddDoc() {
        System.out.println("addDoc");
        Doc doc = null;
        Result instance = new Result();
        instance.addDoc(doc);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
