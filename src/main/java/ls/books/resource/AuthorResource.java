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
import ls.books.dao.SeriesDao;
import ls.books.domain.Author;

import org.restlet.Context;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;

@Path("/rest/author")
public class AuthorResource extends BaseResource {

    private AuthorDao authorDao = null;
    private SeriesDao seriesDao = null;

    protected void init() {
        if (authorDao == null || seriesDao == null) {
            DataSource dataSource = (DataSource) Context.getCurrent().getAttributes().get("DATA_SOURCE");
            authorDao = new DBI(dataSource).open(AuthorDao.class);
            seriesDao = new DBI(dataSource).open(SeriesDao.class);
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
        } catch (Exception e) {
            return Response.serverError().cacheControl(cacheControl).build();
        }

        return buildCreatedOkResponse(author.getAuthorId(), "/rest/author/");
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
            return Response.status(404).cacheControl(cacheControl).build();
        }
    }
    
    @GET
    @Path("/{id}/series")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAuthorSeriesById(@PathParam("id") int id) {
        init();
        return Response.ok().cacheControl(cacheControl).entity(jsonBuilder.toJson(seriesDao.findSeriesByAuthorId(id))).build();
    }

    //Update
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateAuthor(Author author) {
        init();

        try {
            authorDao.updateAuthor(author);
        } catch (Exception e) {
            Response.status(400).cacheControl(cacheControl).build();
        }

        return Response.ok().cacheControl(cacheControl).entity(jsonBuilder.toJson("Author " + author.getAuthorId() + " updated")).build();
    }

    //Delete
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAuthor(@PathParam("id") int id) {
        init();

        try {
            authorDao.deleteAuthorById(id);
        } catch (UnableToExecuteStatementException e) {
            if (e.getMessage().contains("PUBLIC.SERIES FOREIGN KEY(AUTHOR_ID) REFERENCES PUBLIC.AUTHOR(AUTHOR_ID)")) {
                return Response.status(404).cacheControl(cacheControl).entity(jsonBuilder.toJson("Delete Failed, Author has existing series.")).build();
            }
        }
        return buildOkResponse("Author " + id + " deleted");
    }
}
