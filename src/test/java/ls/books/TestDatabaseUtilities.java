package ls.books;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;

public class TestDatabaseUtilities {
    
    DataSource dataSource;
    
    public TestDatabaseUtilities() {
        dataSource = new JdbcDataSource();
        ((JdbcDataSource) dataSource).setURL("jdbc:h2:mem:ls-books;DB_CLOSE_DELAY=-1");
        ((JdbcDataSource) dataSource).setUser("books");
        ((JdbcDataSource) dataSource).setPassword("books");
    }
    
    public DataSource getDataSource() {
        return dataSource;
    }
    
    public void createSchema() throws ClassNotFoundException, SQLException {
        createAuthorsTable();
    }
    
    public void teardownSchema() throws ClassNotFoundException, SQLException {
       dropTable("AUTHORS");
    }
    
    public void dropTable(final String tableName) throws ClassNotFoundException, SQLException {
        Connection connection = dataSource.getConnection();
        Statement dropTableStatement = connection.createStatement();
        dropTableStatement.execute("DROP TABLE " + tableName);
        dropTableStatement.close();
        connection.close();
    }
    
    public void createAuthorsTable() throws ClassNotFoundException, SQLException {
        Connection connection = dataSource.getConnection();
        Statement createTable = connection.createStatement();
        createTable.execute(
                "CREATE TABLE AUTHORS ("
                + "ID NUMBER,"
                + "LAST_NAME VARCHAR(500),"
                + "FIRST_NAME VARCHAR(500))");
        createTable.close();
        connection.close();
    }
}
