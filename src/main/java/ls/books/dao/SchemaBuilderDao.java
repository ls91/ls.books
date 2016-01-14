package ls.books.dao;

import org.skife.jdbi.v2.sqlobject.SqlUpdate;

public interface SchemaBuilderDao {

    @SqlUpdate("CREATE TABLE AUTHORS ("
            + " ID NUMBER AUTO_INCREMENT,"
            + " LAST_NAME VARCHAR(500),"
            + " FIRST_NAME VARCHAR(500))")
    void createAuthorsTable();

    void close();
}
