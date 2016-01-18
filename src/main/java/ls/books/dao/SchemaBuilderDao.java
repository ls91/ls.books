package ls.books.dao;

import org.skife.jdbi.v2.sqlobject.SqlUpdate;

public interface SchemaBuilderDao {

    @SqlUpdate("CREATE TABLE AUTHORS ("
            + " AUTHOR_ID   NUMBER          PRIMARY KEY AUTO_INCREMENT,"
            + " LAST_NAME   VARCHAR(500)    NOT NULL,"
            + " FIRST_NAME  VARCHAR(500)    NOT NULL"
            + ")")
    void createAuthorsTable();

    @SqlUpdate("CREATE TABLE SERIES ("
            + " SERIES_ID   NUMBER          PRIMARY KEY AUTO_INCREMENT,"
            + " AUTHOR_ID   NUMBER          NOT NULL,"
            + " SERIES_NAME VARCHAR(500)    NOT NULL,"
            + " DESCRIPTION VARCHAR2(1000),"
            + " FOREIGN KEY (AUTHOR_ID) REFERENCES AUTHORS(AUTHOR_ID)"
            + ")")
    void createSeriesTable();

    void close();
}
