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
import ls.books.dao.FormatDao;
import ls.books.db.SchemaBuilder;
import ls.books.domain.Format;

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

public class FormatResourceTest {

    DataSource dataSource;
    FormatDao testFormatDao;
    ByteArrayOutputStream baos;
    Component comp;

    @Before
    public void setup() throws Exception {
        dataSource = SchemaBuilder.getDataSource(null, "password");
        SchemaBuilder.buildSchema(dataSource);

        testFormatDao = new DBI(dataSource).open(FormatDao.class);

        baos = new ByteArrayOutputStream();
        
        comp = new Component();
        comp.getServers().add(Protocol.HTTP, 8182);

        WebServicesApplication application = new WebServicesApplication(comp.getContext(), dataSource);

        comp.getDefaultHost().attach(application);
        comp.start();
    }

    @After
    public void teardown() throws Exception {
        testFormatDao.close();
        
        Connection connection = dataSource.getConnection();
        PreparedStatement wipeDatabase = connection.prepareStatement("DROP ALL OBJECTS");
        wipeDatabase.execute();
        wipeDatabase.close();
        connection.close();
        
        comp.stop();
    }

    @Test
    public void staticConstantsShouldEqual() {
        assertEquals("/rest/format/%s", FormatResource.FORMAT_URL);
    }

    @Test
    public void postFormatShouldPersistAformatAndReturnALinkToWhereItCanBeAccessed() throws Exception {
        assertNull(testFormatDao.findFormatById(1));
        
        JSONObject newFormat = new JSONObject();
        newFormat.put("formatId", "1234");
        newFormat.put("name", "Foo");
        
        StringRepresentation formatJson = new StringRepresentation(newFormat.toString());
        formatJson.setMediaType(MediaType.APPLICATION_JSON);
        
        ClientResource resource = new ClientResource("http://localhost:8182/rest/format");
        resource.post(formatJson).write(baos);
        resource.release();
        
        assertEquals("\"1\"", baos.toString());

        assertEquals(new Format(1, "Foo"), testFormatDao.findFormatById(1));
    }
    
    @Test
    public void putFormatShouldUpdateAnExistingRecordWithTheNewValuesIgnoringTheId() throws ResourceException, IOException, JSONException {
        Format format = new Format(1, "Name");
        testFormatDao.createFormat(format);
        assertEquals(format, testFormatDao.findFormatById(1));
        
        JSONObject updatedFormat = new JSONObject();
        updatedFormat.put("formatId", "1");
        updatedFormat.put("name", "Foo Updated");
        
        StringRepresentation formatJson = new StringRepresentation(updatedFormat.toString());
        formatJson.setMediaType(MediaType.APPLICATION_JSON);

        ClientResource resource = new ClientResource("http://localhost:8182/rest/format");
        resource.setMethod(Method.PUT);
        resource.put(formatJson).write(baos);
        resource.release();

        assertEquals("\"Format 1 successfully updated\"", baos.toString());
        assertEquals(new Format(1, "Foo Updated"), testFormatDao.findFormatById(1));
    }
    
    @Test
    public void deleteFormatShouldRemoveTheFormatFromTheDatabase() throws ResourceException, IOException {
        Format format = new Format(1, "Name");
        testFormatDao.createFormat(format);
        
        Format retrievedFormat= testFormatDao.findFormatById(1);
        assertEquals(format, retrievedFormat);
        
        ClientResource resource = new ClientResource("http://localhost:8182/rest/format/1");
        resource.delete().write(baos);
        resource.release();
        
        assertEquals("\"Format 1 deleted\"", baos.toString());
        
        assertNull(testFormatDao.findFormatById(1));
    }
    
    @Test
    public void getFormatWithNoQueryParametersShouldReturnAllformatsInTheDatabase() throws ResourceException, IOException {
        testFormatDao.createFormat(new Format(1, "NAME 1"));
        testFormatDao.createFormat(new Format(1, "NAME 2"));
        testFormatDao.createFormat(new Format(1, "NAME 3"));
        
        ClientResource resource = new ClientResource("http://localhost:8182/rest/format");
        resource.get().write(baos);
        resource.release();
        
        assertEquals("[{\"formatId\":1,\"name\":\"NAME 1\"},{\"formatId\":2,\"name\":\"NAME 2\"},{\"formatId\":3,\"name\":\"NAME 3\"}]", baos.toString());
    }
    
    @Test
    public void getFormatWithNoQueryParameterAndNoEntriesInTheDbShouldReturnAnEmptyList() throws ResourceException, IOException {
        ClientResource resource = new ClientResource("http://localhost:8182/rest/format");
        resource.get().write(baos);
        resource.release();
        
        assertEquals("[]", baos.toString());
    }
    
    @Test
    public void getFormatWithAqueryParameterShouldReturnThatFormatFromTheDatabase() throws ResourceException, IOException {
        testFormatDao.createFormat(new Format(1, "NAME 1"));
        testFormatDao.createFormat(new Format(1, "NAME 2"));
        testFormatDao.createFormat(new Format(1, "NAME 3"));
        
        ClientResource resource = new ClientResource("http://localhost:8182/rest/format/2");
        resource.get().write(baos);
        resource.release();
        
        assertEquals("{\"formatId\":2,\"name\":\"NAME 2\"}", baos.toString());
    }
    
    @Test
    public void getAuthorWithAqueryParameterWithAnIdForAnonExistantAuthorShouldReturnA404() throws ResourceException, IOException {
        testFormatDao.createFormat(new Format(1, "NAME 1"));
        testFormatDao.createFormat(new Format(1, "NAME 2"));
        testFormatDao.createFormat(new Format(1, "NAME 3"));
        
        ClientResource resource = new ClientResource("http://localhost:8182/rest/format/4");
        try {
            resource.get();
            fail("A 404 should have been thrown as the format doesnt exist.");
        } catch (ResourceException e) {
            assertEquals("Not Found (404) - The server has not found anything matching the request URI", e.getMessage());
        }
        resource.release();
    }
}
