package ls.books.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import ls.books.db.SchemaBuilder;
import ls.books.domain.Format;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;

public class FormatDaoTest {

    DataSource dataSource;
    FormatDao testFormatDao;

    @Before
    public void setup() throws ClassNotFoundException, SQLException {
        dataSource = SchemaBuilder.buildSchema("jdbc:h2:mem:ls-books;DB_CLOSE_DELAY=-1", "password");

        testFormatDao = new DBI(dataSource).open(FormatDao.class);
    }

    @After
    public void teardown() throws ClassNotFoundException, SQLException {
        testFormatDao.close();
        
        Connection connection = dataSource.getConnection();
        PreparedStatement wipeDatabase = connection.prepareStatement("DROP ALL OBJECTS");
        wipeDatabase.execute();
        wipeDatabase.close();
        connection.close();
    }

    //Create
    @Test
    public void createFormatShouldAddAnewRecordToTheFormatTable() {
        assertEquals(0, testFormatDao.getFormats().size());

        Format format = new Format(1, "FOO");
        testFormatDao.createFormat(format);
        
        List<Format> results = testFormatDao.getFormats();
        
        assertEquals(1, results.size());
        assertEquals(format, results.get(0));
    }

    //Read
    @Test
    public void getFormatsShouldReturnAlistOfAllFormatsOrderedByName() throws SQLException {
        assertEquals(0, testFormatDao.getFormats().size());
        
        Format format1 = new Format(1, "Luke");
        Format format2 = new Format(2, "Jo");
        Format format3 = new Format(3, "Clive");
        testFormatDao.createFormat(format1);
        testFormatDao.createFormat(format2);
        testFormatDao.createFormat(format3);
        
        List<Format> results = testFormatDao.getFormats();
        
        assertEquals(3, results.size());
        assertEquals(format3, results.get(0));
        assertEquals(format2, results.get(1));
        assertEquals(format1, results.get(2));
    }
    
    @Test
    public void findFormatByIdShouldReturnTheFormatAsSelectedById() throws SQLException {
        assertNull(testFormatDao.findFormatById(1));
        
        Format format1 = new Format(1, "Luke");
        Format format2 = new Format(2, "Jo");
        Format format3 = new Format(3, "Clive");
        testFormatDao.createFormat(format1);
        testFormatDao.createFormat(format2);
        testFormatDao.createFormat(format3);
        
        assertEquals(format1, testFormatDao.findFormatById(1));
        assertEquals(format2, testFormatDao.findFormatById(2));
        assertEquals(format3, testFormatDao.findFormatById(3));
    }

    //Update
    @Test
    public void updateFormatShouldModifyTheExistingRecordWithAnyAttributeThatChanges() {
        Format format = new Format(1, "FOO");
        testFormatDao.createFormat(format);
        Format result = testFormatDao.findFormatById(1);
        assertEquals(format, result);

        format.setName("FOO2");
        testFormatDao.updateFormat(format);

        result = testFormatDao.findFormatById(1);
        assertEquals(format, result);
    }
    
    //Delete
    @Test
    public void deleteFormatByIdShouldRemoveTheFormatFromTheTable() {
        Format format = new Format(1, "FOO");
        testFormatDao.createFormat(format);
        assertEquals(format, testFormatDao.findFormatById(1));

        testFormatDao.deleteFormatById(1);

        assertNull(testFormatDao.findFormatById(1));
    }
}
