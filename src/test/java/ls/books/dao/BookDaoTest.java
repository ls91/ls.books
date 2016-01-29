package ls.books.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import ls.books.db.SchemaBuilder;
import ls.books.domain.Author;
import ls.books.domain.Book;
import ls.books.domain.Format;
import ls.books.domain.Series;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;

public class BookDaoTest {

    DataSource dataSource;
    AuthorDao testAuthorDao;
    SeriesDao testSeriesDao;
    FormatDao testFormatDao;
    BookDao testBookDao;

    @Before
    public void setup() throws ClassNotFoundException, SQLException {
        dataSource = SchemaBuilder.buildSchema("jdbc:h2:mem:ls-books;DB_CLOSE_DELAY=-1", "password");

        testAuthorDao = new DBI(dataSource).open(AuthorDao.class);
        testSeriesDao = new DBI(dataSource).open(SeriesDao.class);
        testFormatDao = new DBI(dataSource).open(FormatDao.class);
        testBookDao = new DBI(dataSource).open(BookDao.class);
        
        testAuthorDao.createAuthor(new Author(0, "FOO BAR", "FEE"));
        testAuthorDao.createAuthor(new Author(0, "BAR", "FEE"));
        testSeriesDao.createSeries(new Series(0, 1, "a", "description"));
        testSeriesDao.createSeries(new Series(0, 2, "b", "description"));
        testSeriesDao.createSeries(new Series(0, 2, "c", "description"));
        testFormatDao.createFormat(new Format(0, "name1"));
        testFormatDao.createFormat(new Format(0, "name2"));
    }

    @After
    public void teardown() throws ClassNotFoundException, SQLException {
        testAuthorDao.close();
        testBookDao.close();
        
        Connection connection = dataSource.getConnection();
        PreparedStatement wipeDatabase = connection.prepareStatement("DROP ALL OBJECTS");
        wipeDatabase.execute();
        wipeDatabase.close();
        connection.close();
    }

    //Create
    @Test
    public void createBookShouldAddAnewRecordToTheBookTable() {
        assertEquals(0, testBookDao.getBooks().size());

        testBookDao.createBook(new Book("0", "title", 1, 0, 1, 1, "notes"));
        
        assertEquals(1, testBookDao.getBooks().size());
        assertEquals(new Book("0", "title", 1, 0, 1, 1, "notes"), testBookDao.getBooks().get(0));
    }
    
    @Test
    public void createBookShouldFailIfTheIsbnIsNotUnique() {
        assertEquals(0, testBookDao.getBooks().size());

        try {
            testBookDao.createBook(new Book("0", "title", 1, 0, 1, 1, "notes"));
            testBookDao.createBook(new Book("0", "title", 1, 0, 1, 1, "notes"));
            fail("Second insert shouldn't succeed as the ISBN's are the same");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Unique index or primary key violation:"));
            assertTrue(e.getMessage().contains("ON PUBLIC.BOOK(ISBN)"));
        }
        
        assertEquals(1, testBookDao.getBooks().size());
        assertEquals(new Book("0", "title", 1, 0, 1, 1, "notes"), testBookDao.getBooks().get(0));
    }
    
    @Test
    public void createBookShouldFailIfSeriesDoesntExist() {
        assertEquals(0, testBookDao.getBooks().size());

        try {
            testBookDao.createBook(new Book("0", "title", 100, 0, 1, 1, "notes"));
            fail("Second insert shouldn't succeed as the series doesnt exist");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Referential integrity constraint violation:"));
            assertTrue(e.getMessage().contains("PUBLIC.BOOK FOREIGN KEY(SERIES_ID) REFERENCES PUBLIC.SERIES(SERIES_ID)"));
        }
        
        assertEquals(0, testBookDao.getBooks().size());
    }
    
    @Test
    public void createBookShouldFailIfFormatDoesntExist() {
        assertEquals(0, testBookDao.getBooks().size());

        try {
            testBookDao.createBook(new Book("0", "title", 1, 0, 100, 1, "notes"));
            fail("Second insert shouldn't succeed as the series doesnt exist");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Referential integrity constraint violation:"));
            assertTrue(e.getMessage().contains("PUBLIC.BOOK FOREIGN KEY(FORMAT_ID) REFERENCES PUBLIC.FORMAT(FORMAT_ID)"));
        }
        
        assertEquals(0, testBookDao.getBooks().size());
    }

    //Read
    @Test
    public void getBooksShouldReturnAlistOfAllBooksOrderedByAuthorSeriesSeriesNumberTitle() throws SQLException {
        assertEquals(0, testBookDao.getBooks().size());
        assertEquals(3, testSeriesDao.getSeries().size());
        
        Book book1 = new Book("324", "TITLE1", 1, 1, 1, 100, "NOTE");
        Book book2 = new Book("322", "TITLE2", 2, 1, 1, 100, "NOTE");
        Book book3 = new Book("45", "TITLE3", 3, 1, 1, 100, "NOTE");
        Book book4 = new Book("435", "TITLE4", 3, 2, 1, 100, "NOTE");
        Book book5 = new Book("757", "TITLE5", 2, 2, 1, 100, "NOTE");
        Book book6 = new Book("5456", "TITLE6", 2, 2, 1, 100, "NOTE");
        Book book7 = new Book("678", "TITLE7", 1, 1, 1, 100, "NOTE");
        Book book8 = new Book("23", "TITLE8", 2, 2, 1, 100, "NOTE");
        Book book9 = new Book("436", "TITLE9", 1, 1, 1, 100, "NOTE");
        
        testBookDao.createBook(book1);
        testBookDao.createBook(book2);
        testBookDao.createBook(book3);
        testBookDao.createBook(book4);
        testBookDao.createBook(book5);
        testBookDao.createBook(book6);
        testBookDao.createBook(book7);
        testBookDao.createBook(book8);
        testBookDao.createBook(book9);
        
        List<Book> results = testBookDao.getBooks();
        
        assertEquals(9, results.size());
        assertEquals(book2, results.get(0));
        assertEquals(book5, results.get(1));
        assertEquals(book6, results.get(2));
        assertEquals(book8, results.get(3));
        assertEquals(book3, results.get(4));
        assertEquals(book4, results.get(5));
        assertEquals(book1, results.get(6));
        assertEquals(book7, results.get(7));
        assertEquals(book9, results.get(8));
    }
    
    @Test
    public void findBookByIdShouldReturnTheBookAsSelectedById() throws SQLException {
        assertEquals(0, testBookDao.getBooks().size());
        
        Book book1 = new Book("55", "TITLE1", 1, 1, 1, 100, "NOTE");
        Book book2 = new Book("3434", "TITLE2", 2, 1, 1, 100, "NOTE");
        testBookDao.createBook(book1);
        testBookDao.createBook(book2);
        
        assertEquals(book1, testBookDao.findBookByIsbn("55"));
        assertEquals(book2, testBookDao.findBookByIsbn("3434"));
    }


    //Update
    @Test
    public void updateBookShouldModifyTheExistingRecordWithAnyAttributeThatChanges() {
        Book book = new Book("3434", "TITLE1", 1, 1, 1, 100, "NOTE");
        testBookDao.createBook(book);
        Book result = testBookDao.findBookByIsbn("3434");
        assertEquals(book, result);

        book.setFormatId(2);
        book.setNoPages(5);
        book.setNoSeries(8);
        book.setNotes("FOO BAR");
        book.setSeriesId(2);
        book.setTitle("NEW TITLE");
        testBookDao.updateBook(book);

        result = testBookDao.findBookByIsbn("3434");
        assertEquals(book, result);
    }

    //Delete
    @Test
    public void deleteAuthorByIdShouldRemoveTheAuthorFromTheTable() {
        Book book = new Book("2", "TITLE1", 1, 1, 1, 100, "NOTE");
        testBookDao.createBook(book);
        assertEquals(book, testBookDao.findBookByIsbn("2"));

        testBookDao.deleteBookById("2");

        assertNull(testBookDao.findBookByIsbn("2"));
    }
}
