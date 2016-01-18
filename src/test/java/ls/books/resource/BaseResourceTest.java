package ls.books.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BaseResourceTest {
    
    @Test
    public void cacheControllerShouldBePreConfigured() {
        assertTrue(BaseResource.cacheControl.isNoCache());
        assertEquals(-1, BaseResource.cacheControl.getMaxAge());
        assertTrue(BaseResource.cacheControl.isMustRevalidate());
    }
}
