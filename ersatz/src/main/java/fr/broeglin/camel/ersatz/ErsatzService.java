package fr.broeglin.camel.ersatz;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

public class ErsatzService {
	@GET
	@Path("/backend/{uri}")
	public Response test(
			@PathParam("uri") String service
			) {
		return Response.ok("SHOULD NEVER APPEAR").build();
	}

}
