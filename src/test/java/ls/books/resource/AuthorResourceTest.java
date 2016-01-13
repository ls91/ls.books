package ls.books.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import ls.books.TestDatabaseUtilities;
import ls.books.WebServicesApplication;
import ls.books.dao.AuthorDao;
import ls.books.domain.Author;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Component;
import org.restlet.data.Protocol;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.skife.jdbi.v2.DBI;

public class AuthorResourceTest {

    TestDatabaseUtilities testDatabaseUtilities;
    AuthorDao testAuthorDao;
    ByteArrayOutputStream baos;
    Component comp;

    @Before
    public void setup() throws Exception {
        testDatabaseUtilities = new TestDatabaseUtilities();
        testDatabaseUtilities.createSchema();

        testAuthorDao = new DBI(testDatabaseUtilities.getDataSource()).open(AuthorDao.class);

        baos = new ByteArrayOutputStream();
        
        comp = new Component();
        comp.getServers().add(Protocol.HTTP, 8182);

        WebServicesApplication application = new WebServicesApplication(comp.getContext(), testDatabaseUtilities.getDataSource());

        comp.getDefaultHost().attach(application);
        comp.start();
    }

    @After
    public void teardown() throws Exception {
        testAuthorDao.close();
        testDatabaseUtilities.teardownSchema();
        comp.stop();
    }

    @Test
    public void postAuthorShouldPersistAnAuthorAndReturnALinkToWhereItCanBeAccessed() throws Exception {
        assertNull(testAuthorDao.findAuthorById(1234));
        
        ClientResource resource = new ClientResource("http://localhost:8182/rest/author");
        JSONObject newAuthor = new JSONObject();
        newAuthor.append("authorId", "1234");
        newAuthor.append("lastName", "Foo");
        newAuthor.append("firstName", "Bar");
        
        Representation result = resource.post(newAuthor.toString());
        
        result.write(baos);
        assertEquals("", baos.toString());

        assertEquals("/rest/author/1234", result.getLocationRef());

        Author author = testAuthorDao.findAuthorById(1234);
        assertEquals(new Author(1234, "Foo", "Bar"), author);
    }
    
    /*@Test
    public void putAuthorShouldUpdateAnExistingRecordWithTheNewValuesIgnoringTheId() throws ResourceException, IOException {
        Author author = new Author(1, "lastName", "firstName");
        testAuthorDao.createAuthor(author);
        Author retrievedAuthor = testAuthorDao.findAuthorById(1);
        assertEquals(author, retrievedAuthor);
        
        ClientResource resource = new ClientResource("http://localhost:8182/rest/author");
        resource.put(author).write(baos);
        
        assertEquals("Author 1 updated", baos.toString());
        
        retrievedAuthor = testAuthorDao.findAuthorById(1);
        assertEquals(new Author(1, "updatedLastName", "updatedFirstName"), retrievedAuthor);
    }*/
    
    @Test
    public void deleteAuthorShouldRemoveTheAuthorFromTheDatabase() throws ResourceException, IOException {
        Author author = new Author(1, "lastName", "firstName");
        
        testAuthorDao.createAuthor(author);
        
        Author retrievedAuthor = testAuthorDao.findAuthorById(1);
        assertEquals(author, retrievedAuthor);
        
        ClientResource resource = new ClientResource("http://localhost:8182/rest/author/1");
        resource.delete().write(baos);
        
        assertNull(testAuthorDao.findAuthorById(1));
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
