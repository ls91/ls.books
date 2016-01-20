package ls.books.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;

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
        assertEquals(new URI("rest/api/5"), new BaseResource().buildCreatedOkResponse(5, "rest/api/").getHeaders().get("Location").get(0));
        assertEquals("5", new BaseResource().buildCreatedOkResponse(5, "rest/api/").getEntity());
    }
}
