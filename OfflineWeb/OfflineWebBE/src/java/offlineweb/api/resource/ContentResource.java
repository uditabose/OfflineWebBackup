
package offlineweb.api.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static offlineweb.api.util.ContentUtil.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 *
 * @author papa
 */

@Path("/content")
public class ContentResource {
    private static final Logger logger
            = LoggerFactory.getLogger(ContentResource.class);
    
    @GET
    @Path("/wiki")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getWikiText(@QueryParam("id") String articleId,
            @QueryParam("name") String articleName) {
        
        logger.info("Wiki for : id {} name {}", articleId, articleName);
        return Response.ok(getWikiContent(articleName, articleId)).build();
    }
    
    @GET
    @Path("/guten")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getGutenText(@QueryParam("id") String bookId,
            @QueryParam("name") String bookName) {       
        
        logger.info("Guten for : id {} name {}", bookId, bookName);
        return Response.ok(getGutenContent(bookName, bookId)).build();    
    }
    
    @GET
    @Path("/youtube")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getYoutubeURL(@QueryParam("id") String videoId,
            @QueryParam("name") String videoName) {
 
        logger.info("Youtube for : id {} name {}", videoId, videoName);
        return Response.ok(getYoutubeLink(videoName, videoId)).build();
    }
    
}
