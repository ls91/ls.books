package ls.books.router;

import javax.sql.DataSource;

import ls.books.resource.AuthorResource;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;
 
public class AuthorRouter extends Application {

    private DataSource dataSource;

    public AuthorRouter(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public synchronized Restlet createInboundRoot() {
        Router router = new Router(getContext());
        
        router.getContext().getAttributes().put("DATA_SOURCE", dataSource);


        router.attach("/author/{id}", AuthorResource.class);
        return router;
    }
}
