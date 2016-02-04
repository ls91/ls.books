package ls.books.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.sql.SQLException;

import javax.sql.DataSource;

import ls.books.dao.AuthorDao;
import ls.books.dao.BookDao;
import ls.books.dao.FormatDao;
import ls.books.dao.SeriesDao;
import ls.books.dao.StatusDao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;

public class SchemaBuilderTest {

    File databaseFile = new File(System.getProperty("user.home") + "/testDatabaseName.mv.db");
    File databaseTraceFile = new File(System.getProperty("user.home") + "/testDatabaseName.trace.db");
    DataSource dataSource;
    
    @Before
    public void setup() throws ClassNotFoundException, SQLException {
        dataSource = SchemaBuilder.buildSchema("jdbc:h2:~/testDatabaseName", "password");
    }
    
    @After
    public void teardown() {
        if (databaseFile.exists()) {
            databaseFile.delete();
        }
        
        if (databaseTraceFile.exists()) {
            databaseTraceFile.delete();
        }
    }
    
    @Test
    public void buildSchemaShouldProduceAh2DatabaseFileAtTheLocationSpecified() throws ClassNotFoundException, SQLException {
        File databaseFile = new File(System.getProperty("user.home") + "/testDbName.mv.db");
        File databaseTraceFile = new File(System.getProperty("user.home") + "/testDbName.trace.db");
        
        if (databaseFile.exists()) {
            databaseFile.delete();
        }
        
        if (databaseTraceFile.exists()) {
            databaseTraceFile.delete();
        }
        
        assertFalse(databaseFile.exists());
        
        SchemaBuilder.buildSchema("jdbc:h2:~/testDbName", "password");
        
        assertTrue(databaseFile.exists());
        
        databaseFile.delete();
        databaseTraceFile.delete();
    }
    
    @Test
    public void populateWithSampleDataShouldAddFormatsSeriesAuthorsAndBooks() throws ClassNotFoundException, SQLException {
        DataSource dataSource = SchemaBuilder.buildSchema("jdbc:h2:~/testDbName2", "password");
        
        File databaseFile = new File(System.getProperty("user.home") + "/testDbName2.mv.db");
        File databaseTraceFile = new File(System.getProperty("user.home") + "/testDbName2.trace.db");
        
        FormatDao formatDao = new DBI(dataSource).open(FormatDao.class);
        StatusDao statusDao = new DBI(dataSource).open(StatusDao.class);
        AuthorDao authorDao = new DBI(dataSource).open(AuthorDao.class);
        SeriesDao seriesDao = new DBI(dataSource).open(SeriesDao.class);
        BookDao bookDao = new DBI(dataSource).open(BookDao.class);

        assertEquals(0, formatDao.getFormats().size());
        assertEquals(0, statusDao.getStatuses().size());
        assertEquals(0, authorDao.getAuthors().size());
        assertEquals(0, seriesDao.getSeries().size());
        assertEquals(0, bookDao.getBooks().size());
        
        SchemaBuilder.populateWithSampleData(dataSource);
        
        assertEquals(2, formatDao.getFormats().size());
        assertEquals(2, statusDao.getStatuses().size());
        assertEquals(3, authorDao.getAuthors().size());
        assertEquals(9, seriesDao.getSeries().size());
        assertEquals(3, bookDao.getBooks().size());
        
        formatDao.close();
        statusDao.close();
        authorDao.close();
        seriesDao.close();
        bookDao.close();
        
        databaseFile.delete();
        databaseTraceFile.delete();
    }
}
