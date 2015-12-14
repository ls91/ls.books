package ls.books.dao;

import java.util.List;

import ls.books.dao.mapper.AuthorMapper;
import ls.books.domain.Author;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

public interface AuthorDao {

    //Create
    @SqlUpdate("insert into authors (id, last_name, first_name)"
            + " values (:author.id, :author.lastName, :author.firstName)")
    void createAuthor(@BindBean("author") Author author);

    //Read
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
    Author findAuthorById(@Bind("id") int id);

    @SqlQuery("select   id, "
            + "         first_name, "
            + "         last_name "
            + "from     authors "
            + "where    lower(first_name)   like    concat('%', lower(:query), '%') "
            + "or       lower(last_name)    like    concat('%', lower(:query), '%') ")
    @Mapper(AuthorMapper.class)
    List<Author> findAuthorsByNameLike(@Bind("query") String query);

    //Update
    @SqlUpdate("update  authors"
            + " set     last_name   =   :author.lastName"
            + " ,       first_name  =   :author.firstName"
            + " where   id          =   :author.id")
    void updateAuthor(@BindBean("author") Author author);

    //Delete
    @SqlUpdate("delete from authors"
            + " where id = :id")
    void deleteAuthorById(@Bind("id") int id);

    void close();
}
