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

import ls.books.dao.FormatDao;
import ls.books.domain.Format;

import org.restlet.Context;
import org.skife.jdbi.v2.DBI;

@Path("/rest/format")
public class FormatResource extends BaseResource {

    private FormatDao formatDao = null;

    protected void init() {
        if (formatDao == null) {
            DataSource dataSource = (DataSource) Context.getCurrent().getAttributes().get("DATA_SOURCE");
            formatDao = new DBI(dataSource).open(FormatDao.class);
        }
    }

    //Create
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createFormat(Format format) throws URISyntaxException {
        init();

        try {
            format.setFormatId(formatDao.createFormat(format));
        } catch (Exception e) {
            return Response.serverError().cacheControl(cacheControl).build();
        }

        return buildCreatedOkResponse(format.getFormatId(), "/rest/format/");
    }

    //Read
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFormats() {
        init();
        return buildOkResponse(formatDao.getFormats());
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFormatById(@PathParam("id") int id) {
        init();
        Format result = formatDao.findFormatById(id);
        
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
    public Response updateFormat(Format format) {
        init();

        try {
            formatDao.updateFormat(format);
        } catch (Exception e) {
            Response.status(400).cacheControl(cacheControl).build();
        }

        return Response.ok().cacheControl(cacheControl).entity(jsonBuilder.toJson("Format " + format.getFormatId() + " updated")).build();
    }

    //Delete
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteFormat(@PathParam("id") int id) {
        init();
        formatDao.deleteFormatById(id);
        return buildOkResponse("Format " + id + " deleted");
    }
}