package ls.books.resource;

import javax.sql.DataSource;

import ls.books.dao.AuthorDao;
import ls.books.domain.Author;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.skife.jdbi.v2.DBI;

public class AuthorResource extends ServerResource {

    private AuthorDao dao;

    protected void doInit() throws ResourceException {
        DataSource dataSource = (DataSource) getContext().getAttributes().get("DATA_SOURCE");
        dao = new DBI(dataSource).open(AuthorDao.class);
    }

    //Create
    @Post
    public Representation createAuthor(Form form) {  
        int id = Integer.parseInt(form.getFirstValue("id"));
        String lastName = form.getFirstValue("lastName");
        String firstName = form.getFirstValue("firstName");

        Author author = new Author(id, lastName, firstName);
        dao.createAuthor(author);

        return new StringRepresentation("/author/" + author.getId(), MediaType.TEXT_PLAIN);
    } 

    //Read
    @Get  
    public String getAuthor() {
        int id = Integer.parseInt((String) getRequestAttributes().get("id"));

        return dao.findAuthorById(id).toString();
    }

    //Update

    //Delete
}
