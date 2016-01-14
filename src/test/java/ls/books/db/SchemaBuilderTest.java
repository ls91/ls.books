package ls.books.db;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SchemaBuilderTest {

    File databaseFile = new File(System.getProperty("user.home") + "/testDatabaseName.mv.db");
    File databaseTraceFile = new File(System.getProperty("user.home") + "/testDatabaseName.trace.db");
    DataSource dataSource;
    
    @Before
    public void setup() throws ClassNotFoundException, SQLException {
        SchemaBuilder.buildSchema("jdbc:h2:~/testDatabaseName", "password");
        
        
        dataSource = new JdbcDataSource();
        ((JdbcDataSource) dataSource).setURL("jdbc:h2:~/testDatabaseName");
        ((JdbcDataSource) dataSource).setUser("ls.books");
        ((JdbcDataSource) dataSource).setPassword("password");
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
}
