package ls.books.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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

public class SeriesResourceTest {

    DataSource dataSource;
    AuthorDao testAuthorDao;
    SeriesDao testSeriesDao;
    BookDao testBookDao;
    FormatDao testFormatDao;
    StatusDao testStatusDao;
    ByteArrayOutputStream baos;
    Component comp;

    @Before
    public void setup() throws Exception {
        dataSource = SchemaBuilder.getDataSource(null, "password");
        SchemaBuilder.buildSchema(dataSource);

        testAuthorDao = new DBI(dataSource).open(AuthorDao.class);
        testSeriesDao = new DBI(dataSource).open(SeriesDao.class);
        testBookDao = new DBI(dataSource).open(BookDao.class);
        testFormatDao = new DBI(dataSource).open(FormatDao.class);
        testStatusDao = new DBI(dataSource).open(StatusDao.class);

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
        testBookDao.close();
        testFormatDao.close();
        testStatusDao.close();
        
        Connection connection = dataSource.getConnection();
        PreparedStatement wipeDatabase = connection.prepareStatement("DROP ALL OBJECTS");
        wipeDatabase.execute();
        wipeDatabase.close();
        connection.close();
        
        comp.stop();
    }

    @Test
    public void staticConstantsShouldEqual() {
        assertEquals("/rest/series/%s", SeriesResource.SERIES_URL);
    }

    @Test
    public void postSeriesShouldPersistAseriesAndReturnAlinkToWhereItCanBeAccessed() throws Exception {
        testAuthorDao.createAuthor(new Author(1, "FOO", "BAR"));
        assertNotNull(testAuthorDao.findAuthorById(1));
        
        assertNull(testSeriesDao.findSeriesById(1));
        
        JSONObject newSeries = new JSONObject();
        newSeries.put("seriesId", "1");
        newSeries.put("authorId", "1");
        newSeries.put("seriesName", "name");
        newSeries.put("description", "");
        
        StringRepresentation seriesJson = new StringRepresentation(newSeries.toString());
        seriesJson.setMediaType(MediaType.APPLICATION_JSON);
        
        ClientResource resource = new ClientResource("http://localhost:8182/rest/series");
        resource.post(seriesJson).write(baos);
        resource.release();
        
        assertEquals("\"1\"", baos.toString());

        assertEquals(new Series(1, 1, "name", ""), testSeriesDao.findSeriesById(1));
    }
    
    @Test
    public void putSeriesShouldUpdateAnExistingRecordWithTheNewValuesIgnoringTheId() throws ResourceException, IOException, JSONException {
        testAuthorDao.createAuthor(new Author(1, "lastName1", "firstName"));
        testAuthorDao.createAuthor(new Author(2, "lastName2", "firstName"));
        
        Series series1 = new Series(1, 1, "seriesName1", "description");
        testSeriesDao.createSeries(series1);
        
        assertEquals(series1, testSeriesDao.findSeriesById(1));
        
        JSONObject updatedSeries = new JSONObject();
        updatedSeries.put("seriesId", "1");
        updatedSeries.put("authorId", "2");
        updatedSeries.put("seriesName", "name");
        updatedSeries.put("description", "");
        
        StringRepresentation seriesJson = new StringRepresentation(updatedSeries.toString());
        seriesJson.setMediaType(MediaType.APPLICATION_JSON);

        ClientResource resource = new ClientResource("http://localhost:8182/rest/series");
        resource.setMethod(Method.PUT);
        resource.put(seriesJson).write(baos);
        resource.release();

        assertEquals("\"Series 1 successfully updated\"", baos.toString());
        assertEquals(new Series(1, 2, "name", ""), testSeriesDao.findSeriesById(1));
    }
    
    @Test
    public void deleteSeriesShouldRemoveTheSeriesFromTheDatabase() throws ResourceException, IOException {
        Author author1 = new Author(1, "lastName", "firstName");
        testAuthorDao.createAuthor(author1);
        
        Series series1 = new Series(1, 1, "seriesName1", "description");
        testSeriesDao.createSeries(series1);
        
        assertEquals(series1, testSeriesDao.findSeriesById(1));
        
        ClientResource resource = new ClientResource("http://localhost:8182/rest/series/1");
        resource.delete().write(baos);
        resource.release();
        
        assertEquals("\"Series 1 deleted\"", baos.toString());
        
        assertNull(testSeriesDao.findSeriesById(1));
    }
    
    @Test
    public void getSeriesWithNoQueryParametersShouldReturnAllSeriesInTheDatabase() throws ResourceException, IOException {
        testAuthorDao.createAuthor(new Author(1, "lastName", "firstName"));

        testSeriesDao.createSeries(new Series(1, 1, "seriesName1", "description"));
        testSeriesDao.createSeries(new Series(2, 1, "seriesName2", "description"));
        testSeriesDao.createSeries(new Series(3, 1, "seriesName3", "description"));
        
        ClientResource resource = new ClientResource("http://localhost:8182/rest/series");
        resource.get().write(baos);
        resource.release();
        
        assertEquals("[{\"seriesId\":1,\"authorId\":1,\"seriesName\":\"seriesName1\",\"description\":\"description\"},{\"seriesId\":2,\"authorId\":1,\"seriesName\":\"seriesName2\",\"description\":\"description\"},{\"seriesId\":3,\"authorId\":1,\"seriesName\":\"seriesName3\",\"description\":\"description\"}]", baos.toString());
    }
    
    @Test
    public void getSeriesWithNoQueryParameterAndNoEntriesInTheDbShouldReturnAnEmptyList() throws ResourceException, IOException {
        ClientResource resource = new ClientResource("http://localhost:8182/rest/series");
        resource.get().write(baos);
        resource.release();
        
        assertEquals("[]", baos.toString());
    }
    
    @Test
    public void getSeriesWithAqueryParameterShouldReturnThatSeriesFromTheDatabase() throws ResourceException, IOException {
        testAuthorDao.createAuthor(new Author(1, "lastName", "firstName"));

        testSeriesDao.createSeries(new Series(1, 1, "seriesName1", "description"));
        testSeriesDao.createSeries(new Series(2, 1, "seriesName2", "description"));
        testSeriesDao.createSeries(new Series(3, 1, "seriesName3", "description"));
        
        ClientResource resource = new ClientResource("http://localhost:8182/rest/series/2");
        resource.get().write(baos);
        resource.release();
        
        assertEquals("{\"seriesId\":2,\"authorId\":1,\"seriesName\":\"seriesName2\",\"description\":\"description\"}", baos.toString());
    }
    
    @Test
    public void getBooksInSeriesWithAqueryParameterShouldReturnAllBooksInThatSeriesFromTheDatabase() throws ResourceException, IOException {
        Author author1 = new Author(1, "lastName", "firstName");
        testAuthorDao.createAuthor(author1);
        
        testSeriesDao.createSeries(new Series(1, 1, "seriesName1", "description"));
        testSeriesDao.createSeries(new Series(2, 1, "seriesName2", "description"));
        testStatusDao.createStatus(new Status(1, "name"));
        testFormatDao.createFormat(new Format(1, "name"));
        
        testBookDao.createBook(new Book(1, "1", "title", 1, 1, 1, 1, 1, "notes"));
        testBookDao.createBook(new Book(2, "2", "title2", 1, 1, 1, 1, 1, "notes"));
        testBookDao.createBook(new Book(3, "3", "title3", 2, 1, 1, 1, 1, "notes"));
        
        ClientResource resource = new ClientResource("http://localhost:8182/rest/series/2/books");
        resource.get().write(baos);
        resource.release();
        
        assertEquals("[{\"authorId\":1,\"bookId\":3,\"isbn\":\"3\",\"title\":\"title3\",\"seriesId\":2,\"noSeries\":1,\"formatId\":1,\"statusId\":1,\"noPages\":1,\"notes\":\"notes\"}]", baos.toString());
        
        resource = new ClientResource("http://localhost:8182/rest/series/1/books");
        resource.get().write(baos);
        
        assertEquals("[{\"authorId\":1,\"bookId\":3,\"isbn\":\"3\",\"title\":\"title3\",\"seriesId\":2,\"noSeries\":1,\"formatId\":1,\"statusId\":1,\"noPages\":1,\"notes\":\"notes\"}][{\"authorId\":1,\"bookId\":1,\"isbn\":\"1\",\"title\":\"title\",\"seriesId\":1,\"noSeries\":1,\"formatId\":1,\"statusId\":1,\"noPages\":1,\"notes\":\"notes\"},{\"authorId\":1,\"bookId\":2,\"isbn\":\"2\",\"title\":\"title2\",\"seriesId\":1,\"noSeries\":1,\"formatId\":1,\"statusId\":1,\"noPages\":1,\"notes\":\"notes\"}]", baos.toString());
    }
    
    @Test
    public void getSeriesWithAqueryParameterWithAnIdForAnonExistantSeriesShouldReturnA404() throws ResourceException, IOException {
        testAuthorDao.createAuthor(new Author(1, "lastName", "firstName"));

        testSeriesDao.createSeries(new Series(1, 1, "seriesName1", "description"));
        testSeriesDao.createSeries(new Series(2, 1, "seriesName2", "description"));
        testSeriesDao.createSeries(new Series(3, 1, "seriesName3", "description"));
        
        ClientResource resource = new ClientResource("http://localhost:8182/rest/series/4");
        try {
            resource.get();
            fail("A 404 should have been thrown as the author doesnt exist.");
        } catch (ResourceException e) {
            assertEquals("Not Found (404) - The server has not found anything matching the request URI", e.getMessage());
        }
        resource.release();
    }
}
