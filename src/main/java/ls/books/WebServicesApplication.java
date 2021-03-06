package ls.books;

import java.util.HashSet;
import java.util.Set;

import javax.sql.DataSource;
import javax.ws.rs.core.Application;

import ls.books.resource.AuthorResource;
import ls.books.resource.BookResource;
import ls.books.resource.FormatResource;
import ls.books.resource.FrontendResource;
import ls.books.resource.SeriesResource;
import ls.books.resource.StatusResource;

import org.restlet.Context;
import org.restlet.ext.jaxrs.JaxRsApplication;

public class WebServicesApplication extends JaxRsApplication {

    public WebServicesApplication(Context context, DataSource dataSource) {
        super(context);
        context.getAttributes().put("DATA_SOURCE", dataSource);
        this.add(new JaxRsApplicationImpl());
    }

    private static class JaxRsApplicationImpl extends Application {
        @Override
        public Set<Class<?>> getClasses() {
            Set<Class<?>> resourceClasses = new HashSet<Class<?>>();
            resourceClasses.add(FrontendResource.class);
            resourceClasses.add(AuthorResource.class);
            resourceClasses.add(SeriesResource.class);
            resourceClasses.add(FormatResource.class);
            resourceClasses.add(StatusResource.class);
            resourceClasses.add(BookResource.class);

            return resourceClasses;
        }
    }
}
