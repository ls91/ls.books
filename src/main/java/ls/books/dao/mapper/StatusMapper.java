package ls.books.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import ls.books.dao.StatusDao;
import ls.books.domain.Status;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class StatusMapper implements ResultSetMapper<Status> {

    @Override
    public Status map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return new Status(r.getInt(StatusDao.ColumnName.STATUS_ID.name()), r.getString(StatusDao.ColumnName.NAME.name()));
    }

}
