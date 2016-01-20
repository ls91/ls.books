package ls.books.resource;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

public class BaseResource {

    protected static CacheControl cacheControl;
    protected Gson jsonBuilder = new Gson();

    static {
        cacheControl = new CacheControl();
        cacheControl.setNoCache(true);
        cacheControl.setMaxAge(-1);
        cacheControl.setMustRevalidate(true);
    }

    protected Response buildCreatedOkResponse(final int id, final String url) throws URISyntaxException {
        return Response.created(new URI(url + id)).cacheControl(cacheControl).entity(jsonBuilder.toJson(id)).build();
    }

    public Response buildOkResponse(Object object) {
        return Response.ok().cacheControl(cacheControl).entity(jsonBuilder.toJson(object)).build();
    }
}
