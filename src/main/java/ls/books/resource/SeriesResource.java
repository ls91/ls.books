package ls.books.resource;

import java.net.URI;
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

    private SeriesDao dao = null;

    protected void init() {
        if (dao == null) {
            DataSource dataSource = (DataSource) Context.getCurrent().getAttributes().get("DATA_SOURCE");
            dao = new DBI(dataSource).open(SeriesDao.class);
        }
    }

    //Create
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSeries(Series series) throws URISyntaxException {
        init();

        try {
            series.setSeriesId(dao.createSeries(series));
        } catch (Exception e) {
            return Response.serverError().cacheControl(cacheControl).build();
        }

        return Response.created(new URI("/rest/series/" + series.getSeriesId())).cacheControl(cacheControl).entity(jsonBuilder.toJson(series.getSeriesId())).build();
    }

    //Read
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSeries() {
        init();
        return Response.ok().cacheControl(cacheControl).entity(jsonBuilder.toJson(dao.getSeries())).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSeriesById(@PathParam("id") int id) {
        init();
        Series result = dao.findSeriesBySeriesId(id);
        
        if (result != null) {
            return Response.ok().cacheControl(cacheControl).entity(jsonBuilder.toJson(result)).build();
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
            dao.updateSeries(series);
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
        dao.deleteSeriesById(id);
        return Response.ok(jsonBuilder.toJson("Series " + id + " deleted")).cacheControl(cacheControl).build();
    }
}
