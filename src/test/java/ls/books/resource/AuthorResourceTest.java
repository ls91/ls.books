package ls.books.resource;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.sql.SQLException;

import ls.books.TestDatabaseUtilities;
import ls.books.dao.AuthorDao;
import ls.books.domain.Author;
import ls.books.router.AuthorRouter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Component;
import org.restlet.data.Form;
import org.restlet.data.Protocol;
import org.restlet.resource.ClientResource;
import org.skife.jdbi.v2.DBI;

public class AuthorResourceTest {

    TestDatabaseUtilities testDatabaseUtilities;
    AuthorDao testAuthorDao;
    ByteArrayOutputStream baos;

    @Before
    public void setup() throws ClassNotFoundException, SQLException {
        testDatabaseUtilities = new TestDatabaseUtilities();
        testDatabaseUtilities.createSchema();

        testAuthorDao = new DBI(testDatabaseUtilities.getDataSource()).open(AuthorDao.class);

        baos = new ByteArrayOutputStream();
    }

    @After
    public void teardown() throws ClassNotFoundException, SQLException {
        testAuthorDao.close();
        testDatabaseUtilities.teardownSchema();
    }

    @Test
    public void createAuthorShouldPersistAnAuthorAndReturnALinkToWhereItCanBeAccessed() throws Exception {
        Component component = new Component();  
        component.getServers().add(Protocol.HTTP, 8182); 
 
        component.getDefaultHost().attach(new AuthorRouter(testDatabaseUtilities.getDataSource()));
        component.start();
        
        ClientResource resource = new ClientResource("http://localhost:8182/author");
        
        
        Form form = new Form();
        form.add("id", "1234");
        form.add("lastName", "Foo");
        form.add("firstName", "Bar");
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        resource.post(form).write(baos);
        assertEquals("/author/1234", baos.toString());

        Author author = testAuthorDao.findAuthorById(1234);
        assertEquals(new Author(1234, "Foo", "Bar"), author);
        
        component.stop();
    }
}
