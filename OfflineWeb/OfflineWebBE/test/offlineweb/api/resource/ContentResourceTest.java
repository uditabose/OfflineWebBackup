package offlineweb.api.resource;

import javax.ws.rs.core.Response;
import offlineweb.api.bean.Content;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author papa
 */
public class ContentResourceTest {
    
    public ContentResourceTest() {
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
    
    // New York City=645042

    /**
     * Test of getWikiText method, of class ContentResource.
     */
    @Test
    public void testGetWikiText() {
        System.out.println("getWikiText");
        String articleId = "645042";
        String articleName = "New York City";
        ContentResource instance = new ContentResource();
        
        Response result = instance.getWikiText(articleId, articleName);
        assertNotNull(result);
        assertEquals(200, result.getStatus());
        
        Content content = (Content) result.getEntity();
        assertNotNull(content);
        assertNotNull(content.getContent());
        assertTrue(content.getContent().length() > 0);
        
        //System.out.println(content);
    }
    
    @Test
    public void testGetWikiTextByName() {
        System.out.println("getWikiText");
        String articleId = null;
        String articleName = "New York City";
        ContentResource instance = new ContentResource();
        
        Response result = instance.getWikiText(articleId, articleName);
        assertNotNull(result);
        assertEquals(200, result.getStatus());
        
        Content content = (Content) result.getEntity();
        assertNotNull(content);
        assertNotNull(content.getContent());
        assertTrue(content.getContent().length() > 0);
        
        //System.out.println(content);
    }
    
    @Test
    public void testGetWikiTextBlank() {
        System.out.println("getWikiText");
        String articleId = null;
        String articleName = "#$%^";
        ContentResource instance = new ContentResource();
        
        Response result = instance.getWikiText(articleId, articleName);
        assertNotNull(result);
        assertEquals(200, result.getStatus());
        
        Content content = (Content) result.getEntity();
        assertNotNull(content);
        assertNotNull(content.getContent());
        assertTrue(content.getContent().length() == 0);
        
        //System.out.println(content);
    }

    /**
     * Test of getGutenText method, of class ContentResource.
     */
    @Test
    public void testGetGutenText() {
        System.out.println("getGutenText");
        String bookName = "The Folk-lore of Plants";
        String bookId = "10118";
        ContentResource instance = new ContentResource();

        Response result = instance.getGutenText(bookId, bookName);
        assertNotNull(result);
        assertEquals(200, result.getStatus());
        
        Content content = (Content) result.getEntity();
        assertNotNull(content);
        assertNotNull(content.getContent());
        assertTrue(content.getContent().length() > 0);
        
        System.out.println(content);
    }

    /**
     * Test of getYoutubeURL method, of class ContentResource.
     */
    //@Test
    public void testGetYoutubeURL() {
        System.out.println("getYoutubeURL");
        String videoId = "";
        String videoName = "";
        ContentResource instance = new ContentResource();
        Response expResult = null;
        Response result = instance.getYoutubeURL(videoId, videoName);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
