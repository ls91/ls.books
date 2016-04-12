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
import ls.books.dao.StatusDao;
import ls.books.db.SchemaBuilder;
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

public class StatusResourceTest {

    DataSource dataSource;
    StatusDao testStatusDao;
    ByteArrayOutputStream baos;
    Component comp;

    @Before
    public void setup() throws Exception {
        dataSource = SchemaBuilder.buildSchema("jdbc:h2:mem:ls-books;DB_CLOSE_DELAY=-1", "password");

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
        assertEquals("/rest/status/%s", StatusResource.STATUS_URL);
    }

    @Test
    public void postStatusShouldPersistAstatusAndReturnALinkToWhereItCanBeAccessed() throws Exception {
        assertNull(testStatusDao.findStatusById(1));
        
        JSONObject newStatus = new JSONObject().put("statusId", "1234").put("name", "Foo");
        
        StringRepresentation statusJson = new StringRepresentation(newStatus.toString());
        statusJson.setMediaType(MediaType.APPLICATION_JSON);
        
        ClientResource resource = new ClientResource("http://localhost:8182/rest/status");
        resource.post(statusJson).write(baos);
        resource.release();
        
        assertEquals("\"1\"", baos.toString());

        assertEquals(new Status(1, "Foo"), testStatusDao.findStatusById(1));
    }
    
    @Test
    public void postStatusShouldReturnAnErrorIfTheStatusNameHasAlreadyBeenUsed() throws Exception {
        testStatusDao.createStatus(new Status(1, "NAME 1"));
        assertEquals(new Status(1, "NAME 1"), testStatusDao.findStatusById(1));
        assertEquals(1, testStatusDao.getStatuses().size());
        
        JSONObject newStatus = new JSONObject().put("statusId", "1").put("name", "NAME 1");
        
        StringRepresentation statusJson = new StringRepresentation(newStatus.toString());
        statusJson.setMediaType(MediaType.APPLICATION_JSON);
        
        ClientResource resource = new ClientResource("http://localhost:8182/rest/status");
        try {
            resource.post(statusJson).write(baos);
            fail("A 404 should have been thrown as the status already exists.");
        } catch (ResourceException e) {
            assertEquals("Not Found (404) - The server has not found anything matching the request URI", e.getMessage());
        }
        resource.release();
        
        assertEquals(1, testStatusDao.getStatuses().size());
    }
    
    @Test
    public void putStatusShouldUpdateAnExistingRecordWithTheNewValuesIgnoringTheId() throws ResourceException, IOException, JSONException {
        Status status = new Status(1, "Name");
        testStatusDao.createStatus(status);
        assertEquals(status, testStatusDao.findStatusById(1));
        
        JSONObject updatedStatus = new JSONObject().put("statusId", "1").put("name", "Foo Updated");
        
        StringRepresentation statusJson = new StringRepresentation(updatedStatus.toString());
        statusJson.setMediaType(MediaType.APPLICATION_JSON);

        ClientResource resource = new ClientResource("http://localhost:8182/rest/status");
        resource.setMethod(Method.PUT);
        resource.put(statusJson).write(baos);
        resource.release();

        assertEquals("\"Status 1 successfully updated\"", baos.toString());
        assertEquals(new Status(1, "Foo Updated"), testStatusDao.findStatusById(1));
    }
    
    @Test
    public void putStatusShouldNotUpdateAnExistingRecordIfUsingAnExistingName() throws ResourceException, IOException, JSONException {
        Status status = new Status(1, "Name");
        testStatusDao.createStatus(status);
        assertEquals(status, testStatusDao.findStatusById(1));
        
        Status status2 = new Status(2, "Name2");
        testStatusDao.createStatus(status2);
        assertEquals(status2, testStatusDao.findStatusById(2));
        
        assertEquals(2, testStatusDao.getStatuses().size());
        
        JSONObject updatedStatus = new JSONObject().put("statusId", "2").put("name", "Name");
        
        StringRepresentation statusJson = new StringRepresentation(updatedStatus.toString());
        statusJson.setMediaType(MediaType.APPLICATION_JSON);

        ClientResource resource = new ClientResource("http://localhost:8182/rest/status");
        resource.setMethod(Method.PUT);
        try {
            resource.put(statusJson).write(baos);
            fail("A 404 should have been thrown as the status already exists.");
        } catch (ResourceException e) {
            assertEquals("Not Found (404) - The server has not found anything matching the request URI", e.getMessage());
        }
        resource.release();
        
        assertEquals(2, testStatusDao.getStatuses().size());
        assertEquals(status, testStatusDao.findStatusById(1));
        assertEquals(status2, testStatusDao.findStatusById(2));
    }
    
    @Test
    public void deleteStatusShouldRemoveTheFormatFromTheDatabase() throws ResourceException, IOException {
        Status status = new Status(1, "Name");
        testStatusDao.createStatus(status);
        
        Status retrievedStatus= testStatusDao.findStatusById(1);
        assertEquals(status, retrievedStatus);
        
        ClientResource resource = new ClientResource("http://localhost:8182/rest/status/1");
        resource.delete().write(baos);
        resource.release();
        
        assertEquals("\"Status 1 deleted\"", baos.toString());
        
        assertNull(testStatusDao.findStatusById(1));
    }
    
    @Test
    public void getStatusWithNoQueryParametersShouldReturnAllStatusesInTheDatabase() throws ResourceException, IOException {
        testStatusDao.createStatus(new Status(1, "NAME 1"));
        testStatusDao.createStatus(new Status(1, "NAME 2"));
        testStatusDao.createStatus(new Status(1, "NAME 3"));
        
        ClientResource resource = new ClientResource("http://localhost:8182/rest/status");
        resource.get().write(baos);
        resource.release();
        
        assertEquals("[{\"statusId\":1,\"name\":\"NAME 1\"},{\"statusId\":2,\"name\":\"NAME 2\"},{\"statusId\":3,\"name\":\"NAME 3\"}]", baos.toString());
    }
    
    @Test
    public void getStatusWithNoQueryParameterAndNoEntriesInTheDbShouldReturnAnEmptyList() throws ResourceException, IOException {
        ClientResource resource = new ClientResource("http://localhost:8182/rest/status");
        resource.get().write(baos);
        resource.release();
        
        assertEquals("[]", baos.toString());
    }
    
    @Test
    public void getStatusWithAqueryParameterShouldReturnThatStatusFromTheDatabase() throws ResourceException, IOException {
        testStatusDao.createStatus(new Status(1, "NAME 1"));
        testStatusDao.createStatus(new Status(1, "NAME 2"));
        testStatusDao.createStatus(new Status(1, "NAME 3"));
        
        ClientResource resource = new ClientResource("http://localhost:8182/rest/status/2");
        resource.get().write(baos);
        resource.release();
        
        assertEquals("{\"statusId\":2,\"name\":\"NAME 2\"}", baos.toString());
    }
    
    @Test
    public void getStatusWithAqueryParameterWithAnIdForAnonExistantStatusShouldReturnA404() throws ResourceException, IOException {
        testStatusDao.createStatus(new Status(1, "NAME 1"));
        testStatusDao.createStatus(new Status(1, "NAME 2"));
        testStatusDao.createStatus(new Status(1, "NAME 3"));
        
        ClientResource resource = new ClientResource("http://localhost:8182/rest/status/4");
        try {
            resource.get();
            fail("A 404 should have been thrown as the format doesnt exist.");
        } catch (ResourceException e) {
            assertEquals("Not Found (404) - The server has not found anything matching the request URI", e.getMessage());
        }
        resource.release();
    }
}
