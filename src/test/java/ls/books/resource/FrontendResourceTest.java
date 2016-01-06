package ls.books.resource;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import ls.books.TestDatabaseUtilities;
import ls.books.WebServicesApplication;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Component;
import org.restlet.data.Protocol;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

public class FrontendResourceTest {

    TestDatabaseUtilities testDatabaseUtilities;
    ByteArrayOutputStream baos;
    Component comp;

    @Before
    public void setup() throws Exception {
        testDatabaseUtilities = new TestDatabaseUtilities();

        baos = new ByteArrayOutputStream();

        comp = new Component();
        comp.getServers().add(Protocol.HTTP, 8182);

        WebServicesApplication application = new WebServicesApplication(comp.getContext(), testDatabaseUtilities.getDataSource());

        comp.getDefaultHost().attach(application);
        comp.start();
    }

    @After
    public void teardown() throws Exception {
        comp.stop();
    }

    @Test
    public void getingNormalResourcesShouldReturnThemAsAstring() throws ResourceException, IOException {
        ClientResource resource = new ClientResource("http://localhost:8182/testindex.html");
        resource.get().write(baos);
        assertEquals("foo-bar\r\n"
                + " \r\n"
                + "bar-foo", baos.toString());
    }
}
