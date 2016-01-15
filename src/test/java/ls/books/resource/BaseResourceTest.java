package ls.books.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.core.CacheControl;

import org.junit.Test;

public class BaseResourceTest {
    
    @Test
    public void getNoCacheControllerShouldReturnAPreConfiguredCacheController() {
        CacheControl result = new BaseResource().getNoCacheController();
        
        assertTrue(result.isNoCache());
        assertEquals(-1, result.getMaxAge());
        assertTrue(result.isMustRevalidate());
    }
}
