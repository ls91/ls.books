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
import ls.books.dao.StatusDao;
import ls.books.db.SchemaBuilder;
import ls.books.domain.Author;
import ls.books.domain.Book;
import ls.books.domain.Format;
import ls.books.domain.Series;
import ls.books.domain.Status;

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
    StatusDao testStatusDao;
    BookDao testBookDao;
    ByteArrayOutputStream baos;
    Component comp;

    @Before
    public void setup() throws Exception {
        dataSource = SchemaBuilder.getDataSource(null, "password");
        SchemaBuilder.buildSchema(dataSource);

        testAuthorDao = new DBI(dataSource).open(AuthorDao.class);
        testSeriesDao = new DBI(dataSource).open(SeriesDao.class);
        testFormatDao = new DBI(dataSource).open(FormatDao.class);
        testStatusDao = new DBI(dataSource).open(StatusDao.class);
        testBookDao = new DBI(dataSource).open(BookDao.class);

        testAuthorDao.createAuthor(new Author(1, "lastName", "firstName"));
        testAuthorDao.createAuthor(new Author(1, "lastName2", "firstName"));
        testSeriesDao.createSeries(new Series(1, 1, "seriesName1", "description"));
        testSeriesDao.createSeries(new Series(1, 1, "seriesName2", "description"));
        testSeriesDao.createSeries(new Series(1, 1, "seriesName3", "description"));
        testFormatDao.createFormat(new Format(1, "name"));
        testFormatDao.createFormat(new Format(1, "name2"));
        testStatusDao.createStatus(new Status(1, "name"));
        testStatusDao.createStatus(new Status(1, "name2"));
        
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
        testStatusDao.close();
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
        assertNull(testBookDao.findBookById(1));
        
        JSONObject newBook = new JSONObject();
        newBook.put("bookId", "1");
        newBook.put("isbn", "123");
        newBook.put("title", "Bar");
        newBook.put("authorId", "1");
        newBook.put("seriesId", "1");
        newBook.put("noSeries", "1");
        newBook.put("formatId", "1");
        newBook.put("statusId", "1");
        newBook.put("noPages", "1");
        newBook.put("notes", "hey");
        
        StringRepresentation bookJson = new StringRepresentation(newBook.toString());
        bookJson.setMediaType(MediaType.APPLICATION_JSON);
        
        ClientResource resource = new ClientResource("http://localhost:8182/rest/book");
        resource.post(bookJson).write(baos);
        resource.release();
        
        assertEquals("\"1\"", baos.toString());

        assertEquals(new Book(1, "123", "Bar", 1, 1, 1, 1, 1, 1, "hey"), testBookDao.findBookById(1));
    }
    
    @Test
    public void putBookShouldUpdateAnExistingRecordWithTheNewValuesIgnoringTheId() throws ResourceException, IOException, JSONException {
        Book book = new Book(1, "1", "title", 1, 1, 1, 1, 1, 1, "notes");
        testBookDao.createBook(book);
        assertEquals(book, testBookDao.findBookById(1));
        
        JSONObject updatedBook = new JSONObject();
        updatedBook.put("bookId", "1");
        updatedBook.put("isbn", "2");
        updatedBook.put("title", "Bar");
        updatedBook.put("authorId", "2");
        updatedBook.put("seriesId", "3");
        updatedBook.put("noSeries", "4");
        updatedBook.put("formatId", "2");
        updatedBook.put("statusId", "2");
        updatedBook.put("noPages", "6");
        updatedBook.put("notes", "hey");
        
        StringRepresentation bookJson = new StringRepresentation(updatedBook.toString());
        bookJson.setMediaType(MediaType.APPLICATION_JSON);

        ClientResource resource = new ClientResource("http://localhost:8182/rest/book");
        resource.setMethod(Method.PUT);
        resource.put(bookJson).write(baos);
        resource.release();

        assertEquals("\"Book 1 successfully updated\"", baos.toString());
        assertEquals(new Book(1, "2", "Bar", 1, 3, 4, 2, 2, 6, "hey"), testBookDao.findBookById(1));
    }
    
    @Test
    public void putBookShouldNotUpdateAnExistingRecordIfTheStatusDoesntExist() throws ResourceException, IOException, JSONException {
        Book book = new Book(1, "1", "title", 1, 1, 1, 1, 1, 1, "notes");
        testBookDao.createBook(book);
        assertEquals(book, testBookDao.findBookById(1));
        
        JSONObject updatedBook = new JSONObject();
        updatedBook.put("bookId", "1");
        updatedBook.put("isbn", "1");
        updatedBook.put("title", "Bar");
        updatedBook.put("authorId", "1");
        updatedBook.put("seriesId", "3");
        updatedBook.put("noSeries", "4");
        updatedBook.put("formatId", "2");
        updatedBook.put("statusId", "5");
        updatedBook.put("noPages", "6");
        updatedBook.put("notes", "hey");
        
        StringRepresentation bookJson = new StringRepresentation(updatedBook.toString());
        bookJson.setMediaType(MediaType.APPLICATION_JSON);

        ClientResource resource = new ClientResource("http://localhost:8182/rest/book");
        resource.setMethod(Method.PUT);
        try {
            resource.put(bookJson).write(baos);
            fail("Update should have failed as the new status doesnt exist");
        } catch (Exception e) {
            assertEquals("Not Found (404) - The server has not found anything matching the request URI", e.getMessage());
        }
        resource.release();
        assertEquals(book, testBookDao.findBookById(1));
    }
    
    @Test
    public void putBookShouldNotUpdateAnExistingRecordIfTheFormatDoesntExist() throws ResourceException, IOException, JSONException {
        Book book = new Book(1, "1", "title", 1, 1, 1, 1, 1, 1, "notes");
        testBookDao.createBook(book);
        assertEquals(book, testBookDao.findBookById(1));
        
        JSONObject updatedBook = new JSONObject();
        updatedBook.put("bookId", "1");
        updatedBook.put("isbn", "1");
        updatedBook.put("title", "Bar");
        updatedBook.put("authorId", "1");
        updatedBook.put("seriesId", "3");
        updatedBook.put("noSeries", "4");
        updatedBook.put("formatId", "5");
        updatedBook.put("statusId", "1");
        updatedBook.put("noPages", "6");
        updatedBook.put("notes", "hey");
        
        StringRepresentation bookJson = new StringRepresentation(updatedBook.toString());
        bookJson.setMediaType(MediaType.APPLICATION_JSON);

        ClientResource resource = new ClientResource("http://localhost:8182/rest/book");
        resource.setMethod(Method.PUT);
        try {
            resource.put(bookJson).write(baos);
            fail("Update should have failed as the new format doesnt exist");
        } catch (Exception e) {
            assertEquals("Not Found (404) - The server has not found anything matching the request URI", e.getMessage());
        }
        resource.release();
        
        assertEquals(book, testBookDao.findBookById(1));
    }
    
    @Test
    public void putBookShouldNotUpdateAnExistingRecordIfTheSeriesDoesntExist() throws ResourceException, IOException, JSONException {
        Book book = new Book(1, "1", "title", 1, 1, 1, 1, 1, 1, "notes");
        testBookDao.createBook(book);
        assertEquals(book, testBookDao.findBookById(1));
        
        JSONObject updatedBook = new JSONObject();
        updatedBook.put("bookId", "1");
        updatedBook.put("isbn", "1");
        updatedBook.put("title", "Bar");
        updatedBook.put("authorId", "1");
        updatedBook.put("seriesId", "7");
        updatedBook.put("noSeries", "1");
        updatedBook.put("formatId", "1");
        updatedBook.put("statusId", "1");
        updatedBook.put("noPages", "6");
        updatedBook.put("notes", "hey");
        
        StringRepresentation bookJson = new StringRepresentation(updatedBook.toString());
        bookJson.setMediaType(MediaType.APPLICATION_JSON);

        ClientResource resource = new ClientResource("http://localhost:8182/rest/book");
        resource.setMethod(Method.PUT);
        try {
            resource.put(bookJson).write(baos);
            fail("Update should have failed as the new series doesnt exist");
        } catch (Exception e) {
            assertEquals("Not Found (404) - The server has not found anything matching the request URI", e.getMessage());
        }
        resource.release();
        
        assertEquals(book, testBookDao.findBookById(1));
    }
    
    @Test
    public void putBookShouldNotUpdateAnExistingRecordIfTheAuthorDoesntExist() throws ResourceException, IOException, JSONException {
        Book book = new Book(1, "1", "title", 1, 1, 1, 1, 1, 1, "notes");
        testBookDao.createBook(book);
        assertEquals(book, testBookDao.findBookById(1));
        
        JSONObject updatedBook = new JSONObject();
        updatedBook.put("bookId", "1");
        updatedBook.put("isbn", "1");
        updatedBook.put("title", "Bar");
        updatedBook.put("authorId", "100");
        updatedBook.put("seriesId", "1");
        updatedBook.put("noSeries", "1");
        updatedBook.put("formatId", "1");
        updatedBook.put("statusId", "1");
        updatedBook.put("noPages", "6");
        updatedBook.put("notes", "hey");
        
        StringRepresentation bookJson = new StringRepresentation(updatedBook.toString());
        bookJson.setMediaType(MediaType.APPLICATION_JSON);

        ClientResource resource = new ClientResource("http://localhost:8182/rest/book");
        resource.setMethod(Method.PUT);
        try {
            resource.put(bookJson).write(baos);
            fail("Update should have failed as the new author doesnt exist");
        } catch (Exception e) {
            assertEquals("Not Found (404) - The server has not found anything matching the request URI", e.getMessage());
        }
        resource.release();
        
        assertEquals(book, testBookDao.findBookById(1));
    }
    
    @Test
    public void deleteBookShouldRemoveTheBookFromTheDatabase() throws ResourceException, IOException {
        Book book = new Book(1, "1", "title", 1, 1, 1, 1, 1, 1, "notes");
        testBookDao.createBook(book);
        assertEquals(book, testBookDao.findBookById(1));
        
        ClientResource resource = new ClientResource("http://localhost:8182/rest/book/1");
        resource.delete().write(baos);
        resource.release();
        
        assertEquals("\"Book 1 deleted\"", baos.toString());
        
        assertNull(testBookDao.findBookById(1));
    }
    
    @Test
    public void getBookWithNoQueryParametersShouldReturnAllBooksInTheDatabase() throws ResourceException, IOException {
        testBookDao.createBook(new Book(1, "1", "title", 1, 1, 1, 1, 1, 1, "notes"));
        testBookDao.createBook(new Book(2, "2", "title2", 1, 1, 1, 1, 1, 1, "notes"));
        testBookDao.createBook(new Book(3, "3", "title3", 1, 1, 1, 1, 1, 1, "notes"));
        
        ClientResource resource = new ClientResource("http://localhost:8182/rest/book");
        resource.get().write(baos);
        resource.release();
        
        assertEquals("[{\"bookId\":1,\"isbn\":\"1\",\"title\":\"title\",\"authorId\":1,\"seriesId\":1,\"noSeries\":1,\"formatId\":1,\"statusId\":1,\"noPages\":1,\"notes\":\"notes\"},{\"bookId\":2,\"isbn\":\"2\",\"title\":\"title2\",\"authorId\":1,\"seriesId\":1,\"noSeries\":1,\"formatId\":1,\"statusId\":1,\"noPages\":1,\"notes\":\"notes\"},{\"bookId\":3,\"isbn\":\"3\",\"title\":\"title3\",\"authorId\":1,\"seriesId\":1,\"noSeries\":1,\"formatId\":1,\"statusId\":1,\"noPages\":1,\"notes\":\"notes\"}]", baos.toString());
    }
    
    @Test
    public void getBookWithNoQueryParameterAndNoEntriesInTheDbShouldReturnAnEmptyList() throws ResourceException, IOException {
        ClientResource resource = new ClientResource("http://localhost:8182/rest/book");
        resource.get().write(baos);
        resource.release();
        
        assertEquals("[]", baos.toString());
    }
    
    @Test
    public void getBookWithAqueryParameterShouldReturnThatBookFromTheDatabase() throws ResourceException, IOException {
        testBookDao.createBook(new Book(1, "1", "title", 1, 1, 1, 1, 1, 1, "notes"));
        testBookDao.createBook(new Book(2, "2", "title2", 1, 1, 1, 1, 1, 1, "notes"));
        testBookDao.createBook(new Book(3, "3", "title3", 1, 1, 1, 1, 1, 1, "notes"));
        
        ClientResource resource = new ClientResource("http://localhost:8182/rest/book/2");
        resource.get().write(baos);
        resource.release();
        
        assertEquals("{\"bookId\":2,\"isbn\":\"2\",\"title\":\"title2\",\"authorId\":1,\"seriesId\":1,\"noSeries\":1,\"formatId\":1,\"statusId\":1,\"noPages\":1,\"notes\":\"notes\"}", baos.toString());
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
        resource.release();
    }
}
