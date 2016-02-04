package ls.books.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import ls.books.dao.BookDao;
import ls.books.domain.Book;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class BookMapper implements ResultSetMapper<Book> {

    @Override
    public Book map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return new Book(r.getString(BookDao.ColumnName.ISBN.name())
                , r.getString(BookDao.ColumnName.TITLE.name())
                , r.getInt(BookDao.ColumnName.SERIES_ID.name())
                , r.getInt(BookDao.ColumnName.NO_SERIES.name())
                , r.getInt(BookDao.ColumnName.FORMAT_ID.name())
                , r.getInt(BookDao.ColumnName.STATUS_ID.name())
                , r.getInt(BookDao.ColumnName.NO_PAGES.name())
                , r.getString(BookDao.ColumnName.NOTES.name())
                );
    }

}
