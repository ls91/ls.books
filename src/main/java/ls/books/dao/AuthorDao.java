package ls.books.dao;

import java.util.List;

import ls.books.dao.mapper.AuthorMapper;
import ls.books.domain.Author;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

public interface AuthorDao {

    public enum ColumnName {
        AUTHOR_ID,
        LAST_NAME,
        FIRST_NAME
    }

    //Create
    @SqlUpdate("insert into author (last_name, first_name)"
            + " values (:author.lastName, :author.firstName)")
    @GetGeneratedKeys
    int createAuthor(@BindBean("author") Author author);

    //Read
    @SqlQuery("select   author_id, "
            + "         first_name, "
            + "         last_name "
            + "from     author "
            + "order by last_name, first_name")
    @Mapper(AuthorMapper.class)
    List<Author> getAuthors();

    @SqlQuery("select   author_id, "
            + "         first_name, "
            + "         last_name "
            + "from     author "
            + "where    author_id = :authorId")
    @Mapper(AuthorMapper.class)
    Author findAuthorById(@Bind("authorId") int authorId);

    @SqlQuery("select   author_id, "
            + "         first_name, "
            + "         last_name "
            + "from     author "
            + "where    lower(first_name)   like    concat('%', lower(:query), '%') "
            + "or       lower(last_name)    like    concat('%', lower(:query), '%') ")
    @Mapper(AuthorMapper.class)
    List<Author> findAuthorsByNameLike(@Bind("query") String query);

    //Update
    @SqlUpdate("update  author"
            + " set     last_name   =   :author.lastName"
            + " ,       first_name  =   :author.firstName"
            + " where   author_id   =   :author.authorId")
    void updateAuthor(@BindBean("author") Author author);

    //Delete
    @SqlUpdate("delete from author"
            + " where author_id = :authorId")
    void deleteAuthorById(@Bind("authorId") int authorId);

    void close();
}
