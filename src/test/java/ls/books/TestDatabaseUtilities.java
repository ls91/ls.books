package ls.books;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import ls.books.domain.Author;

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

    public void addAuthor(Author author) throws SQLException {
        Connection connection = dataSource.getConnection();
        PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO AUTHORS (ID, LAST_NAME, FIRST_NAME) VALUES (?, ?, ?)");
        insertStatement.setInt(1, author.getId());
        insertStatement.setString(2, author.getLastName());
        insertStatement.setString(3, author.getFirstName());
        insertStatement.execute();
        insertStatement.close();
        connection.close();
    }

    public int countRecordsIn(final String tableName) throws SQLException {
        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("select count(*) from " + tableName);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        int rowCount = resultSet.getInt(1);
        resultSet.close();
        connection.close();

        return rowCount;
    }
}
