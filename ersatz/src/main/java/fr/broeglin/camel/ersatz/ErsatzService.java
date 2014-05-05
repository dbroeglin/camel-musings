package fr.broeglin.camel.ersatz;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

public class ErsatzService {
	@POST
	@Path("/customers/{type}")
	public Response newCustomer(@PathParam("type") String type,
			@QueryParam("active") @DefaultValue("true") boolean active) {
		return null;
	}

	@GET
	@Path("/")
	public Response test(@PathParam("type") String type,
			@QueryParam("active") @DefaultValue("true") boolean active) {
		return Response.ok("COUCOU").build();
	}
}
