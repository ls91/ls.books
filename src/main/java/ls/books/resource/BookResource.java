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

import ls.books.dao.BookDao;
import ls.books.domain.Book;
import ls.books.domain.Entity;

import org.restlet.Context;
import org.skife.jdbi.v2.DBI;

@Path("/rest/book")
public class BookResource extends BaseResource {

    protected static final String BOOK_URL = "/rest/book/%s";

    private BookDao bookDao = null;

    protected void init() {
        if (bookDao == null) {
            DataSource dataSource = (DataSource) Context.getCurrent().getAttributes().get("DATA_SOURCE");
            bookDao = new DBI(dataSource).open(BookDao.class);
        }
    }

    //Create
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createBook(Book book) throws URISyntaxException {
        init();

        try {
            bookDao.createBook(book);
            return buildEntityCreatedResponse(book.getIsbn(), BOOK_URL);
        } catch (Exception e) {
            return build404Response();
        }
    }

    //Read
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBooks() {
        init();
        return buildOkResponse(bookDao.getBooks());
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBookByBookId(@PathParam("id") String id) {
        init();
        Book result = bookDao.findBookByIsbn(id);

        if (result != null) {
            return buildOkResponse(result);
        } else {
            return build404Response();
        }
    }
    
    //Update
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateBook(Book book) {
        init();

        try {
            bookDao.updateBook(book);
            return buildEntityUpdatedResponse(Entity.Book, book.getIsbn());
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().contains("PUBLIC.BOOK FOREIGN KEY(STATUS_ID) REFERENCES PUBLIC.STATUS(STATUS_ID)")) {
                return build404Response("Update Failed, Status doesn't exists.");
            } if (e.getMessage().contains("PUBLIC.BOOK FOREIGN KEY(FORMAT_ID) REFERENCES PUBLIC.FORMAT(FORMAT_ID)")) {
                return build404Response("Update Failed, Format doesn't exists.");
            } if (e.getMessage().contains("PUBLIC.BOOK FOREIGN KEY(SERIES_ID) REFERENCES PUBLIC.SERIES(SERIES_ID)")) {
                return build404Response("Update Failed, Series doesn't exists.");
            } else {
                return build404Response();
            }
        }
    }

    //Delete
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteBook(@PathParam("id") String id) {
        init();

        bookDao.deleteBookById(id);
        return buildEntityDeletedResponse(Entity.Book, id);
    }
}
