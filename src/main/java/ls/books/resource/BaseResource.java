package ls.books.resource;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;

import ls.books.domain.Entity;

import com.google.gson.Gson;

public class BaseResource {

    protected static CacheControl cacheControl;
    protected Gson jsonBuilder = new Gson();

    protected static final String ENTITY_UPDATED = "%s %d successfully updated";
    protected static final String ENTITY_DELETED = "%s %d deleted";

    static {
        cacheControl = new CacheControl();
        cacheControl.setNoCache(true);
        cacheControl.setMaxAge(-1);
        cacheControl.setMustRevalidate(true);
    }

    protected Response buildEntityCreatedResponse(final int id, final String url) throws URISyntaxException {
        return Response.created(new URI(String.format(url, id))).cacheControl(cacheControl).entity(jsonBuilder.toJson(id)).build();
    }

    protected Response buildEntityUpdatedResponse(final Entity entity, final int id) {
        return Response.ok().cacheControl(cacheControl).entity(jsonBuilder.toJson(String.format(ENTITY_UPDATED, entity.name(), id))).build();
    }

    protected Response buildEntityDeletedResponse(final Entity entity, final int id) {
        return Response.ok().cacheControl(cacheControl).entity(jsonBuilder.toJson(String.format(ENTITY_DELETED, entity.name(), id))).build();
    }

    protected Response buildOkResponse(final Object object) {
        return Response.ok().cacheControl(cacheControl).entity(jsonBuilder.toJson(object)).build();
    }

    protected Response build404Response() {
        return Response.status(404).cacheControl(cacheControl).build();
    }

    protected Response build404Response(final Object object) {
        return Response.status(404).cacheControl(cacheControl).entity(jsonBuilder.toJson(object)).build();
    }
}
