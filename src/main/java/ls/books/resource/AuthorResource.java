package ls.books.resource;

import javax.sql.DataSource;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import ls.books.dao.AuthorDao;
import ls.books.domain.Author;

import org.restlet.Context;
import org.restlet.data.Form;
import org.skife.jdbi.v2.DBI;

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
    public String createAuthor(Form form) {
        init();
        int id = Integer.parseInt(form.getFirstValue("id"));
        String lastName = form.getFirstValue("lastName");
        String firstName = form.getFirstValue("firstName");

        Author author = new Author(id, lastName, firstName);
        dao.createAuthor(author);

        return "/author/" + author.getId();
    }

    //Read
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getAuthors() {
        init();
        return dao.getAuthors().toString();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getAuthor(@PathParam("id") int id) {
        init();
        return dao.findAuthorById(id).toString();
    }

    //Update
    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String updateAuthor(@PathParam("id") int id, @QueryParam("lastName") String lastName, @QueryParam("firstName") String firstName) {
        init();
        Author newAuthor = new Author(id, lastName, firstName);
        dao.updateAuthor(newAuthor);
        return "Author " + id + " updated";
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
