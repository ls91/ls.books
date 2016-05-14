package ls.books.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

public class BookWithAuthorTest {

    BookWithAuthor testBook;

    @Before
    public void setup() {
        testBook = new BookWithAuthor(0, "0",  "", 0, 0, 0, 0, 0, "", 0);
    }

    @Test
    public void shouldBeAbleToSetAndGetAuthorId() {
        assertEquals(0, testBook.getAuthorId());
        testBook.setAuthorId(10);
        assertEquals(10, testBook.getAuthorId());
    }

    @Test
    public void toStringShouldReturnAformattedStringRepresentingTheAuthor() {
        testBook = new BookWithAuthor(0, "567834", "FOO", 6, 7, 8, 5, 9, "BAR", 10);
        assertEquals("Book ID: 0\nISBN: 567834\nTitle: FOO\nSeries ID: 6\nNo. series: 7\nFormat ID: 8\nStatus ID: 5\nNo. pages: 9\nNotes: BAR\nAuthor ID: 10", testBook.toString());
    }

    @Test
    public void equalsShouldReturnFalseIfTheAuthorIdsAreDifferent() {
        assertFalse(testBook.equals(new BookWithAuthor(0, "0", "", 0, 0, 0, 0, 0, "", 10)));
    }
}
