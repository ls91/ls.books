package ls.books.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import ls.books.dao.AuthorDao;
import ls.books.domain.Author;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class AuthorMapper implements ResultSetMapper<Author> {

    @Override
    public Author map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return new Author(r.getInt(AuthorDao.ColumnName.AUTHOR_ID.name()), r.getString(AuthorDao.ColumnName.LAST_NAME.name()), r.getString(AuthorDao.ColumnName.FIRST_NAME.name()));
    }

}
