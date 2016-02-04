package ls.books.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class StatusTest {

    Status testStatus;

    @Before
    public void setup() {
        testStatus = new Status(0, "");
    }

    @Test
    public void shouldBeAbleToSetAndGetStatusId() {
        assertEquals(0, testStatus.getStatusId());
        testStatus.setStatusId(5);
        assertEquals(5, testStatus.getStatusId());
    }

    @Test
    public void shouldBeAbleToSetAndGetName() {
        assertEquals("", testStatus.getName());
        testStatus.setName("FOO");
        assertEquals("FOO", testStatus.getName());
    }

    @Test
    public void constructorShouldSetTheIdAndName() {
        testStatus = new Status(5, "FOO");
        assertEquals(5, testStatus.getStatusId());
        assertEquals("FOO", testStatus.getName());
    }

    @Test
    public void toStringShouldReturnAformattedStringRepresentingTheAuthor() {
        testStatus = new Status(5, "FOO");
        assertEquals("Status ID: 5\nName: FOO", testStatus.toString());
    }

    @Test
    public void equalsShouldReturnFalseIfArgumentIsNull() {
        assertFalse(testStatus.equals(null));
    }

    @Test
    public void equalsShouldReturnFalseIfArgumentIsNotAnAuthor() {
        assertFalse(testStatus.equals(""));
    }

    @Test
    public void equalsShouldReturnFalseIfTheIdsAreDifferent() {
        assertFalse(testStatus.equals(new Status(5, null)));
    }

    @Test
    public void equalsShouldReturnFalseIfTheNamesAreDifferent() {
        assertFalse(testStatus.equals(new Status(0, "FOO")));
    }

    @Test
    public void equalsShouldReturnTrueIfAllAttributesAreTheSame() {
        assertTrue(testStatus.equals(new Status(0, "")));
    }
}
