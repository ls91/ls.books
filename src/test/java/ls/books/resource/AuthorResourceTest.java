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

public class AuthorResourceTest {

    DataSource dataSource;
    AuthorDao testAuthorDao;
    SeriesDao testSeriesDao;
    BookDao testBookDao;
    FormatDao testFormatDao;
    ByteArrayOutputStream baos;
    Component comp;

    @Before
    public void setup() throws Exception {
        dataSource = SchemaBuilder.buildSchema("jdbc:h2:mem:ls-books;DB_CLOSE_DELAY=-1", "password");

        testAuthorDao = new DBI(dataSource).open(AuthorDao.class);
        testSeriesDao = new DBI(dataSource).open(SeriesDao.class);
        testFormatDao = new DBI(dataSource).open(FormatDao.class);
        testBookDao = new DBI(dataSource).open(BookDao.class);

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
        assertEquals("/rest/author/%d", AuthorResource.AUTHOR_URL);
    }
    
    @Test
    public void postAuthorShouldPersistAnAuthorAndCreateASeriesAndReturnALinkToWhereItCanBeAccessed() throws Exception {
        assertEquals(0, testSeriesDao.findSeriesByAuthorId(1).size());
        assertNull(testAuthorDao.findAuthorById(1));
        
        JSONObject newAuthor = new JSONObject();
        newAuthor.put("authorId", "1234");
        newAuthor.put("lastName", "Foo");
        newAuthor.put("firstName", "Bar");
        
        StringRepresentation authorJson = new StringRepresentation(newAuthor.toString());
        authorJson.setMediaType(MediaType.APPLICATION_JSON);
        
        ClientResource resource = new ClientResource("http://localhost:8182/rest/author");
        resource.post(authorJson).write(baos);
        
        assertEquals("1", baos.toString());

        assertEquals(1, testSeriesDao.findSeriesByAuthorId(1).size());
        assertEquals(new Author(1, "Foo", "Bar"), testAuthorDao.findAuthorById(1));
        assertEquals(new Series(1, 1, "", ""), testSeriesDao.findSeriesByAuthorId(1).get(0));
    }
    
    @Test
    public void putAuthorShouldUpdateAnExistingRecordWithTheNewValuesIgnoringTheId() throws ResourceException, IOException, JSONException {
        Author author = new Author(1, "lastName", "firstName");
        testAuthorDao.createAuthor(author);
        assertEquals(author, testAuthorDao.findAuthorById(1));
        
        JSONObject updatedAuthor = new JSONObject();
        updatedAuthor.put("authorId", "1");
        updatedAuthor.put("lastName", "Foo Updated");
        updatedAuthor.put("firstName", "Bar Updated");
        
        StringRepresentation authorJson = new StringRepresentation(updatedAuthor.toString());
        authorJson.setMediaType(MediaType.APPLICATION_JSON);

        ClientResource resource = new ClientResource("http://localhost:8182/rest/author");
        resource.setMethod(Method.PUT);
        resource.put(authorJson).write(baos);

        assertEquals("\"Author 1 successfully updated\"", baos.toString());
        assertEquals(new Author(1, "Foo Updated", "Bar Updated"), testAuthorDao.findAuthorById(1));
    }
    
    @Test
    public void deleteAuthorShouldRemoveTheAuthorFromTheDatabase() throws ResourceException, IOException {
        Author author = new Author(1, "lastName", "firstName");
        
        testAuthorDao.createAuthor(author);
        
        Author retrievedAuthor = testAuthorDao.findAuthorById(1);
        assertEquals(author, retrievedAuthor);
        
        ClientResource resource = new ClientResource("http://localhost:8182/rest/author/1");
        resource.delete().write(baos);
        
        assertEquals("\"Author 1 deleted\"", baos.toString());
        
        assertNull(testAuthorDao.findAuthorById(1));
    }
    
    @Test
    public void deleteAuthorShouldReturnAnErrorIfTheAuthorHasExistingSeries() throws ResourceException, IOException, JSONException {
        testAuthorDao.createAuthor(new Author(1, "lastName", "firstName"));
        testSeriesDao.createSeries(new Series(1, 1, "seriesName", "description"));
        
        ClientResource resource = new ClientResource("http://localhost:8182/rest/author/1");
        try {
            resource.delete().write(baos);
            fail("A 404 should have been raised.");
        } catch (ResourceException e) {
            assertEquals("\"Delete Failed, Author has existing series.\"", resource.getResponseEntity().getText());
        }
        
        assertEquals(new Author(1, "lastName", "firstName"), testAuthorDao.findAuthorById(1));
    }
    
    @Test
    public void getAuthorWithNoQueryParametersShouldReturnAllAuthorsInTheDatabase() throws ResourceException, IOException {
        Author author1 = new Author(1, "lastName", "firstName");
        Author author2 = new Author(2, "lastName", "firstName");
        Author author3 = new Author(3, "lastName", "firstName");
        
        testAuthorDao.createAuthor(author1);
        testAuthorDao.createAuthor(author2);
        testAuthorDao.createAuthor(author3);
        
        ClientResource resource = new ClientResource("http://localhost:8182/rest/author");
        resource.get().write(baos);
        
        assertEquals("[{\"authorId\":1,\"lastName\":\"lastName\",\"firstName\":\"firstName\"},{\"authorId\":2,\"lastName\":\"lastName\",\"firstName\":\"firstName\"},{\"authorId\":3,\"lastName\":\"lastName\",\"firstName\":\"firstName\"}]", baos.toString());
    }
    
    @Test
    public void getAuthorWithNoQueryParameterAndNoEntriesInTheDbShouldReturnAnEmptyList() throws ResourceException, IOException {
        ClientResource resource = new ClientResource("http://localhost:8182/rest/author");
        resource.get().write(baos);
        
        assertEquals("[]", baos.toString());
    }
    
    @Test
    public void getAuthorSeriesWhenNoSeriesExistShouldReturnAnEmptyList() throws ResourceException, IOException {
        testAuthorDao.createAuthor(new Author(1, "lastName", "firstName"));
        
        ClientResource resource = new ClientResource("http://localhost:8182/rest/author/1/series");
        resource.get().write(baos);
        
        assertEquals("[]", baos.toString());
    }
    
    @Test
    public void getAuthorSeriesWhenSeriesExistShouldReturnThoseSeriesAsaList() throws ResourceException, IOException {
        testAuthorDao.createAuthor(new Author(1, "lastName", "firstName"));
        testAuthorDao.createAuthor(new Author(1, "lastName", "firstName"));
        testSeriesDao.createSeries(new Series(1, 1, "seriesName1", "description"));
        testSeriesDao.createSeries(new Series(1, 1, "seriesName2", "description"));
        testSeriesDao.createSeries(new Series(1, 2, "seriesName3", "description"));
        
        ClientResource resource = new ClientResource("http://localhost:8182/rest/author/1/series");
        resource.get().write(baos);
        
        assertEquals("[{\"seriesId\":1,\"authorId\":1,\"seriesName\":\"seriesName1\",\"description\":\"description\"},{\"seriesId\":2,\"authorId\":1,\"seriesName\":\"seriesName2\",\"description\":\"description\"}]", baos.toString());
    }
    
    @Test
    public void getAuthorBooksWhenNoBooksExistShouldReturnAnEmptyList() throws ResourceException, IOException {
        testAuthorDao.createAuthor(new Author(1, "lastName", "firstName"));
        
        ClientResource resource = new ClientResource("http://localhost:8182/rest/author/1/books");
        resource.get().write(baos);
        
        assertEquals("[]", baos.toString());
    }
    
    @Test
    public void getAuthorBooksWhenBooksExistShouldReturnThoseBooksAsaList() throws ResourceException, IOException {
        testAuthorDao.createAuthor(new Author(1, "lastName", "firstName"));
        testAuthorDao.createAuthor(new Author(1, "lastName", "firstName"));
        testSeriesDao.createSeries(new Series(1, 1, "seriesName1", "description"));
        testSeriesDao.createSeries(new Series(1, 1, "seriesName2", "description"));
        testFormatDao.createFormat(new Format(1, "name"));
        testBookDao.createBook(new Book("isbn", "title", 1, 0, 1, 5, "notes"));
        testBookDao.createBook(new Book("isbn2", "title", 1, 0, 1, 5, "notes"));
        
        ClientResource resource = new ClientResource("http://localhost:8182/rest/author/1/books");
        resource.get().write(baos);
        
        assertEquals("[{\"isbn\":\"isbn\",\"title\":\"title\",\"seriesId\":1,\"noSeries\":0,\"formatId\":1,\"noPages\":5,\"notes\":\"notes\"},{\"isbn\":\"isbn2\",\"title\":\"title\",\"seriesId\":1,\"noSeries\":0,\"formatId\":1,\"noPages\":5,\"notes\":\"notes\"}]", baos.toString());
    }
    
    @Test
    public void getAuthorWithAqueryParameterShouldReturnThatAuthorFromTheDatabase() throws ResourceException, IOException {
        Author author1 = new Author(1, "lastName1", "firstName1");
        Author author2 = new Author(2, "lastName2", "firstName2");
        Author author3 = new Author(3, "lastName3", "firstName3");
        
        testAuthorDao.createAuthor(author1);
        testAuthorDao.createAuthor(author2);
        testAuthorDao.createAuthor(author3);
        
        ClientResource resource = new ClientResource("http://localhost:8182/rest/author/2");
        resource.get().write(baos);
        
        assertEquals("{\"authorId\":2,\"lastName\":\"lastName2\",\"firstName\":\"firstName2\"}", baos.toString());
    }
    
    @Test
    public void getAuthorWithAqueryParameterWithAnIdForAnonExistantAuthorShouldReturnA404() throws ResourceException, IOException {
        Author author1 = new Author(1, "lastName1", "firstName1");
        Author author2 = new Author(2, "lastName2", "firstName2");
        Author author3 = new Author(3, "lastName3", "firstName3");
        
        testAuthorDao.createAuthor(author1);
        testAuthorDao.createAuthor(author2);
        testAuthorDao.createAuthor(author3);
        
        ClientResource resource = new ClientResource("http://localhost:8182/rest/author/4");
        try {
            resource.get();
            fail("A 404 should have been thrown as the author doesnt exist.");
        } catch (ResourceException e) {
            assertEquals("Not Found (404) - The server has not found anything matching the request URI", e.getMessage());
        }
    }
}
