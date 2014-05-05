package fr.broeglin.camel.ersatz;

import org.apache.camel.builder.RouteBuilder;

public class ErsatzRouteBuilder extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		from("cxfrs:bean:cxfEndpoint?resourceClasses=org.apache.camel.rs.Example&setDefaultBus=true")
		.log("foo");
	}

}
