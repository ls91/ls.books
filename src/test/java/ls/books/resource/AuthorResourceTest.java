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
import ls.books.db.SchemaBuilder;
import ls.books.domain.Author;

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
    ByteArrayOutputStream baos;
    Component comp;

    @Before
    public void setup() throws Exception {
        dataSource = SchemaBuilder.buildSchema("jdbc:h2:mem:ls-books;DB_CLOSE_DELAY=-1", "password");

        testAuthorDao = new DBI(dataSource).open(AuthorDao.class);

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
        
        Connection connection = dataSource.getConnection();
        PreparedStatement wipeDatabase = connection.prepareStatement("DROP ALL OBJECTS");
        wipeDatabase.execute();
        wipeDatabase.close();
        connection.close();
        
        comp.stop();
    }

    @Test
    public void postAuthorShouldPersistAnAuthorAndReturnALinkToWhereItCanBeAccessed() throws Exception {
        assertNull(testAuthorDao.findAuthorByAuthorId(1));
        
        ClientResource resource = new ClientResource("http://localhost:8182/rest/author");
        
        JSONObject newAuthor = new JSONObject();
        newAuthor.put("authorId", "1234");
        newAuthor.put("lastName", "Foo");
        newAuthor.put("firstName", "Bar");
        
        StringRepresentation authorJson = new StringRepresentation(newAuthor.toString());
        authorJson.setMediaType(MediaType.APPLICATION_JSON);
        
        resource.post(authorJson).write(baos);
        
        assertEquals("1", baos.toString());

        assertEquals(new Author(1, "Foo", "Bar"), testAuthorDao.findAuthorByAuthorId(1));
    }
    
    @Test
    public void putAuthorShouldUpdateAnExistingRecordWithTheNewValuesIgnoringTheId() throws ResourceException, IOException, JSONException {
        Author author = new Author(1, "lastName", "firstName");
        testAuthorDao.createAuthor(author);
        assertEquals(author, testAuthorDao.findAuthorByAuthorId(1));
        
        ClientResource resource = new ClientResource("http://localhost:8182/rest/author");
        resource.setMethod(Method.PUT);
        
        JSONObject updatedAuthor = new JSONObject();
        updatedAuthor.put("authorId", "1");
        updatedAuthor.put("lastName", "Foo Updated");
        updatedAuthor.put("firstName", "Bar Updated");
        
        StringRepresentation authorJson = new StringRepresentation(updatedAuthor.toString());
        authorJson.setMediaType(MediaType.APPLICATION_JSON);

        resource.put(authorJson).write(baos);

        assertEquals("\"Author 1 updated\"", baos.toString());
        assertEquals(new Author(1, "Foo Updated", "Bar Updated"), testAuthorDao.findAuthorByAuthorId(1));
    }
    
    @Test
    public void deleteAuthorShouldRemoveTheAuthorFromTheDatabase() throws ResourceException, IOException {
        Author author = new Author(1, "lastName", "firstName");
        
        testAuthorDao.createAuthor(author);
        
        Author retrievedAuthor = testAuthorDao.findAuthorByAuthorId(1);
        assertEquals(author, retrievedAuthor);
        
        ClientResource resource = new ClientResource("http://localhost:8182/rest/author/1");
        resource.delete().write(baos);
        
        assertEquals("\"Author 1 deleted\"", baos.toString());
        
        assertNull(testAuthorDao.findAuthorByAuthorId(1));
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
