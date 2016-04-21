package ls.books.dao;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.sql.DataSource;

import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Component;
import org.restlet.resource.ResourceException;
import org.skife.jdbi.v2.DBI;

import ls.books.WebService;
import ls.books.db.SchemaBuilder;
import ls.books.domain.Author;
import ls.books.domain.Book;
import ls.books.domain.Format;
import ls.books.domain.Series;
import ls.books.domain.Status;

public class CsvReaderTest {

    FormatDao formatDao;
    StatusDao statusDao;
    SeriesDao seriesDao;
    AuthorDao authorDao;
    BookDao bookDao;
    DataSource dataSource;
    Component comp;
    
    @Before
    public void setup() throws Exception {
        dataSource = SchemaBuilder.buildSchema("jdbc:h2:mem:ls-books;DB_CLOSE_DELAY=-1", "password");
        comp = new WebService(dataSource, 8182);
        comp.start();
        
        formatDao = new DBI(dataSource).open(FormatDao.class);
        statusDao = new DBI(dataSource).open(StatusDao.class);
        seriesDao = new DBI(dataSource).open(SeriesDao.class);
        authorDao = new DBI(dataSource).open(AuthorDao.class);
        bookDao = new DBI(dataSource).open(BookDao.class);
    }
    
    @After
    public void teardown() throws Exception {
        formatDao.close();
        statusDao.close();
        seriesDao.close();
        authorDao.close();
        bookDao.close();

        Connection connection = dataSource.getConnection();
        PreparedStatement wipeDatabase = connection.prepareStatement("DROP ALL OBJECTS");
        wipeDatabase.execute();
        wipeDatabase.close();
        connection.close();
        
        comp.stop();
    }
    
    @Test
    public void constructorShouldSetThePort() {
        assertEquals(5, new CsvReader(5).getPort());
    }
    
    @Test
    public void shouldBeAbleToGetAndSetPort() {
        CsvReader csvReader = new CsvReader(5);
        assertEquals(5, csvReader.getPort());
        csvReader.setPort(6);
        assertEquals(6, csvReader.getPort());
    }
    
    @Test
    public void createFormatShouldCreateTheFormatIfItsNotInTheLocalCache() throws ResourceException, JSONException, IOException {
        CsvReader csvReader = new CsvReader(8182);
        assertEquals(0, formatDao.getFormats().size());
        assertEquals(0, csvReader.formats.size());
        
        assertEquals(1, csvReader.createFormat(new Format(0, "name")));
        
        assertEquals(1, formatDao.getFormats().size());
        assertEquals(1, csvReader.formats.size());
        
        assertEquals(new Format(1, "name"), formatDao.getFormats().get(0));
        assertEquals(new Integer(1), csvReader.formats.get(new Format(0, "name")));
    }
    
    @Test
    public void createFormatShouldNotCreateTheFormatIfItsInTheLocalCache() throws ResourceException, JSONException, IOException {
        CsvReader csvReader = new CsvReader(8182);
        assertEquals(1, csvReader.createFormat(new Format(0, "name")));
        assertEquals(1, formatDao.getFormats().size());
        assertEquals(1, csvReader.formats.size());
        
        assertEquals(1, csvReader.createFormat(new Format(0, "name")));
        
        assertEquals(1, formatDao.getFormats().size());
        assertEquals(1, csvReader.formats.size());
        
        assertEquals(new Format(1, "name"), formatDao.getFormats().get(0));
        assertEquals(new Integer(1), csvReader.formats.get(new Format(0, "name")));
    }
    
    @Test
    public void createStatusShouldCreateTheStatusIfItsNotInTheLocalCache() throws ResourceException, JSONException, IOException {
        CsvReader csvReader = new CsvReader(8182);
        assertEquals(0, statusDao.getStatuses().size());
        assertEquals(0, csvReader.statuses.size());
        
        assertEquals(1, csvReader.createStatus(new Status(0, "name")));
        
        assertEquals(1, statusDao.getStatuses().size());
        assertEquals(1, csvReader.statuses.size());
        
        assertEquals(new Status(1, "name"), statusDao.getStatuses().get(0));
        assertEquals(new Integer(1), csvReader.statuses.get(new Status(0, "name")));
    }
    
    @Test
    public void createStatusShouldNotCreateTheStatusIfItsInTheLocalCache() throws ResourceException, JSONException, IOException {
        CsvReader csvReader = new CsvReader(8182);
        assertEquals(1, csvReader.createStatus(new Status(0, "name")));
        assertEquals(1, statusDao.getStatuses().size());
        assertEquals(1, csvReader.statuses.size());
        
        assertEquals(1, csvReader.createStatus(new Status(0, "name")));
        
        assertEquals(1, statusDao.getStatuses().size());
        assertEquals(1, csvReader.statuses.size());
        
        assertEquals(new Status(1, "name"), statusDao.getStatuses().get(0));
        assertEquals(new Integer(1), csvReader.statuses.get(new Status(0, "name")));
    }
    
    @Test
    public void createAuthorShouldCreateTheAuthorIfItsNotInTheLocalCache() throws ResourceException, JSONException, IOException {
        CsvReader csvReader = new CsvReader(8182);
        assertEquals(0, authorDao.getAuthors().size());
        assertEquals(0, seriesDao.getSeries().size());
        assertEquals(0, csvReader.authors.size());
        assertEquals(0, csvReader.series.size());
        
        assertEquals(1, csvReader.createAuthor(new Author(0, "lastName", "firstName")));
        
        assertEquals(1, authorDao.getAuthors().size());
        assertEquals(1, seriesDao.getSeries().size());
        assertEquals(1, csvReader.authors.size());
        assertEquals(1, csvReader.series.size());
        
        assertEquals(new Author(1, "lastName", "firstName"), authorDao.getAuthors().get(0));
        assertEquals(new Integer(1), csvReader.authors.get(new Author(0, "lastName", "firstName")));
        assertEquals(new Series(1, 1, "", ""), seriesDao.getSeries().get(0));
        assertEquals(new Integer(1), csvReader.series.get(new Series(0, 1, "", "")));
    }
    
    @Test
    public void createAuthorShouldNotCreateTheAuthorIfItsInTheLocalCache() throws ResourceException, JSONException, IOException {
        CsvReader csvReader = new CsvReader(8182);
        assertEquals(1, csvReader.createAuthor(new Author(0, "lastName", "firstName")));
        assertEquals(1, authorDao.getAuthors().size());
        assertEquals(1, seriesDao.getSeries().size());
        assertEquals(1, csvReader.authors.size());
        assertEquals(1, csvReader.series.size());
        
        assertEquals(1, csvReader.createAuthor(new Author(0, "lastName", "firstName")));
        
        assertEquals(1, authorDao.getAuthors().size());
        assertEquals(1, seriesDao.getSeries().size());
        assertEquals(1, csvReader.authors.size());
        assertEquals(1, csvReader.series.size());
        
        assertEquals(new Author(1, "lastName", "firstName"), authorDao.getAuthors().get(0));
        assertEquals(new Integer(1), csvReader.authors.get(new Author(0, "lastName", "firstName")));
        assertEquals(new Series(1, 1, "", ""), seriesDao.getSeries().get(0));
        assertEquals(new Integer(1), csvReader.series.get(new Series(0, 1, "", "")));
    }
    
    @Test
    public void createSeriesShouldCreateTheSeriesIfItsNotInTheLocalCache() throws ResourceException, JSONException, IOException {
        CsvReader csvReader = new CsvReader(8182);
        assertEquals(0, authorDao.getAuthors().size());
        assertEquals(0, seriesDao.getSeries().size());
        assertEquals(0, csvReader.authors.size());
        assertEquals(0, csvReader.series.size());
        
        assertEquals(1, csvReader.createAuthor(new Author(0, "lastName", "firstName")));
        assertEquals(1, authorDao.getAuthors().size());
        assertEquals(1, seriesDao.getSeries().size());
        assertEquals(1, csvReader.authors.size());
        assertEquals(1, csvReader.series.size());
        
        assertEquals(2, csvReader.createSeries(new Series(0, 1, "seriesName", "description")));
        
        assertEquals(1, authorDao.getAuthors().size());
        assertEquals(2, seriesDao.getSeries().size());
        assertEquals(1, csvReader.authors.size());
        assertEquals(2, csvReader.series.size());
        
        assertEquals(new Author(1, "lastName", "firstName"), authorDao.getAuthors().get(0));
        assertEquals(new Integer(1), csvReader.authors.get(new Author(0, "lastName", "firstName")));
        assertEquals(new Series(1, 1, "", ""), seriesDao.getSeries().get(0));
        assertEquals(new Integer(1), csvReader.series.get(new Series(0, 1, "", "")));
        assertEquals(new Series(2, 1, "seriesName", "description"), seriesDao.getSeries().get(1));
        assertEquals(new Integer(2), csvReader.series.get(new Series(0, 1, "seriesName", "description")));
    }
    
    @Test
    public void createSeriesShouldNotCreateTheSeriesIfItsInTheLocalCache() throws ResourceException, JSONException, IOException {
        CsvReader csvReader = new CsvReader(8182);
        assertEquals(0, authorDao.getAuthors().size());
        assertEquals(0, seriesDao.getSeries().size());
        assertEquals(0, csvReader.authors.size());
        assertEquals(0, csvReader.series.size());
        
        assertEquals(1, csvReader.createAuthor(new Author(0, "lastName", "firstName")));
        assertEquals(1, authorDao.getAuthors().size());
        assertEquals(1, seriesDao.getSeries().size());
        assertEquals(1, csvReader.authors.size());
        assertEquals(1, csvReader.series.size());
        
        assertEquals(1, csvReader.createSeries(new Series(0, 1, "", "")));
        
        assertEquals(1, authorDao.getAuthors().size());
        assertEquals(1, seriesDao.getSeries().size());
        assertEquals(1, csvReader.authors.size());
        assertEquals(1, csvReader.series.size());
        
        assertEquals(new Author(1, "lastName", "firstName"), authorDao.getAuthors().get(0));
        assertEquals(new Integer(1), csvReader.authors.get(new Author(0, "lastName", "firstName")));
        assertEquals(new Series(1, 1, "", ""), seriesDao.getSeries().get(0));
        assertEquals(new Integer(1), csvReader.series.get(new Series(0, 1, "", "")));
    }
    
    @Test
    public void createBookShouldCreateTheBookIfItsNotInTheLocalCache() throws ResourceException, JSONException, IOException {
        CsvReader csvReader = new CsvReader(8182);
        assertEquals(0, bookDao.getBooks().size());
        assertEquals(0, csvReader.books.size());

        csvReader.createFormat(new Format(0, "name"));
        csvReader.createStatus(new Status(0, "name"));
        csvReader.createAuthor(new Author(0, "lastName", "firstName"));
        
        assertEquals(1, csvReader.createBook(new Book(0, "isbn", "title", 1, 1, 1, 1, 1, 1, "notes")));
        
        assertEquals(1, bookDao.getBooks().size());
        assertEquals(1, csvReader.books.size());
        assertEquals(new Book(1, "isbn", "title", 1, 1, 1, 1, 1, 1, "notes"), bookDao.getBooks().get(0));
        assertEquals(new Integer(1), csvReader.books.get(new Book(0, "isbn", "title", 1, 1, 1, 1, 1, 1, "notes")));
    }
    
    @Test
    public void createBookShouldNotCreateTheBookIfItsInTheLocalCache() throws ResourceException, JSONException, IOException {
        CsvReader csvReader = new CsvReader(8182);
        assertEquals(0, bookDao.getBooks().size());
        assertEquals(0, csvReader.books.size());

        csvReader.createFormat(new Format(0, "name"));
        csvReader.createStatus(new Status(0, "name"));
        csvReader.createAuthor(new Author(0, "lastName", "firstName"));
        
        assertEquals(1, csvReader.createBook(new Book(0, "isbn", "title", 1, 1, 1, 1, 1, 1, "notes")));
        
        assertEquals(1, bookDao.getBooks().size());
        assertEquals(1, csvReader.books.size());
        assertEquals(new Book(1, "isbn", "title", 1, 1, 1, 1, 1, 1, "notes"), bookDao.getBooks().get(0));
        assertEquals(new Integer(1), csvReader.books.get(new Book(0, "isbn", "title", 1, 1, 1, 1, 1, 1, "notes")));
    
        assertEquals(1, csvReader.createBook(new Book(0, "isbn", "title", 1, 1, 1, 1, 1, 1, "notes")));
        
        assertEquals(1, bookDao.getBooks().size());
        assertEquals(1, csvReader.books.size());
        assertEquals(new Book(1, "isbn", "title", 1, 1, 1, 1, 1, 1, "notes"), bookDao.getBooks().get(0));
        assertEquals(new Integer(1), csvReader.books.get(new Book(0, "isbn", "title", 1, 1, 1, 1, 1, 1, "notes")));
    }
}
