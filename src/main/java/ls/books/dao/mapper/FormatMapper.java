package ls.books.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import ls.books.dao.FormatDao;
import ls.books.domain.Format;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class FormatMapper implements ResultSetMapper<Format> {

    @Override
    public Format map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return new Format(r.getInt(FormatDao.ColumnName.FORMAT_ID.name()), r.getString(FormatDao.ColumnName.NAME.name()));
    }

}
