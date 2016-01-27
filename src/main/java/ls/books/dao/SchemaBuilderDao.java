package ls.books.dao;

import org.skife.jdbi.v2.sqlobject.SqlUpdate;

public interface SchemaBuilderDao {

    @SqlUpdate("CREATE TABLE AUTHOR ("
            + " AUTHOR_ID   NUMBER          PRIMARY KEY AUTO_INCREMENT"
            + ",LAST_NAME   VARCHAR2(500)   NOT NULL"
            + ",FIRST_NAME  VARCHAR2(500)   NOT NULL"
            + ")")
    void createAuthorTable();

    @SqlUpdate("CREATE TABLE SERIES ("
            + " SERIES_ID   NUMBER          PRIMARY KEY AUTO_INCREMENT"
            + ",AUTHOR_ID   NUMBER          NOT NULL"
            + ",SERIES_NAME VARCHAR2(500)   NOT NULL"
            + ",DESCRIPTION VARCHAR2(1000)"
            + ",FOREIGN KEY (AUTHOR_ID) REFERENCES AUTHOR(AUTHOR_ID)"
            + ")")
    void createSeriesTable();

    @SqlUpdate("CREATE TABLE FORMAT ("
            + " FORMAT_ID   NUMBER          PRIMARY KEY AUTO_INCREMENT"
            + ",NAME        VARCHAR2(100)   NOT NULL"
            + ")")
    void createFormatTable();

    @SqlUpdate("CREATE TABLE BOOK ("
            + " BOOK_ID     NUMBER          PRIMARY KEY AUTO_INCREMENT"
            + ",TITLE       VARCHAR2(100)   NOT NULL"
            + ",SERIES_ID   NUMBER          NOT NULL"
            + ",NO_SERIES   NUMBER          NOT NULL"
            + ",FORMAT_ID   NUMBER          NOT NULL"
            + ",NO_PAGES    NUMBER          NOT NULL"
            + ",NOTES       VARCHAR2(1500)"
            + ",FOREIGN KEY (SERIES_ID) REFERENCES SERIES(SERIES_ID)"
            + ",FOREIGN KEY (FORMAT_ID) REFERENCES AUTHOR(FORMAT_ID)"
            + ")")
    void createBookTable();

    void close();
}
