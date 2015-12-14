package ls.books.dao;

import java.util.List;

import ls.books.dao.mapper.AuthorMapper;
import ls.books.domain.Author;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

public interface AuthorDao {

    @SqlQuery("select  id, "
            + "        first_name, "
            + "        last_name "
            + "from    authors "
            + "order by last_name, first_name")
    @Mapper(AuthorMapper.class)
    List<Author> getAuthors();

    @SqlQuery("select  id, "
            + "        first_name, "
            + "        last_name "
            + "from    authors "
            + "where   id = :id")
    @Mapper(AuthorMapper.class)
    Author getAuthor(@Bind("id") int id);

    void close();
}
