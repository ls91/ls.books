package ls.books.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class SeriesTest {

    Series testSeries;

    @Before
    public void setup() {
        testSeries = new Series(0, 0, "", "");
    }

    @Test
    public void shouldBeAbleToSetAndGetSeriesId() {
        assertEquals(0, testSeries.getSeriesId());
        testSeries.setSeriesId(5);
        assertEquals(5, testSeries.getSeriesId());
    }

    @Test
    public void shouldBeAbleToSetAndGetAuthorId() {
        assertEquals(0, testSeries.getAuthorId());
        testSeries.setAuthorId(5);
        assertEquals(5, testSeries.getAuthorId());
    }

    @Test
    public void shouldBeAbleToSetAndGetSeriesName() {
        assertEquals("", testSeries.getSeriesName());
        testSeries.setSeriesName("FOO");
        assertEquals("FOO", testSeries.getSeriesName());
    }

    @Test
    public void shouldBeAbleToSetAndGetDescription() {
        assertEquals("", testSeries.getDescription());
        testSeries.setDescription("FOO");
        assertEquals("FOO", testSeries.getDescription());
    }

    @Test
    public void constructorShouldSetAllAttributes() {
        testSeries = new Series(5, 6, "FOO", "BAR");
        assertEquals(5, testSeries.getSeriesId());
        assertEquals(6, testSeries.getAuthorId());
        assertEquals("FOO", testSeries.getSeriesName());
        assertEquals("BAR", testSeries.getDescription());
    }

    @Test
    public void toStringShouldReturnAformattedStringRepresentingTheAuthor() {
        testSeries = new Series(5, 6, "FOO", "BAR");
        assertEquals("Series ID: 5\nAuthor ID: 6\nSeries Name: FOO\nDescription: BAR", testSeries.toString());
    }

    @Test
    public void equalsShouldReturnFalseIfArgumentIsNull() {
        assertFalse(testSeries.equals(null));
    }

    @Test
    public void equalsShouldReturnFalseIfArgumentIsNotAnAuthor() {
        assertFalse(testSeries.equals(""));
    }

    @Test
    public void equalsShouldReturnFalseIfTheSeriesIdsAreDifferent() {
        assertFalse(testSeries.equals(new Series(5, 0, null, null)));
    }

    @Test
    public void equalsShouldReturnFalseIfTheAuthorIdsAreDifferent() {
        assertFalse(testSeries.equals(new Series(0, 5, null, null)));
    }

    @Test
    public void equalsShouldReturnFalseIfTheSeriesNamesAreDifferent() {
        assertFalse(testSeries.equals(new Series(0, 0, "FOO", null)));
    }

    @Test
    public void equalsShouldReturnFalseIfTheDescriptionsAreDifferent() {
        assertFalse(testSeries.equals(new Series(0, 0, null, "FOO")));
    }

    @Test
    public void equalsShouldReturnTrueIfAllAttributesAreTheSame() {
        assertTrue(testSeries.equals(new Series(0, 0, "", "")));
    }
}
