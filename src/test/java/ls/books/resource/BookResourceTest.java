package ls.books.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.sql.DataSource;

import ls.books.WebServicesApplication;
import ls.books.dao.AuthorDao;
import ls.books.dao.BookDao;
import ls.books.dao.FormatDao;
import ls.books.dao.SeriesDao;
import ls.books.db.SchemaBuilder;
import ls.books.domain.Author;
import ls.books.domain.Book;
import ls.books.domain.Format;
import ls.books.domain.Series;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Component;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.skife.jdbi.v2.DBI;

public class BookResourceTest {

    DataSource dataSource;
    AuthorDao testAuthorDao;
    SeriesDao testSeriesDao;
    FormatDao testFormatDao;
    BookDao testBookDao;
    ByteArrayOutputStream baos;
    Component comp;

    @Before
    public void setup() throws Exception {
        dataSource = SchemaBuilder.buildSchema("jdbc:h2:mem:ls-books;DB_CLOSE_DELAY=-1", "password");

        testAuthorDao = new DBI(dataSource).open(AuthorDao.class);
        testSeriesDao = new DBI(dataSource).open(SeriesDao.class);
        testFormatDao = new DBI(dataSource).open(FormatDao.class);
        testBookDao = new DBI(dataSource).open(BookDao.class);

        testAuthorDao.createAuthor(new Author(1, "lastName", "firstName"));
        testSeriesDao.createSeries(new Series(1, 1, "seriesName", "description"));
        testSeriesDao.createSeries(new Series(1, 1, "seriesName", "description"));
        testSeriesDao.createSeries(new Series(1, 1, "seriesName", "description"));
        testFormatDao.createFormat(new Format(1, "name"));
        testFormatDao.createFormat(new Format(1, "name2"));
        
        baos = new ByteArrayOutputStream();
        
        comp = new Component();
        comp.getServers().add(Protocol.HTTP, 8182);

        WebServicesApplication application = new WebServicesApplication(comp.getContext(), dataSource);

        comp.getDefaultHost().attach(application);
        comp.start();
    }

    @After
    public void teardown() throws Exception {
        testAuthorDao.close();
        testSeriesDao.close();
        testFormatDao.close();
        testBookDao.close();
        
        Connection connection = dataSource.getConnection();
        PreparedStatement wipeDatabase = connection.prepareStatement("DROP ALL OBJECTS");
        wipeDatabase.execute();
        wipeDatabase.close();
        connection.close();
        
        comp.stop();
    }

    @Test
    public void staticConstantsShouldEqual() {
        assertEquals("/rest/book/%s", BookResource.BOOK_URL);
    }
    
    @Test
    public void postBookShouldPersistAbookAndReturnALinkToWhereItCanBeAccessed() throws Exception {
        assertNull(testBookDao.findBookByIsbn("123"));
        
        JSONObject newBook = new JSONObject();
        newBook.put("isbn", "123");
        newBook.put("title", "Bar");
        newBook.put("seriesId", "1");
        newBook.put("noSeries", "1");
        newBook.put("formatId", "1");
        newBook.put("noPages", "1");
        newBook.put("notes", "hey");
        
        StringRepresentation bookJson = new StringRepresentation(newBook.toString());
        bookJson.setMediaType(MediaType.APPLICATION_JSON);
        
        ClientResource resource = new ClientResource("http://localhost:8182/rest/book");
        resource.post(bookJson).write(baos);
        
        assertEquals("\"123\"", baos.toString());

        assertEquals(new Book("123", "Bar", 1, 1, 1, 1, "hey"), testBookDao.findBookByIsbn("123"));
    }
    
    @Test
    public void putBookShouldUpdateAnExistingRecordWithTheNewValuesIgnoringTheId() throws ResourceException, IOException, JSONException {
        Book book = new Book("1", "title", 1, 1, 1, 1, "notes");
        testBookDao.createBook(book);
        assertEquals(book, testBookDao.findBookByIsbn("1"));
        
        JSONObject updatedBook = new JSONObject();
        updatedBook.put("isbn", "1");
        updatedBook.put("title", "Bar");
        updatedBook.put("seriesId", "3");
        updatedBook.put("noSeries", "4");
        updatedBook.put("formatId", "2");
        updatedBook.put("noPages", "6");
        updatedBook.put("notes", "hey");
        
        StringRepresentation bookJson = new StringRepresentation(updatedBook.toString());
        bookJson.setMediaType(MediaType.APPLICATION_JSON);

        ClientResource resource = new ClientResource("http://localhost:8182/rest/book");
        resource.setMethod(Method.PUT);
        resource.put(bookJson).write(baos);

        assertEquals("\"Book 1 successfully updated\"", baos.toString());
        assertEquals(new Book("1", "Bar", 3, 4, 2, 6, "hey"), testBookDao.findBookByIsbn("1"));
    }
    
    @Test
    public void deleteBookShouldRemoveTheBookFromTheDatabase() throws ResourceException, IOException {
        Book book = new Book("1", "title", 1, 1, 1, 1, "notes");
        testBookDao.createBook(book);
        assertEquals(book, testBookDao.findBookByIsbn("1"));
        
        ClientResource resource = new ClientResource("http://localhost:8182/rest/book/1");
        resource.delete().write(baos);
        
        assertEquals("\"Book 1 deleted\"", baos.toString());
        
        assertNull(testBookDao.findBookByIsbn("1"));
    }
    
    @Test
    public void getBookWithNoQueryParametersShouldReturnAllBooksInTheDatabase() throws ResourceException, IOException {
        testBookDao.createBook(new Book("1", "title", 1, 1, 1, 1, "notes"));
        testBookDao.createBook(new Book("2", "title2", 1, 1, 1, 1, "notes"));
        testBookDao.createBook(new Book("3", "title3", 1, 1, 1, 1, "notes"));
        
        ClientResource resource = new ClientResource("http://localhost:8182/rest/book");
        resource.get().write(baos);
        
        assertEquals("[{\"isbn\":\"1\",\"title\":\"title\",\"seriesId\":1,\"noSeries\":1,\"formatId\":1,\"noPages\":1,\"notes\":\"notes\"},{\"isbn\":\"2\",\"title\":\"title2\",\"seriesId\":1,\"noSeries\":1,\"formatId\":1,\"noPages\":1,\"notes\":\"notes\"},{\"isbn\":\"3\",\"title\":\"title3\",\"seriesId\":1,\"noSeries\":1,\"formatId\":1,\"noPages\":1,\"notes\":\"notes\"}]", baos.toString());
    }
    
    @Test
    public void getBookWithNoQueryParameterAndNoEntriesInTheDbShouldReturnAnEmptyList() throws ResourceException, IOException {
        ClientResource resource = new ClientResource("http://localhost:8182/rest/book");
        resource.get().write(baos);
        
        assertEquals("[]", baos.toString());
    }
    
    @Test
    public void getBookWithAqueryParameterShouldReturnThatBookFromTheDatabase() throws ResourceException, IOException {
        testBookDao.createBook(new Book("1", "title", 1, 1, 1, 1, "notes"));
        testBookDao.createBook(new Book("2", "title2", 1, 1, 1, 1, "notes"));
        testBookDao.createBook(new Book("3", "title3", 1, 1, 1, 1, "notes"));
        
        ClientResource resource = new ClientResource("http://localhost:8182/rest/book/2");
        resource.get().write(baos);
        
        assertEquals("{\"isbn\":\"2\",\"title\":\"title2\",\"seriesId\":1,\"noSeries\":1,\"formatId\":1,\"noPages\":1,\"notes\":\"notes\"}", baos.toString());
    }
    
    @Test
    public void getBookWithAqueryParameterWithAnIdForAnonExistantBookShouldReturnA404() throws ResourceException, IOException {
        ClientResource resource = new ClientResource("http://localhost:8182/rest/book/4");
        try {
            resource.get();
            fail("A 404 should have been thrown as the book doesnt exist.");
        } catch (ResourceException e) {
            assertEquals("Not Found (404) - The server has not found anything matching the request URI", e.getMessage());
        }
    }
}