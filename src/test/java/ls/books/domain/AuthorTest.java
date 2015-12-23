package ls.books.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class AuthorTest {

    Author testAuthor;

    @Before
    public void setup() {
        testAuthor = new Author(0, "", "");
    }

    @Test
    public void shuldBeAbleToSetAndGetId() {
        assertEquals(0, testAuthor.getId());
        testAuthor.setId(5);
        assertEquals(5, testAuthor.getId());
    }

    @Test
    public void shuldBeAbleToSetAndGetLastName() {
        assertEquals("", testAuthor.getLastName());
        testAuthor.setLastName("FOO");
        assertEquals("FOO", testAuthor.getLastName());
    }

    @Test
    public void shuldBeAbleToSetAndGetFirstName() {
        assertEquals("", testAuthor.getFirstName());
        testAuthor.setFirstName("FOO");
        assertEquals("FOO", testAuthor.getFirstName());
    }

    @Test
    public void constructorShouldSetTheIdFirstAndLastName() {
        testAuthor = new Author(5, "FOO", "BAR");
        assertEquals(5, testAuthor.getId());
        assertEquals("FOO", testAuthor.getLastName());
        assertEquals("BAR", testAuthor.getFirstName());
    }

    @Test
    public void toStringShouldReturnAformattedStringRepresentingTheAuthor() {
        testAuthor = new Author(5, "FOO", "BAR");
        assertEquals("ID: 5\nLast Name: FOO\nFirst Name: BAR", testAuthor.toString());
    }

    @Test
    public void equalsShouldReturnFalseIfArgumentIsNull() {
        assertFalse(testAuthor.equals(null));
    }

    @Test
    public void equalsShouldReturnFalseIfArgumentIsNotAnAuthor() {
        assertFalse(testAuthor.equals(""));
    }

    @Test
    public void equalsShouldReturnFalseIfTheIdsAreDifferent() {
        assertFalse(testAuthor.equals(new Author(5, null, null)));
    }

    @Test
    public void equalsShouldReturnFalseIfTheLastNamesAreDifferent() {
        assertFalse(testAuthor.equals(new Author(0, "FOO", null)));
    }

    @Test
    public void equalsShouldReturnFalseIfFirstNamesAreDifferent() {
        assertFalse(testAuthor.equals(new Author(0, null, "FOO")));
    }

    @Test
    public void equalsShouldReturnTrueIfAllAttributesAreTheSame() {
        assertTrue(testAuthor.equals(new Author(0, "", "")));
    }
}
