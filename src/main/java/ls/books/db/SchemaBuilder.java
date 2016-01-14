package ls.books.db;

import java.sql.SQLException;

import javax.sql.DataSource;

import ls.books.dao.SchemaBuilderDao;

import org.h2.jdbcx.JdbcDataSource;
import org.skife.jdbi.v2.DBI;

public class SchemaBuilder {

    protected static DataSource getDataSource(final String url, final String password) {
        DataSource dataSource = new JdbcDataSource();
        ((JdbcDataSource) dataSource).setURL(url);
        ((JdbcDataSource) dataSource).setUser("ls.books");
        ((JdbcDataSource) dataSource).setPassword(password);

        return dataSource;
    }

    public static DataSource buildSchema(final String url, final String password) throws SQLException, ClassNotFoundException {
        DataSource dataSource = getDataSource(url, password);

        SchemaBuilderDao dao = new DBI(dataSource).open(SchemaBuilderDao.class);

        //BUILD TABLES
        dao.createAuthorsTable();

        //CLEANUP
        dao.close();

        return dataSource;
    }
}
