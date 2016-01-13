package ls.books.resource;

import java.net.URI;
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
import ls.books.domain.Author;

import org.restlet.Context;
import org.restlet.data.Form;
import org.skife.jdbi.v2.DBI;

import com.google.gson.Gson;

@Path("/rest/author")
public class AuthorResource {

    private AuthorDao dao = null;

    protected void init() {
        if (dao == null) {
            DataSource dataSource = (DataSource) Context.getCurrent().getAttributes().get("DATA_SOURCE");
            dao = new DBI(dataSource).open(AuthorDao.class);
        }
    }

    //Create
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response createAuthor(Form form) throws URISyntaxException {
        init();
        int id = Integer.parseInt(form.getFirstValue("id"));
        String lastName = form.getFirstValue("lastName");
        String firstName = form.getFirstValue("firstName");

        Author author = new Author(id, lastName, firstName);
        dao.createAuthor(author);

        return Response.created(new URI("/rest/author/" + author.getAuthorId())).build();
    }

    //Read
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAuthors() {
        init();
        return Response.ok().entity(new Gson().toJson(dao.getAuthors())).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAuthorById(@PathParam("id") int id) {
        init();
        Author result = dao.findAuthorById(id);
        
        if (result != null) {
            return Response.ok().entity(new Gson().toJson(result)).build();
        } else {
            return Response.status(404).build();
        }
    }

    //Update
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateAuthor(Author author) {
        init();

        try {
            dao.updateAuthor(author);
        } catch (Exception e) {
            e.printStackTrace();
            Response.status(400).build();
        }

        return Response.ok().entity(new Gson().toJson("Author " + author.getAuthorId() + " updated")).build();
    }

    //Delete
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteAuthor(@PathParam("id") int id) {
        init();
        dao.deleteAuthorById(id);
        return "Author " + id + " deleted";
    }
}
