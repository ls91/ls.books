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
import ls.books.dao.SeriesDao;
import ls.books.db.SchemaBuilder;
import ls.books.domain.Author;
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

public class SeriesResourceTest {

    DataSource dataSource;
    AuthorDao testAuthorDao;
    SeriesDao testSeriesDao;
    ByteArrayOutputStream baos;
    Component comp;

    @Before
    public void setup() throws Exception {
        dataSource = SchemaBuilder.buildSchema("jdbc:h2:mem:ls-books;DB_CLOSE_DELAY=-1", "password");

        testAuthorDao = new DBI(dataSource).open(AuthorDao.class);
        testSeriesDao = new DBI(dataSource).open(SeriesDao.class);

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
        
        Connection connection = dataSource.getConnection();
        PreparedStatement wipeDatabase = connection.prepareStatement("DROP ALL OBJECTS");
        wipeDatabase.execute();
        wipeDatabase.close();
        connection.close();
        
        comp.stop();
    }

    @Test
    public void postSeriesShouldPersistAseriesAndReturnAlinkToWhereItCanBeAccessed() throws Exception {
        testAuthorDao.createAuthor(new Author(1, "FOO", "BAR"));
        assertNotNull(testAuthorDao.findAuthorByAuthorId(1));
        
        assertNull(testSeriesDao.findSeriesBySeriesId(1));
        
        JSONObject newSeries = new JSONObject();
        newSeries.put("seriesId", "1");
        newSeries.put("authorId", "1");
        newSeries.put("seriesName", "name");
        newSeries.put("description", "");
        
        StringRepresentation seriesJson = new StringRepresentation(newSeries.toString());
        seriesJson.setMediaType(MediaType.APPLICATION_JSON);
        
        ClientResource resource = new ClientResource("http://localhost:8182/rest/series");
        resource.post(seriesJson).write(baos);
        
        assertEquals("1", baos.toString());

        assertEquals(new Series(1, 1, "name", ""), testSeriesDao.findSeriesBySeriesId(1));
    }
    
    @Test
    public void putSeriesShouldUpdateAnExistingRecordWithTheNewValuesIgnoringTheId() throws ResourceException, IOException, JSONException {
        testAuthorDao.createAuthor(new Author(1, "lastName", "firstName"));
        testAuthorDao.createAuthor(new Author(2, "lastName", "firstName"));
        
        Series series1 = new Series(1, 1, "seriesName1", "description");
        testSeriesDao.createSeries(series1);
        
        assertEquals(series1, testSeriesDao.findSeriesBySeriesId(1));
        
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

        assertEquals("\"Series 1 updated\"", baos.toString());
        assertEquals(new Series(1, 2, "name", ""), testSeriesDao.findSeriesBySeriesId(1));
    }
    
    @Test
    public void deleteSeriesShouldRemoveTheSeriesFromTheDatabase() throws ResourceException, IOException {
        Author author1 = new Author(1, "lastName", "firstName");
        testAuthorDao.createAuthor(author1);
        
        Series series1 = new Series(1, 1, "seriesName1", "description");
        testSeriesDao.createSeries(series1);
        
        assertEquals(series1, testSeriesDao.findSeriesBySeriesId(1));
        
        ClientResource resource = new ClientResource("http://localhost:8182/rest/series/1");
        resource.delete().write(baos);
        
        assertEquals("\"Series 1 deleted\"", baos.toString());
        
        assertNull(testSeriesDao.findSeriesBySeriesId(1));
    }
    
    @Test
    public void getSeriesWithNoQueryParametersShouldReturnAllSeriesInTheDatabase() throws ResourceException, IOException {
        Author author1 = new Author(1, "lastName", "firstName");
        testAuthorDao.createAuthor(author1);
        
        Series series1 = new Series(1, 1, "seriesName1", "description");
        testSeriesDao.createSeries(series1);
        
        Series series2 = new Series(2, 1, "seriesName2", "description");
        testSeriesDao.createSeries(series2);
        
        Series series3 = new Series(3, 1, "seriesName3", "description");
        testSeriesDao.createSeries(series3);
        
        ClientResource resource = new ClientResource("http://localhost:8182/rest/series");
        resource.get().write(baos);
        
        assertEquals("[{\"seriesId\":1,\"authorId\":1,\"seriesName\":\"seriesName1\",\"description\":\"description\"},{\"seriesId\":2,\"authorId\":1,\"seriesName\":\"seriesName2\",\"description\":\"description\"},{\"seriesId\":3,\"authorId\":1,\"seriesName\":\"seriesName3\",\"description\":\"description\"}]", baos.toString());
    }
    
    @Test
    public void getSeriesWithNoQueryParameterAndNoEntriesInTheDbShouldReturnAnEmptyList() throws ResourceException, IOException {
        ClientResource resource = new ClientResource("http://localhost:8182/rest/series");
        resource.get().write(baos);
        
        assertEquals("[]", baos.toString());
    }
    
    @Test
    public void getSeriesWithAqueryParameterShouldReturnThatSeriesFromTheDatabase() throws ResourceException, IOException {
        Author author1 = new Author(1, "lastName", "firstName");
        testAuthorDao.createAuthor(author1);
        
        Series series1 = new Series(1, 1, "seriesName1", "description");
        testSeriesDao.createSeries(series1);
        
        Series series2 = new Series(2, 1, "seriesName2", "description");
        testSeriesDao.createSeries(series2);
        
        Series series3 = new Series(3, 1, "seriesName3", "description");
        testSeriesDao.createSeries(series3);
        
        ClientResource resource = new ClientResource("http://localhost:8182/rest/series/2");
        resource.get().write(baos);
        
        assertEquals("{\"seriesId\":2,\"authorId\":1,\"seriesName\":\"seriesName2\",\"description\":\"description\"}", baos.toString());
    }
    
    @Test
    public void getSeriesWithAqueryParameterWithAnIdForAnonExistantSeriesShouldReturnA404() throws ResourceException, IOException {
        Author author1 = new Author(1, "lastName", "firstName");
        testAuthorDao.createAuthor(author1);
        
        Series series1 = new Series(1, 1, "seriesName1", "description");
        testSeriesDao.createSeries(series1);
        
        Series series2 = new Series(2, 1, "seriesName2", "description");
        testSeriesDao.createSeries(series2);
        
        Series series3 = new Series(3, 1, "seriesName3", "description");
        testSeriesDao.createSeries(series3);
        
        ClientResource resource = new ClientResource("http://localhost:8182/rest/series/4");
        try {
            resource.get();
            fail("A 404 should have been thrown as the author doesnt exist.");
        } catch (ResourceException e) {
            assertEquals("Not Found (404) - The server has not found anything matching the request URI", e.getMessage());
        }
    }
}