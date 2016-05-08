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
import ls.books.domain.Status;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;

public class StatusDaoTest {

    DataSource dataSource;
    StatusDao testStatusDao;

    @Before
    public void setup() throws ClassNotFoundException, SQLException {
        dataSource = SchemaBuilder.getDataSource(null, "password");
        SchemaBuilder.buildSchema(dataSource);

        testStatusDao = new DBI(dataSource).open(StatusDao.class);
    }

    @After
    public void teardown() throws ClassNotFoundException, SQLException {
        testStatusDao.close();
        
        Connection connection = dataSource.getConnection();
        PreparedStatement wipeDatabase = connection.prepareStatement("DROP ALL OBJECTS");
        wipeDatabase.execute();
        wipeDatabase.close();
        connection.close();
    }

    //Create
    @Test
    public void createStatusShouldAddAnewRecordToTheStatusTable() {
        assertEquals(0, testStatusDao.getStatuses().size());

        Status status = new Status(1, "Paperback");
        testStatusDao.createStatus(status);
        
        List<Status> results = testStatusDao.getStatuses();
        
        assertEquals(1, results.size());
        assertEquals(status, results.get(0));
    }
    
    @Test
    public void createStatusShouldNotAllowTwoStatusesOfTheSameName() {
        assertEquals(0, testStatusDao.getStatuses().size());

        Status status = new Status(1, "Paperback");
        testStatusDao.createStatus(status);
        
        List<Status> results = testStatusDao.getStatuses();
        assertEquals(1, results.size());
        assertEquals(status, results.get(0));
        
        try {
            testStatusDao.createStatus(status);
            fail("An exception should have been thrown as the same name is being reused");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Unique index or primary key violation: \"STATUS"));
        }

        results = testStatusDao.getStatuses();
        assertEquals(1, results.size());
        assertEquals(status, results.get(0));
    }

    //Read
    @Test
    public void getStatusesShouldReturnAlistOfAllStatusOrderedByName() throws SQLException {
        assertEquals(0, testStatusDao.getStatuses().size());
        
        Status status1 = new Status(1, "Paperback");
        Status status2 = new Status(2, "Hardback");
        testStatusDao.createStatus(status1);
        testStatusDao.createStatus(status2);
        
        List<Status> results = testStatusDao.getStatuses();
        
        assertEquals(2, results.size());
        assertEquals(status2, results.get(0));
        assertEquals(status1, results.get(1));
    }
    
    @Test
    public void findStatusByIdShouldReturnTheStatusAsSelectedById() throws SQLException {
        assertNull(testStatusDao.findStatusById(1));
        
        Status status1 = new Status(1, "Paperback");
        Status status2 = new Status(2, "Hardback");
        testStatusDao.createStatus(status1);
        testStatusDao.createStatus(status2);
        
        assertEquals(status1, testStatusDao.findStatusById(1));
        assertEquals(status2, testStatusDao.findStatusById(2));
    }

    //Update
    @Test
    public void updateStatusShouldModifyTheExistingRecordWithAnyAttributeThatChanges() {
        Status status = new Status(1, "FOO");
        testStatusDao.createStatus(status);
        Status result = testStatusDao.findStatusById(1);
        assertEquals(status, result);

        status.setName("FOO2");
        testStatusDao.updateStatus(status);

        result = testStatusDao.findStatusById(1);
        assertEquals(status, result);
    }
    
    //Delete
    @Test
    public void deleteStatusByIdShouldRemoveTheStatusFromTheTable() {
        Status status = new Status(1, "FOO");
        testStatusDao.createStatus(status);
        assertEquals(status, testStatusDao.findStatusById(1));

        testStatusDao.deleteStatusById(1);

        assertNull(testStatusDao.findStatusById(1));
    }
}
