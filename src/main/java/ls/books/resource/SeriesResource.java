package ls.books.resource;

import java.net.URISyntaxException;

import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ls.books.dao.BookDao;
import ls.books.dao.SeriesDao;
import ls.books.domain.Entity;
import ls.books.domain.Series;

import org.restlet.Context;
import org.skife.jdbi.v2.DBI;

@Path("/rest/series")
public class SeriesResource extends BaseResource {

    protected static final String SERIES_URL = "/rest/series/%d";

    private SeriesDao seriesDao = null;
    private BookDao bookDao = null;

    protected void init() {
        if (seriesDao == null || bookDao == null) {
            DataSource dataSource = (DataSource) Context.getCurrent().getAttributes().get("DATA_SOURCE");
            seriesDao = new DBI(dataSource).open(SeriesDao.class);
            bookDao = new DBI(dataSource).open(BookDao.class);
        }
    }

    //Create
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSeries(Series series) throws URISyntaxException {
        init();

        try {
            series.setSeriesId(seriesDao.createSeries(series));
            return buildEntityCreatedResponse(series.getSeriesId(), SERIES_URL);
        } catch (Exception e) {
            return build404Response();
        }
    }

    //Read
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSeries() {
        init();
        return buildOkResponse(seriesDao.getSeries());
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSeriesById(@PathParam("id") int id) {
        init();
        Series result = seriesDao.findSeriesById(id);
        
        if (result != null) {
            return buildOkResponse(result);
        } else {
            return build404Response();
        }
    }

    @GET
    @Path("/{id}/books")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBooksInSeries(@PathParam("id") int id) {
        init();
        
        return buildOkResponse(bookDao.findBooksBySeriesId(id));
    }

    //Update
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateSeries(Series series) {
        init();

        try {
            seriesDao.updateSeries(series);
            return buildEntityUpdatedResponse(Entity.Series, series.getSeriesId());
        } catch (Exception e) {
            return build404Response();
        }
    }

    //Delete
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteSeries(@PathParam("id") int id) {
        init();
        seriesDao.deleteSeriesById(id);
        return buildEntityDeletedResponse(Entity.Series, id);
    }
}
