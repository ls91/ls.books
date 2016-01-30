package ls.books.dao;

import java.util.List;

import ls.books.dao.mapper.BookMapper;
import ls.books.domain.Book;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

public interface BookDao {

    public enum ColumnName {
        ISBN,
        TITLE,
        SERIES_ID,
        NO_SERIES,
        FORMAT_ID,
        NO_PAGES,
        NOTES
    }

    //Create
    @SqlUpdate("insert into book(isbn"
            + "                 ,title"
            + "                 ,series_id"
            + "                 ,no_series"
            + "                 ,format_id"
            + "                 ,no_pages"
            + "                 ,notes"
            + "                 )"
            + "           values(:book.isbn"
            + "                 ,:book.title"
            + "                 ,:book.seriesId"
            + "                 ,:book.noSeries"
            + "                 ,:book.formatId"
            + "                 ,:book.noPages"
            + "                 ,:book.notes)")
    void createBook(@BindBean("book") Book book);

    //Read
    @SqlQuery("select   b.isbn, "
            + "         b.title, "
            + "         b.series_id, "
            + "         b.no_series, "
            + "         b.format_id, "
            + "         b.no_pages, "
            + "         b.notes "
            + "from     book b "
            + "join     series s "
            + "     on  b.series_id = s.series_id "
            + "join     author a "
            + "     on  s.author_id = a.author_id "
            + "order by a.last_name, "
            + "         a.first_name, "
            + "         s.series_name, "
            + "         b.no_series, "
            + "         b.title")
    @Mapper(BookMapper.class)
    List<Book> getBooks();

    @SqlQuery("select   isbn, "
            + "         title, "
            + "         series_id, "
            + "         no_series, "
            + "         format_id, "
            + "         no_pages, "
            + "         notes "
            + "from     book "
            + "where    isbn        =   :isbn")
    @Mapper(BookMapper.class)
    Book findBookByIsbn(@Bind("isbn") String isbn);

    @SqlQuery("select   b.isbn, "
            + "         b.title, "
            + "         b.series_id, "
            + "         b.no_series, "
            + "         b.format_id, "
            + "         b.no_pages, "
            + "         b.notes "
            + "from     book b "
            + "join     series s "
            + "     on  b.series_id = s.series_id "
            + "join     author a "
            + "     on  s.author_id = a.author_id "
            + "where    a.author_id = :id "
            + "order by a.last_name, "
            + "         a.first_name, "
            + "         s.series_name, "
            + "         b.no_series, "
            + "         b.title")
    @Mapper(BookMapper.class)
    List<Book> findBooksByAuthorId(@Bind("id") int id);

    //Update
    @SqlUpdate("update  book"
            + " set     title       =   :book.title"
            + " ,       series_id   =   :book.seriesId"
            + " ,       no_series   =   :book.noSeries"
            + " ,       format_id   =   :book.formatId"
            + " ,       no_pages    =   :book.noPages"
            + " ,       notes       =   :book.notes"
            + " where   isbn        =   :book.isbn")
    void updateBook(@BindBean("book") Book book);

    //Delete
    @SqlUpdate("delete from book"
            + " where isbn = :isbn")
    void deleteBookById(@Bind("isbn") String isbn);

    void close();
}
