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
import ls.books.domain.Entity;
import ls.books.domain.Format;

import org.restlet.Context;
import org.skife.jdbi.v2.DBI;

@Path("/rest/format")
public class FormatResource extends BaseResource {

    protected static final String FORMAT_URL = "/rest/format/%s";

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
            return buildEntityCreatedResponse(format.getFormatId(), FORMAT_URL);
        } catch (Exception e) {
            return build404Response();
        }
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
            return build404Response();
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
            return buildEntityUpdatedResponse(Entity.Format, format.getFormatId());
        } catch (Exception e) {
            return build404Response();
        }
    }

    //Delete
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteFormat(@PathParam("id") int id) {
        init();
        formatDao.deleteFormatById(id);
        return buildEntityDeletedResponse(Entity.Format, id);
    }
}
