package ls.books.resource;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;

@Path("/")
public class FrontendResource {

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("{path:.*}")
    public String Get(@PathParam("path") String path) throws IOException {
        return IOUtils.toString(getClass().getResourceAsStream("/" + path));
    }
}
