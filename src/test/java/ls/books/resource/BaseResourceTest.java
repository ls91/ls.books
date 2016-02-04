package ls.books.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.Response;

import ls.books.domain.Entity;
import ls.books.domain.Format;

import org.junit.Before;
import org.junit.Test;

public class BaseResourceTest {
    
    BaseResource baseResource;
    
    @Before
    public void setup() {
        baseResource = new BaseResource();
    }
    
    @Test
    public void cacheControllerShouldBePreConfigured() {
        assertTrue(BaseResource.cacheControl.isNoCache());
        assertEquals(-1, BaseResource.cacheControl.getMaxAge());
        assertTrue(BaseResource.cacheControl.isMustRevalidate());
    }
    
    @Test
    public void staticConstantsShouldEqual() {
        assertEquals("%s %s deleted", BaseResource.ENTITY_DELETED);
        assertEquals("%s %s successfully updated", BaseResource.ENTITY_UPDATED);
    }
    
    @Test
    public void buildCreatedOkResponseShouldReturnTheResponseContainingTheIdPassedIn() throws URISyntaxException {
        Response result = baseResource.buildEntityCreatedResponse(5, "rest/api/%d");
        
        assertEquals(new URI("rest/api/5"), result.getHeaders().get("Location").get(0));
        assertEquals("5", result.getEntity());
    }
    
    @Test
    public void buildEntityUpdatedResponseShouldReturnTheResponseContainingTheExpectedString() throws URISyntaxException {
        Response result = baseResource.buildEntityUpdatedResponse(Entity.Author, 5);
        
        assertEquals("\"Author 5 successfully updated\"", result.getEntity());
    }
    
    @Test
    public void buildEntityDeletedResponseShouldReturnTheResponseContainingTheExpectedString() throws URISyntaxException {
        Response result = baseResource.buildEntityDeletedResponse(Entity.Author, 5);
        
        assertEquals("\"Author 5 deleted\"", result.getEntity());
    }
    
    @Test
    public void buildOkResponseShouldReturnThePassedInObjectAsJsonInTheBody() {
        assertEquals("{\"formatId\":1,\"name\":\"FORMAT\"}", baseResource.buildOkResponse(new Format(1, "FORMAT")).getEntity());
    }
    
    @Test
    public void buildResponseShouldHaveAnEmptyBodyIfTheNoArgVersionIsCalled() {
        assertNull(baseResource.build404Response().getEntity());
    }
    
    @Test
    public void buildResponseShouldHaveTheBodySetToThatWhatWasPassedInIfTheArgVersionIsCalled() {
        assertEquals("\"FORMAT\"", baseResource.build404Response("FORMAT").getEntity());
    }
}
