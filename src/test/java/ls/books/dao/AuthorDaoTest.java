package ls.books.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.sql.SQLException;
import java.util.List;

import ls.books.TestDatabaseUtilities;
import ls.books.domain.Author;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;

public class AuthorDaoTest {

    TestDatabaseUtilities testDatabaseUtilities;
    AuthorDao testAuthorDao;

    @Before
    public void setup() throws ClassNotFoundException, SQLException {
        testDatabaseUtilities = new TestDatabaseUtilities();
        testDatabaseUtilities.createSchema();

        testAuthorDao = new DBI(testDatabaseUtilities.getDataSource()).open(AuthorDao.class);
    }

    @After
    public void teardown() throws ClassNotFoundException, SQLException {
        testAuthorDao.close();
        testDatabaseUtilities.teardownSchema();
    }

    @Test
    public void getAuthorsShouldReturnAlistOfAllAuthorsOrderedByLastThenFirstName() throws SQLException {
        List<Author> results = testAuthorDao.getAuthors();

        assertEquals(0, results.size());

        Author author1 = new Author(1, "South", "Luke");
        Author author2 = new Author(1, "South", "Jo");
        Author author3 = new Author(1, "Cussler", "Clive");
        testDatabaseUtilities.addAuthor(author1);
        testDatabaseUtilities.addAuthor(author2);
        testDatabaseUtilities.addAuthor(author3);
        
        results = testAuthorDao.getAuthors();
        
        assertEquals(3, results.size());
        assertEquals(author3, results.get(0));
        assertEquals(author2, results.get(1));
        assertEquals(author1, results.get(2));
    }

    @Test
    public void getAuthorShouldReturnTheAuthorAsSelectedById() throws SQLException {
        Author result = testAuthorDao.getAuthor(1);

        assertNull(result);

        Author author1 = new Author(1, "South", "Luke");
        Author author2 = new Author(2, "South", "Jo");
        Author author3 = new Author(3, "Cussler", "Clive");
        testDatabaseUtilities.addAuthor(author1);
        testDatabaseUtilities.addAuthor(author2);
        testDatabaseUtilities.addAuthor(author3);

        result = testAuthorDao.getAuthor(1);
        assertEquals(author1, result);

        result = testAuthorDao.getAuthor(2);
        assertEquals(author2, result);

        result = testAuthorDao.getAuthor(3);
        assertEquals(author3, result);
    }
}
