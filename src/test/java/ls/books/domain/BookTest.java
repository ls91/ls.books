package ls.books.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class BookTest {

    Book testBook;

    @Before
    public void setup() {
        testBook = new Book(0, "", 0, 0, 0, 0, "");
    }

    @Test
    public void shouldBeAbleToSetAndGetBookId() {
        assertEquals(0, testBook.getBookId());
        testBook.setBookId(5);
        assertEquals(5, testBook.getBookId());
    }
    
    @Test
    public void shouldBeAbleToSetAndGetSeriesId() {
        assertEquals(0, testBook.getSeriesId());
        testBook.setSeriesId(5);
        assertEquals(5, testBook.getSeriesId());
    }
    
    @Test
    public void shouldBeAbleToSetAndGetNoSeries() {
        assertEquals(0, testBook.getNoSeries());
        testBook.setNoSeries(5);
        assertEquals(5, testBook.getNoSeries());
    }
    
    @Test
    public void shouldBeAbleToSetAndGetFormatId() {
        assertEquals(0, testBook.getFormatId());
        testBook.setFormatId(5);
        assertEquals(5, testBook.getFormatId());
    }
    
    @Test
    public void shouldBeAbleToSetAndGetNoPages() {
        assertEquals(0, testBook.getNoPages());
        testBook.setNoPages(5);
        assertEquals(5, testBook.getNoPages());
    }

    @Test
    public void shouldBeAbleToSetAndGetTitle() {
        assertEquals("", testBook.getTitle());
        testBook.setTitle("FOO");
        assertEquals("FOO", testBook.getTitle());
    }
    
    @Test
    public void shouldBeAbleToSetAndGetNotes() {
        assertEquals("", testBook.getNotes());
        testBook.setNotes("FOO");
        assertEquals("FOO", testBook.getNotes());
    }

    @Test
    public void toStringShouldReturnAformattedStringRepresentingTheAuthor() {
        testBook = new Book(5, "FOO", 6, 7, 8, 9, "BAR");
        assertEquals("Book ID: 5\nTitle: FOO\nSeries ID: 6\nNo. series: 7\nFormat ID: 8\nNo. pages: 9\nNotes: BAR", testBook.toString());
    }

    @Test
    public void equalsShouldReturnFalseIfArgumentIsNull() {
        assertFalse(testBook.equals(null));
    }

    @Test
    public void equalsShouldReturnFalseIfArgumentIsNotAnAuthor() {
        assertFalse(testBook.equals(""));
    }

    @Test
    public void equalsShouldReturnFalseIfTheBookIdsAreDifferent() {
        assertFalse(testBook.equals(new Book(5, null, 0, 0, 0, 0, null)));
    }
    
    @Test
    public void equalsShouldReturnFalseIfTheBookTitlesAreDifferent() {
        assertFalse(testBook.equals(new Book(0, "", 0, 0, 0, 0, null)));
    }
    
    @Test
    public void equalsShouldReturnFalseIfTheSeriesIdsAreDifferent() {
        assertFalse(testBook.equals(new Book(0, null, 1, 0, 0, 0, null)));
    }
    
    @Test
    public void equalsShouldReturnFalseIfTheNoSeriesAreDifferent() {
        assertFalse(testBook.equals(new Book(0, null, 0, 1, 0, 0, null)));
    }
    
    @Test
    public void equalsShouldReturnFalseIfTheFormatIdsAreDifferent() {
        assertFalse(testBook.equals(new Book(0, null, 0, 0, 1, 0, null)));
    }
    
    @Test
    public void equalsShouldReturnFalseIfTheNoPagesAreDifferent() {
        assertFalse(testBook.equals(new Book(0, null, 0, 0, 0, 1, null)));
    }
    
    @Test
    public void equalsShouldReturnFalseIfTheBookNotesAreDifferent() {
        assertFalse(testBook.equals(new Book(0, null, 0, 0, 0, 0, "")));
    }

    @Test
    public void equalsShouldReturnTrueIfAllAttributesAreTheSame() {
        assertTrue(testBook.equals(new Book(0, "", 0, 0, 0, 0, "")));
    }
}
