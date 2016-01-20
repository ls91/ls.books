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

import ls.books.dao.SeriesDao;
import ls.books.domain.Series;

import org.restlet.Context;
import org.skife.jdbi.v2.DBI;

@Path("/rest/series")
public class SeriesResource extends BaseResource {

    private SeriesDao seriesDao = null;

    protected void init() {
        if (seriesDao == null) {
            DataSource dataSource = (DataSource) Context.getCurrent().getAttributes().get("DATA_SOURCE");
            seriesDao = new DBI(dataSource).open(SeriesDao.class);
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
        } catch (Exception e) {
            return Response.serverError().cacheControl(cacheControl).build();
        }

        return buildCreatedOkResponse(series.getSeriesId(), "/rest/series/");
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
            return Response.status(404).cacheControl(cacheControl).build();
        }
    }

    //Update
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateSeries(Series series) {
        init();

        try {
            seriesDao.updateSeries(series);
        } catch (Exception e) {
            Response.status(400).cacheControl(cacheControl).build();
        }

        return Response.ok().cacheControl(cacheControl).entity(jsonBuilder.toJson("Series " + series.getSeriesId() + " updated")).build();
    }

    //Delete
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteSeries(@PathParam("id") int id) {
        init();
        seriesDao.deleteSeriesById(id);
        return buildOkResponse("Series " + id + " deleted");
    }
}
