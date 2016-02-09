package ls.books.resource;

import java.net.URISyntaxException;

import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ls.books.dao.AuthorDao;
import ls.books.dao.BookDao;
import ls.books.dao.SeriesDao;
import ls.books.domain.Author;
import ls.books.domain.Entity;
import ls.books.domain.Series;

import org.restlet.Context;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;

@Path("/rest/author")
public class AuthorResource extends BaseResource {

    protected static final String AUTHOR_URL = "/rest/author/%s";

    private AuthorDao authorDao = null;
    private SeriesDao seriesDao = null;
    private BookDao bookDao = null;

    protected void init() {
        if (authorDao == null || seriesDao == null || bookDao == null) {
            DataSource dataSource = (DataSource) Context.getCurrent().getAttributes().get("DATA_SOURCE");
            authorDao = new DBI(dataSource).open(AuthorDao.class);
            seriesDao = new DBI(dataSource).open(SeriesDao.class);
            bookDao = new DBI(dataSource).open(BookDao.class);
        }
    }

    //Create
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createAuthor(Author author) throws URISyntaxException {
        init();

        try {
            author.setAuthorId(authorDao.createAuthor(author));
            seriesDao.createSeries(new Series(0, author.getAuthorId(), "", ""));
            return buildEntityCreatedResponse(author.getAuthorId(), AUTHOR_URL);
        } catch (Exception e) {
            if (e.getMessage().contains("Unique index or primary key violation") && e.getMessage().contains("PUBLIC.AUTHOR(LAST_NAME, FIRST_NAME)")) {
                return build404Response("An author of that name already exists");
            } else {
                e.printStackTrace();
                return build404Response(e.getMessage());
            }
        }
    }

    //Read
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAuthors() {
        init();
        return buildOkResponse(authorDao.getAuthors());
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAuthorById(@PathParam("id") int id) {
        init();
        Author result = authorDao.findAuthorById(id);
        
        if (result != null) {
            return buildOkResponse(result);
        } else {
            return build404Response();
        }
    }
    
    @GET
    @Path("/{id}/series")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAuthorSeriesById(@PathParam("id") int id) {
        init();
        return buildOkResponse(seriesDao.findSeriesByAuthorId(id));
    }

    @GET
    @Path("/{id}/books")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAuthorBooksById(@PathParam("id") int id) {
        init();
        return buildOkResponse(bookDao.findBooksByAuthorId(id));
    }

    //Update
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateAuthor(Author author) {
        init();

        try {
            authorDao.updateAuthor(author);
            return buildEntityUpdatedResponse(Entity.Author, author.getAuthorId());
        } catch (Exception e) {
            if (e.getMessage().contains("Unique index or primary key violation") && e.getMessage().contains("PUBLIC.AUTHOR(LAST_NAME, FIRST_NAME)")) {
                return build404Response("An author of that name already exists");
            } else {
                return build404Response(e.getMessage());
            }
        }
    }

    //Delete
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAuthor(@PathParam("id") int id) {
        init();

        try {
            authorDao.deleteAuthorById(id);
            return buildEntityDeletedResponse(Entity.Author, id);
        } catch (UnableToExecuteStatementException e) {
            if (e.getMessage().contains("PUBLIC.SERIES FOREIGN KEY(AUTHOR_ID) REFERENCES PUBLIC.AUTHOR(AUTHOR_ID)")) {
                return build404Response("Delete Failed, Author has existing series.");
            } else {
                return build404Response(e.getMessage());
            }
        }
    }
}
