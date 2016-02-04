package ls.books.db;

import java.sql.SQLException;

import javax.sql.DataSource;

import ls.books.dao.AuthorDao;
import ls.books.dao.BookDao;
import ls.books.dao.FormatDao;
import ls.books.dao.SchemaBuilderDao;
import ls.books.dao.SeriesDao;
import ls.books.dao.StatusDao;
import ls.books.domain.Author;
import ls.books.domain.Book;
import ls.books.domain.Format;
import ls.books.domain.Series;
import ls.books.domain.Status;

import org.h2.jdbcx.JdbcDataSource;
import org.skife.jdbi.v2.DBI;

public class SchemaBuilder {

    protected static DataSource getDataSource(final String url, final String password) {
        DataSource dataSource = new JdbcDataSource();
        ((JdbcDataSource) dataSource).setURL(url);
        ((JdbcDataSource) dataSource).setUser("ls.books");
        ((JdbcDataSource) dataSource).setPassword(password);

        return dataSource;
    }

    public static DataSource buildSchema(final String url, final String password) throws SQLException, ClassNotFoundException {
        DataSource dataSource = getDataSource(url, password);

        SchemaBuilderDao dao = new DBI(dataSource).open(SchemaBuilderDao.class);

        //BUILD TABLES
        dao.createFormatTable();
        dao.createStatusTable();
        dao.createAuthorTable();
        dao.createSeriesTable();
        dao.createBookTable();
        
        //CLEANUP
        dao.close();

        return dataSource;
    }

    public static void populateWithSampleData(final DataSource dataSource) {
        FormatDao formatDao = new DBI(dataSource).open(FormatDao.class);
        formatDao.createFormat(new Format(1, "Paperback"));
        formatDao.createFormat(new Format(1, "Hardback"));
        formatDao.close();

        StatusDao statusDao = new DBI(dataSource).open(StatusDao.class);
        statusDao.createStatus(new Status(1, "Owned"));
        statusDao.createStatus(new Status(1, "To Order"));
        statusDao.close();

        AuthorDao authorDao = new DBI(dataSource).open(AuthorDao.class);
        authorDao.createAuthor(new Author(1, "Cussler", "Clive"));
        authorDao.createAuthor(new Author(1, "King", "Stephen"));
        authorDao.createAuthor(new Author(1, "Landy", "Derek"));
        authorDao.close();

        SeriesDao seriesDao = new DBI(dataSource).open(SeriesDao.class);
        seriesDao.createSeries(new Series(1, 1, "", ""));
        seriesDao.createSeries(new Series(1, 1, "NUMA Files", "Kurt Austin is Head of the US National Underwater and Marine Agency's (NUMA) Special Assignments Team - a job that ensures he is no stranger to danger above or below the waves.\n\nAustin's assignments take him across the globe and frequently bring him into conflict with those who seek to use any and all means to exploit the world's resources. But as a professional man of action, Austin is determined that it will never happen on his watch."));
        seriesDao.createSeries(new Series(1, 1, "Oregon Files", "Juan Cabrillo is Chairman of the Corporation, a special US Government-sponsored group that operates out of a ship called the Oregon; a marvel of scientific research equipment bristling with state-of-the-art weaponry - but disguised as a heap of junk.\n\nCabrillo and his crew of mercenaries with a conscience are able to cross the high seas in their rusting tub unmolested, seeking out those beyond the arms of the law and dealing out justice to any who would plot chaos on a global scale."));
        seriesDao.createSeries(new Series(1, 1, "Issac Bell", "Isaac Bell is an electrifying new hero, this tall, lean, no-nonsense detective working for the Van Dorn Detective Agency who is driven by his sense of justice, travels early-twentieth-century America pursuing thieves and killers and sometimes criminals much worse."));
        seriesDao.createSeries(new Series(1, 1, "Fargo Adventure", "Sam and Remi Fargo, a husband and wife treasure-hunting team who travel to exotic locales around the globe; unraveling ancient mysteries, unearthing long-lost treasures, finding themselves in sticky situations at every turn..."));
        seriesDao.createSeries(new Series(1, 1, "Dirk Pitt Adventures", "Ex-US Air force, he is cool, courageous and resourceful, which is why he works for the US National Underwater and Marine Agency (NUMA) - the undersea counterpart to NASA. In service of NUMA Pitt travels across the globe - and wherever he goes, action and adventure are sure to follow..."));
        seriesDao.createSeries(new Series(1, 2, "", ""));
        seriesDao.createSeries(new Series(1, 3, "", ""));
        seriesDao.createSeries(new Series(1, 3, "Skulduggery Pleasant", ""));
        seriesDao.close();

        BookDao bookDao = new DBI(dataSource).open(BookDao.class);
        bookDao.createBook(new Book("978-1-84983-109-3", "Serpent", 2, 1, 1, 1, 474, ""));
        bookDao.createBook(new Book("978-1-84739-971-7", "Blue Gold", 2, 2, 1, 2, 378, ""));
        bookDao.createBook(new Book("0-140-29736-7", "Fire Ice", 2, 3, 1, 1, 434, ""));
        bookDao.close();
    }
}
