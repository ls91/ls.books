package ls.books.dao;

import org.skife.jdbi.v2.sqlobject.SqlUpdate;

public interface SchemaBuilderDao {

    @SqlUpdate("CREATE TABLE AUTHOR ("
            + " AUTHOR_ID   NUMBER          PRIMARY KEY AUTO_INCREMENT,"
            + " LAST_NAME   VARCHAR2(500)   NOT NULL,"
            + " FIRST_NAME  VARCHAR2(500)   NOT NULL"
            + ")")
    void createAuthorTable();

    @SqlUpdate("CREATE TABLE SERIES ("
            + " SERIES_ID   NUMBER          PRIMARY KEY AUTO_INCREMENT,"
            + " AUTHOR_ID   NUMBER          NOT NULL,"
            + " SERIES_NAME VARCHAR2(500)   NOT NULL,"
            + " DESCRIPTION VARCHAR2(1000),"
            + " FOREIGN KEY (AUTHOR_ID) REFERENCES AUTHOR(AUTHOR_ID)"
            + ")")
    void createSeriesTable();

    @SqlUpdate("CREATE TABLE FORMAT ("
            + " FORMAT_ID   NUMBER          PRIMARY KEY AUTO_INCREMENT,"
            + " NAME        VARCHAR2(100)   NOT NULL"
            + ")")
    void createFormatTable();

    void close();
}
