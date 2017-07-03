/*
 * We will decide later!
 */
package offlineweb.sys.parser.impl;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import org.slf4j.Logger;
import offlineweb.sys.parser.ServiceParserException;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.LoggerFactory;
import offlineweb.sys.parser.ServiceParser;

/**
 * Implementation of WSParser for JSON type output
 * 
 * @author uditabose
 */
public class JSONParser implements ServiceParser {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(JSONParser.class);

    @Override
    public <T> T parse(String wsURL, Class<T> clazz) {
        
        // create http client
        CloseableHttpClient wsClient = HttpClients.createDefault();
        HttpGet wsRequest = new HttpGet(wsURL);                
        CloseableHttpResponse wsResponse = null;
        try {
            wsResponse = wsClient.execute(wsRequest);
            
            // check if response is success
            if (wsResponse.getStatusLine().getStatusCode() != 200) {
                LOGGER.error("Response error for {} {}", 
                        wsURL, statusString(wsResponse.getStatusLine()));
                throw new ServiceParserException(statusString(wsResponse.getStatusLine()));
                        
            }
            
        } catch (IOException ex) {
            LOGGER.error("Can't connect to {}", wsURL, ex);
            throw new ServiceParserException(ex);
        }
        
        // in case of any other reason response is unavailable 
        if (wsResponse == null) {
            LOGGER.error("No response for {}", wsURL);
            throw new ServiceParserException("No response for " + wsURL);
        }
        
        // parse JSON to object
        T wsJson = null;
        Reader wsReader = null;
        try {
            wsReader = new InputStreamReader(wsResponse.getEntity().getContent());
            Gson gson = new Gson();
            wsJson = gson.fromJson(wsReader, clazz);
            
        } catch (IOException | UnsupportedOperationException ex) {
            LOGGER.error("Can't read json response {}", wsURL, ex);
            throw new ServiceParserException(ex);
        } finally {
            try {
                wsReader.close();
            } catch (IOException ex) {
                LOGGER.error("Can't close reader {}", wsReader, ex);
            }
        }
        
        // close response
        try {
            wsResponse.close();
        } catch (IOException ex) {
            LOGGER.error("Can't close response {}", wsResponse, ex);
        }

        // close client
        try {
            wsClient.close();
        } catch (IOException ex) {
            LOGGER.error("Can't close client {}", wsClient, ex);
        }

        return wsJson;
    }

    private String statusString(StatusLine statusLine) {
        return new StringBuilder(statusLine.getStatusCode())
                            .append("\n")
                            .append(statusLine.getReasonPhrase()).toString();
    }
   
}
