package offlineweb.api.resource;

import javax.ws.rs.core.Response;
import offlineweb.api.bean.Doc;
import offlineweb.api.bean.Result;
import static org.hamcrest.core.AnyOf.*;
import static org.hamcrest.core.AllOf.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.*;

/**
 *
 * @author papa2
 */
public class SearchResourceTest {
    
    private String searchTerm = null;
    private String searchPart1 = null;
    private String searchPart2 = null;
    private String searchPart3 = null;
    
    public SearchResourceTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        searchTerm = "circle of life";
        searchPart1 = "circle";
        searchPart2 = "of";
        searchPart3 = "life";
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of searchTitle method, of class SearchResource.
     */
    @Test
    public void testSearchTitle() {
        System.out.println("searchTitle");
        System.out.println("-----------------------------");
        SearchResource instance = new SearchResource();
        Response result = instance.searchTitle(searchTerm);

        assertNotNull(result);
        assertNotNull(result.getEntity());
        
        Result titleResult = (Result) result.getEntity();

        for (Doc doc : titleResult.getSearchResult()) {
            String thisTitle = doc.getTitle().toLowerCase();
            System.out.println(doc);
            assertThat(thisTitle, anyOf(containsString(searchPart1), 
                   containsString(searchPart2), containsString(searchPart3)));
        }
    }

    /**
     * Test of searchContent method, of class SearchResource.
     */
    @Test
    public void testSearchContent() {
        System.out.println("searchContent");
        System.out.println("-----------------------------");
        int pageNum = 1;
        SearchResource instance = new SearchResource();

        Response result = instance.searchContent(searchTerm, pageNum, null, null, null);
        assertNotNull(result);
        assertNotNull(result.getEntity());
        
        Result searchResult = (Result) result.getEntity();
        assertEquals(searchResult.getCurrentPage(), pageNum);
        assertEquals(searchResult.getSearchTerm(), searchTerm);
        
        if (searchResult.getTotalResults() > 0) {
            for (Doc doc : searchResult.getSearchResult()) {
                System.out.println(doc);
                 assertThat(doc.getTitle().toLowerCase(),
                   anyOf(containsString(searchTerm.toLowerCase()), containsString(searchPart1), 
                   containsString(searchPart2), containsString(searchPart3)));
            }
        }
    }
    
    /**
     * Test of searchContent method, of class SearchResource.
     */
    @Test
    public void testSearchContentByPageNum() {
        System.out.println("testSearchContentByPageNum");
        System.out.println("-----------------------------");
        int pageNum = 4;
        SearchResource instance = new SearchResource();

        Response result = instance.searchContent(searchTerm, pageNum, null, null, null);
        assertNotNull(result);
        assertNotNull(result.getEntity());
        
        Result searchResult = (Result) result.getEntity();
        assertEquals(searchResult.getCurrentPage(), pageNum);
        assertEquals(searchResult.getSearchTerm(), searchTerm);
        
        if (searchResult.getTotalResults() > 0) {
            for (Doc doc : searchResult.getSearchResult()) {
                System.out.println(doc);
                assertThat(doc.getTitle().toLowerCase(), 
                   anyOf(containsString(searchTerm.toLowerCase()), containsString(searchPart1), 
                   containsString(searchPart2), containsString(searchPart3)));
                 
            }
        }
    }
    
    /**
     * Test of searchContent method, of class SearchResource.
     */
    @Test
    public void testSearchContentBySource() {
        System.out.println("testSearchContentBySource");
        System.out.println("-----------------------------");
        int pageNum = 1;
        SearchResource instance = new SearchResource();

        Response result = instance.searchContent(searchTerm, pageNum, "gutenberg", null, null);
        assertNotNull(result);
        assertNotNull(result.getEntity());
        
        Result searchResult = (Result) result.getEntity();
        assertEquals(searchResult.getCurrentPage(), pageNum);
        assertEquals(searchResult.getSearchTerm(), searchTerm);
        
        if (searchResult.getTotalResults() > 0) {
            for (Doc doc : searchResult.getSearchResult()) {
                System.out.println(doc);
                 assertThat(doc.getSource().toLowerCase(), allOf(containsString("gutenberg")));
            }
        }
    }
    
    /**
     * Test of searchContent method, of class SearchResource.
     */
    @Test
    public void testSearchContentByType() {
        System.out.println("testSearchContentByType");
        System.out.println("-----------------------------");
        int pageNum = 1;
        SearchResource instance = new SearchResource();

        Response result = instance.searchContent(searchTerm, pageNum, null, "book", null);
        assertNotNull(result);
        assertNotNull(result.getEntity());
        
        Result searchResult = (Result) result.getEntity();
        assertEquals(searchResult.getCurrentPage(), pageNum);
        assertEquals(searchResult.getSearchTerm(), searchTerm);
        
        if (searchResult.getTotalResults() > 0) {
            for (Doc doc : searchResult.getSearchResult()) {
                System.out.println(doc);
                 assertThat(doc.getType().toLowerCase(), allOf(containsString("book")));
            }
        }
    }
    
    //@Test
    public void testMoreThanTenTimes() {
        for (int i = 0; i < 12; i++) {
            testSearchContent();
        }
    }

    /**
     * Test of searchLikeThis method, of class SearchResource.
     */
    //@Test
    public void testSearchLikeThis() {
        System.out.println("searchLikeThis");
        String searchTerm = "";
        SearchResource instance = new SearchResource();
        Response expResult = null;
        Response result = instance.searchLikeThis(searchTerm);
        assertEquals(expResult, result);
        
    }
}