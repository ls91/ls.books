package ls.books.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import ls.books.dao.SeriesDao;
import ls.books.domain.Series;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class SeriesMapper implements ResultSetMapper<Series> {

    @Override
    public Series map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return new Series(r.getInt(SeriesDao.ColumnName.SERIES_ID.name()), r.getInt(SeriesDao.ColumnName.AUTHOR_ID.name()), r.getString(SeriesDao.ColumnName.SERIES_NAME.name()), r.getString(SeriesDao.ColumnName.DESCRIPTION.name()));
    }

}
