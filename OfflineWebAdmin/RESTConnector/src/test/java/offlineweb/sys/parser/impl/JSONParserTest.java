/*
 * We will decide later!
 */
package offlineweb.sys.parser.impl;

import com.google.gson.Gson;
import java.io.IOException;
import mockit.Mocked;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import offlineweb.sys.parser.ServiceParserException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author uditabose
 */
@RunWith(JMockit.class)
public class JSONParserTest {
    
    
    @Tested JSONParser jSONParser;
    
    @Mocked
    CloseableHttpClient mockedClient;
    
    @Mocked
    HttpGet mockedRequest;
    
    @Mocked
    CloseableHttpResponse mockedResponse;
    
    @Mocked
    Gson gson;
    
    public JSONParserTest() {
    }
    
    /**
     * Test of parse method, of class JSONParser.
     */
    @Test(expected = ServiceParserException.class)
    public void testParseClientExecutionFail() throws IOException {
        System.out.println("testParseClientExecutionFail");
        
        JSONParser parser = new JSONParser();
        parser.parse("", Object.class);
    }
    
    @Test(expected = ServiceParserException.class)
    public void testParseClientNotSuccess() throws IOException {
        System.out.println("testParseClientNotSuccess");
        
        JSONParser parser = new JSONParser();
        parser.parse("http://www.example.com", Object.class);
    }
    
}
