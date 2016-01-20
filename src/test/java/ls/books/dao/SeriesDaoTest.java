package ls.books.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import ls.books.db.SchemaBuilder;
import ls.books.domain.Author;
import ls.books.domain.Series;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;

public class SeriesDaoTest {

    DataSource dataSource;
    AuthorDao testAuthorDao;
    SeriesDao testSeriesDao;

    @Before
    public void setup() throws ClassNotFoundException, SQLException {
        dataSource = SchemaBuilder.buildSchema("jdbc:h2:mem:ls-books;DB_CLOSE_DELAY=-1", "password");

        testAuthorDao = new DBI(dataSource).open(AuthorDao.class);
        testSeriesDao = new DBI(dataSource).open(SeriesDao.class);
        
        testAuthorDao.createAuthor(new Author(1, "South", "Luke"));
        testAuthorDao.createAuthor(new Author(2, "South", "Jo"));
    }

    @After
    public void teardown() throws ClassNotFoundException, SQLException {
        testSeriesDao.close();
        
        Connection connection = dataSource.getConnection();
        PreparedStatement wipeDatabase = connection.prepareStatement("DROP ALL OBJECTS");
        wipeDatabase.execute();
        wipeDatabase.close();
        connection.close();
    }

    //Create
    @Test
    public void createSeriesShouldAddAnewRecordToTheSeriesTableProvidingTheAuthorExists() {
        assertEquals(2, testAuthorDao.getAuthors().size());

        Series series = new Series(1, 1, "FOO", "BAR");
        testSeriesDao.createSeries(series);
        
        List<Series> results = testSeriesDao.getSeries();
        
        assertEquals(1, results.size());
        assertEquals(series, results.get(0));
    }

    //Read
    @Test
    public void getSeriesShouldReturnAlistOfAllAuthorsOrderedBySeriesName() throws SQLException {
        List<Series> results = testSeriesDao.getSeries();
        
        assertEquals(0, results.size());
        
        Series series1 = new Series(1, 1, "Foo", "");
        testSeriesDao.createSeries(series1);
        Series series2 = new Series(2, 1, "Bar", "");
        testSeriesDao.createSeries(series2);
        Series series3 = new Series(3, 1, "1 Shot", "");
        testSeriesDao.createSeries(series3);

        
        results = testSeriesDao.getSeries();
        
        assertEquals(3, results.size());
        assertEquals(series3, results.get(0));
        assertEquals(series2, results.get(1));
        assertEquals(series1, results.get(2));
    }
    
    @Test
    public void findSeriesBySeriesIdShouldReturnTheSeriesAsSelectedById() throws SQLException {
        assertNull(testSeriesDao.findSeriesById(1));
        
        Series series1 = new Series(1, 1, "seriesName1", "description");
        testSeriesDao.createSeries(series1);
        
        Series series2 = new Series(2, 1, "seriesName2", "description");
        testSeriesDao.createSeries(series2);
        
        Series series3 = new Series(3, 1, "seriesName3", "description");
        testSeriesDao.createSeries(series3);
        
        assertEquals(series1, testSeriesDao.findSeriesById(1));
        assertEquals(series2, testSeriesDao.findSeriesById(2));
        assertEquals(series3, testSeriesDao.findSeriesById(3));
    }
    
    @Test
    public void findSeriesByAuthorIdShouldReturnTheSeriesWithThatAuthorId() throws SQLException {
        assertNull(testSeriesDao.findSeriesById(1));
        
        Series series1 = new Series(1, 1, "Second", "description");
        testSeriesDao.createSeries(series1);
        
        Series series2 = new Series(2, 1, "First", "description");
        testSeriesDao.createSeries(series2);
        
        Series series3 = new Series(3, 2, "seriesName3", "description");
        testSeriesDao.createSeries(series3);
        
        List<Series> results = testSeriesDao.findSeriesByAuthorId(1);
        assertEquals(2, results.size());
        assertEquals(series2, results.get(0));
        assertEquals(series1, results.get(1));
        
        results = testSeriesDao.findSeriesByAuthorId(2);
        assertEquals(1, results.size());
        assertEquals(series3, results.get(0));
    }
    
    @Test
    public void findSeriesByNameLike() {
        Series series1 = new Series(1, 1, "Super Mario", "description");
        testSeriesDao.createSeries(series1);
        
        Series series2 = new Series(2, 1, "Super Mario 2", "description");
        testSeriesDao.createSeries(series2);
        
        Series series3 = new Series(3, 1, "Not as above", "but description contains super mario");
        testSeriesDao.createSeries(series3);
        
        Series series4 = new Series(4, 1, "Not as above", "");
        testSeriesDao.createSeries(series4);
        
        assertEquals(4, testSeriesDao.getSeries().size());

        List<Series> results = testSeriesDao.findSeriesByNameAndDescriptionLike("super");
        assertEquals(3, results.size());
        assertEquals(series1, results.get(0));
        assertEquals(series2, results.get(1));
        assertEquals(series3, results.get(2));
    }
    
    //Update
    @Test
    public void updateSeriesShouldModifyTheExistingRecordWithAnyAttributeThatChanges() {
        Series series = new Series(1, 1, "seriesName", "description");
        testSeriesDao.createSeries(series);
        
        assertEquals(series, testSeriesDao.findSeriesById(1));

        series.setAuthorId(2);
        series.setSeriesName("seriesName2");
        series.setDescription("description2");
        testSeriesDao.updateSeries(series);

        assertEquals(series, testSeriesDao.findSeriesById(1));
    }
    
    //Delete
    @Test
    public void deleteAuthorByIdShouldRemoveTheAuthorFromTheTable() {
        Series series = new Series(1, 1, "seriesName", "description");
        testSeriesDao.createSeries(series);
        
        assertEquals(series, testSeriesDao.findSeriesById(1));

        testSeriesDao.deleteSeriesById(1);

        assertNull(testSeriesDao.findSeriesById(1));
    }
}
