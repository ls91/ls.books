package ls.books.resource;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;

import ls.books.domain.Entity;

import com.google.gson.Gson;

public class BaseResource {

    protected static CacheControl cacheControl;
    private Gson jsonBuilder = new Gson();

    protected static final String ENTITY_UPDATED = "%s %s successfully updated";
    protected static final String ENTITY_DELETED = "%s %s deleted";

    static {
        cacheControl = new CacheControl();
        cacheControl.setNoCache(true);
        cacheControl.setMaxAge(-1);
        cacheControl.setMustRevalidate(true);
    }

    protected Response buildEntityCreatedResponse(final int id, final String url) throws URISyntaxException {
        return buildEntityCreatedResponse(Integer.toString(id), url);
    }
    
    protected Response buildEntityCreatedResponse(final String id, final String url) throws URISyntaxException {
        return Response.created(new URI(String.format(url, id))).cacheControl(cacheControl).entity(jsonBuilder.toJson(id)).build();
    }

    protected Response buildEntityUpdatedResponse(final Entity entity, final int id) {
        return buildEntityResponse(ENTITY_UPDATED, entity, Integer.toString(id));
    }
    
    protected Response buildEntityUpdatedResponse(final Entity entity, final String id) {
        return buildEntityResponse(ENTITY_UPDATED, entity, id);
    }

    protected Response buildEntityDeletedResponse(final Entity entity, final int id) {
        return buildEntityResponse(ENTITY_DELETED, entity, Integer.toString(id));
    }
    
    protected Response buildEntityDeletedResponse(final Entity entity, final String id) {
        return buildEntityResponse(ENTITY_DELETED, entity, id);
    }
    
    protected Response buildEntityResponse(final String message, final Entity entity, final String id) {
        return Response.ok().cacheControl(cacheControl).entity(jsonBuilder.toJson(String.format(message, entity.name(), id))).build();
    }

    protected Response buildOkResponse(final Object object) {
        return Response.ok().cacheControl(cacheControl).entity(jsonBuilder.toJson(object)).build();
    }

    protected Response build404Response(final Object object) {
        return Response.status(404).cacheControl(cacheControl).entity(jsonBuilder.toJson(object)).build();
    }
}
