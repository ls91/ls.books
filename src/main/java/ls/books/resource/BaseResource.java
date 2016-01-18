package ls.books.resource;

import javax.ws.rs.core.CacheControl;

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
}
