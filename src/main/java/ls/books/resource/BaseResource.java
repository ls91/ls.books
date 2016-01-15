package ls.books.resource;

import javax.ws.rs.core.CacheControl;

public class BaseResource {

    protected CacheControl getNoCacheController() {
        CacheControl cc = new CacheControl();
        cc.setNoCache(true);
        cc.setMaxAge(-1);
        cc.setMustRevalidate(true);

        return cc;
    }
}
