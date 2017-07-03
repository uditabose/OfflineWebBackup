
package offlineweb.api.filter;

import java.io.IOException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * adds necessary headers to all response for CORS compliance
 * @author papa2
 */
public class CORSResponseFilter implements ContainerResponseFilter {

    private static final Logger logger = LoggerFactory.getLogger(CORSResponseFilter.class);
    
    @Override
    public void filter(ContainerRequestContext containerRequestContext, 
            ContainerResponseContext containerResponseContext) throws IOException {

        MultivaluedMap<String, Object> headers = containerResponseContext.getHeaders();

        headers.add("Access-Control-Allow-Origin", "*");
        headers.add("Access-Control-Allow-Methods", "GET, POST");
        headers.add("Access-Control-Allow-Headers", 
                "X-Requested-With, Content-Type, origin, accept, authorization");
    }

}
