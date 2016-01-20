package ls.books.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class FormatTest {

    Format testFormat;

    @Before
    public void setup() {
        testFormat = new Format(0, "");
    }

    @Test
    public void shouldBeAbleToSetAndGetFormatId() {
        assertEquals(0, testFormat.getFormatId());
        testFormat.setFormatId(5);
        assertEquals(5, testFormat.getFormatId());
    }

    @Test
    public void shouldBeAbleToSetAndGetName() {
        assertEquals("", testFormat.getName());
        testFormat.setName("FOO");
        assertEquals("FOO", testFormat.getName());
    }

    @Test
    public void constructorShouldSetTheIdAndName() {
        testFormat = new Format(5, "FOO");
        assertEquals(5, testFormat.getFormatId());
        assertEquals("FOO", testFormat.getName());
    }

    @Test
    public void toStringShouldReturnAformattedStringRepresentingTheAuthor() {
        testFormat = new Format(5, "FOO");
        assertEquals("Format ID: 5\nName: FOO", testFormat.toString());
    }

    @Test
    public void equalsShouldReturnFalseIfArgumentIsNull() {
        assertFalse(testFormat.equals(null));
    }

    @Test
    public void equalsShouldReturnFalseIfArgumentIsNotAnAuthor() {
        assertFalse(testFormat.equals(""));
    }

    @Test
    public void equalsShouldReturnFalseIfTheIdsAreDifferent() {
        assertFalse(testFormat.equals(new Format(5, null)));
    }

    @Test
    public void equalsShouldReturnFalseIfTheNamesAreDifferent() {
        assertFalse(testFormat.equals(new Format(0, "FOO")));
    }

    @Test
    public void equalsShouldReturnTrueIfAllAttributesAreTheSame() {
        assertTrue(testFormat.equals(new Format(0, "")));
    }
}
