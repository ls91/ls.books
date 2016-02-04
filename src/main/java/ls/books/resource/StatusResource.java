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

import ls.books.dao.StatusDao;
import ls.books.domain.Entity;
import ls.books.domain.Status;

import org.restlet.Context;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;

@Path("/rest/status")
public class StatusResource extends BaseResource {

    protected static final String STATUS_URL = "/rest/status/%s";

    private StatusDao statusDao = null;

    protected void init() {
        if (statusDao == null) {
            DataSource dataSource = (DataSource) Context.getCurrent().getAttributes().get("DATA_SOURCE");
            statusDao = new DBI(dataSource).open(StatusDao.class);
        }
    }

    //Create
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createStatus(Status status) throws URISyntaxException {
        init();

        try {
            status.setStatusId(statusDao.createStatus(status));
            return buildEntityCreatedResponse(status.getStatusId(), STATUS_URL);
        } catch (UnableToExecuteStatementException e) {
            e.printStackTrace();
            if (e.getMessage().contains("Unique index or primary key violation")) {
                return build404Response("Create Failed, Status already exists.");
            } else {
                return build404Response();
            }
        }
    }

    //Read
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStatuses() {
        init();
        return buildOkResponse(statusDao.getStatuses());
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStatusById(@PathParam("id") int id) {
        init();
        Status result = statusDao.findStatusById(id);
        
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
    public Response updateStatus(Status status) {
        init();

        try {
            statusDao.updateStatus(status);
            return buildEntityUpdatedResponse(Entity.Status, status.getStatusId());
        } catch (UnableToExecuteStatementException e) {
            e.printStackTrace();
            if (e.getMessage().contains("Unique index or primary key violation")) {
                return build404Response("Update Failed, New status already exists.");
            } else {
                return build404Response();
            }
        }
    }

    //Delete
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteStatus(@PathParam("id") int id) {
        init();
        statusDao.deleteStatusById(id);
        return buildEntityDeletedResponse(Entity.Status, id);
    }
}
