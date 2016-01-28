package ls.books.dao;

import java.util.List;

import ls.books.dao.mapper.BookMapper;
import ls.books.domain.Book;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

public interface BookDao {

    public enum ColumnName {
        BOOK_ID,
        TITLE,
        SERIES_ID,
        NO_SERIES,
        FORMAT_ID,
        NO_PAGES,
        NOTES
    }

    //Create
    @SqlUpdate("insert into book(title"
            + "                 ,series_id"
            + "                 ,no_series"
            + "                 ,format_id"
            + "                 ,no_pages"
            + "                 ,notes"
            + "                 )"
            + "           values(:book.title"
            + "                 ,:book.seriesId"
            + "                 ,:book.noSeries"
            + "                 ,:book.formatId"
            + "                 ,:book.noPages"
            + "                 ,:book.notes)")
    @GetGeneratedKeys
    int createBook(@BindBean("book") Book book);

    //Read
    @SqlQuery("select   b.book_id, "
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

    @SqlQuery("select   book_id, "
            + "         title, "
            + "         series_id, "
            + "         no_series, "
            + "         format_id, "
            + "         no_pages, "
            + "         notes "
            + "from     book "
            + "where    book_id = :bookId")
    @Mapper(BookMapper.class)
    Book findBookById(@Bind("bookId") int bookId);

    //Update
    @SqlUpdate("update  book"
            + " set     title       =   :book.title"
            + " ,       series_id   =   :book.seriesId"
            + " ,       no_series   =   :book.noSeries"
            + " ,       format_id   =   :book.formatId"
            + " ,       no_pages    =   :book.noPages"
            + " ,       notes       =   :book.notes"
            + " where   book_id     =   :book.bookId")
    void updateBook(@BindBean("book") Book book);

    //Delete
    @SqlUpdate("delete from book"
            + " where book_id = :bookId")
    void deleteBookById(@Bind("bookId") int bookId);

    void close();
}
