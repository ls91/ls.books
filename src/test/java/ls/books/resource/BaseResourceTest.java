package ls.books.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.Response;

import ls.books.domain.Format;

import org.junit.Test;

public class BaseResourceTest {
    
    @Test
    public void cacheControllerShouldBePreConfigured() {
        assertTrue(BaseResource.cacheControl.isNoCache());
        assertEquals(-1, BaseResource.cacheControl.getMaxAge());
        assertTrue(BaseResource.cacheControl.isMustRevalidate());
    }
    
    @Test
    public void buildCreatedOkResponseShouldReturnTheresponseContainingTheIdPassedIn() throws URISyntaxException {
        Response result = new BaseResource().buildCreatedOkResponse(5, "rest/api/");
        
        assertEquals(new URI("rest/api/5"), result.getHeaders().get("Location").get(0));
        assertEquals("5", result.getEntity());
    }
    
    @Test
    public void buildOkResponseShouldReturnThePassedInObjectAsJsonInTheBody() {
        assertEquals("{\"formatId\":1,\"name\":\"FORMAT\"}", new BaseResource().buildOkResponse(new Format(1, "FORMAT")).getEntity());
    }
}
