package ls.books.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import ls.books.db.SchemaBuilder;
import ls.books.domain.Author;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;

public class AuthorDaoTest {

    DataSource dataSource;
    AuthorDao testAuthorDao;

    @Before
    public void setup() throws ClassNotFoundException, SQLException {
        dataSource = SchemaBuilder.buildSchema("jdbc:h2:mem:ls-books;DB_CLOSE_DELAY=-1", "password");

        testAuthorDao = new DBI(dataSource).open(AuthorDao.class);
    }

    @After
    public void teardown() throws ClassNotFoundException, SQLException {
        testAuthorDao.close();
        
        Connection connection = dataSource.getConnection();
        PreparedStatement wipeDatabase = connection.prepareStatement("DROP ALL OBJECTS");
        wipeDatabase.execute();
        wipeDatabase.close();
        connection.close();
    }

    //Create
    @Test
    public void createAuthorShouldAddAnewRecordToTheAuthorsTable() {
        List<Author> results = testAuthorDao.getAuthors();

        assertEquals(0, results.size());

        Author author = new Author(1, "FOO", "BAR");
        testAuthorDao.createAuthor(author);
        
        results = testAuthorDao.getAuthors();
        
        assertEquals(1, results.size());
        assertEquals(author, results.get(0));
    }

    //Read
    @Test
    public void getAuthorsShouldReturnAlistOfAllAuthorsOrderedByLastThenFirstName() throws SQLException {
        List<Author> results = testAuthorDao.getAuthors();
        
        assertEquals(0, results.size());
        
        Author author1 = new Author(1, "South", "Luke");
        Author author2 = new Author(2, "South", "Jo");
        Author author3 = new Author(3, "Cussler", "Clive");
        testAuthorDao.createAuthor(author1);
        testAuthorDao.createAuthor(author2);
        testAuthorDao.createAuthor(author3);
        
        results = testAuthorDao.getAuthors();
        
        assertEquals(3, results.size());
        assertEquals(author3, results.get(0));
        assertEquals(author2, results.get(1));
        assertEquals(author1, results.get(2));
    }
    
    @Test
    public void findAuthorByIdShouldReturnTheAuthorAsSelectedById() throws SQLException {
        Author result = testAuthorDao.findAuthorByAuthorId(1);
        
        assertNull(result);
        
        Author author1 = new Author(1, "South", "Luke");
        Author author2 = new Author(2, "South", "Jo");
        Author author3 = new Author(3, "Cussler", "Clive");
        testAuthorDao.createAuthor(author1);
        testAuthorDao.createAuthor(author2);
        testAuthorDao.createAuthor(author3);
        
        result = testAuthorDao.findAuthorByAuthorId(1);
        assertEquals(author1, result);
        
        result = testAuthorDao.findAuthorByAuthorId(2);
        assertEquals(author2, result);
        
        result = testAuthorDao.findAuthorByAuthorId(3);
        assertEquals(author3, result);
    }

    @Test
    public void findAuthorsByNameLike() {
        Author author1 = new Author(1, "South", "Luke");
        Author author2 = new Author(2, "South", "Jo");
        Author author3 = new Author(3, "Jones", "Jo");
        testAuthorDao.createAuthor(author1);
        testAuthorDao.createAuthor(author2);
        testAuthorDao.createAuthor(author3);

        assertEquals(3, testAuthorDao.getAuthors().size());

        List<Author> results = testAuthorDao.findAuthorsByNameLike("so");
        assertEquals(2, results.size());
        assertEquals(author1, results.get(0));
        assertEquals(author2, results.get(1));

        results = testAuthorDao.findAuthorsByNameLike("jo");
        assertEquals(2, results.size());
        assertEquals(author2, results.get(0));
        assertEquals(author3, results.get(1));
    }

    //Update
    @Test
    public void updateAuthorShouldModifyTheExistingRecordWithAnyAttributeThatChanges() {
        Author author = new Author(1, "FOO", "BAR");
        testAuthorDao.createAuthor(author);
        Author result = testAuthorDao.findAuthorByAuthorId(1);
        assertEquals(author, result);

        author.setFirstName("FOO");
        author.setLastName("BAR");
        testAuthorDao.updateAuthor(author);

        result = testAuthorDao.findAuthorByAuthorId(1);
        assertEquals(author, result);
    }
    
    //Delete
    @Test
    public void deleteAuthorByIdShouldRemoveTheAuthorFromTheTable() {
        Author author = new Author(1, "FOO", "BAR");
        testAuthorDao.createAuthor(author);
        assertEquals(author, testAuthorDao.findAuthorByAuthorId(1));

        testAuthorDao.deleteAuthorById(1);

        assertNull(testAuthorDao.findAuthorByAuthorId(1));
    }
}
