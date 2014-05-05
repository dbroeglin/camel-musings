package fr.broeglin.camel.ersatz;

import static org.hamcrest.CoreMatchers.equalTo;

import org.apache.camel.test.blueprint.CamelBlueprintTestSupport;
import org.junit.Test;

import com.jayway.restassured.RestAssured;

public class ErsatzRouteTest extends CamelBlueprintTestSupport {

	@Override
	protected String getBlueprintDescriptor() {
		return "blueprint.xml";
	}

	@Test
	public void should_work() throws Exception {
		RestAssured.given().baseUri("http://localhost:8282")
				.given()
				.get("/svc/route")
				.then()
				.statusCode(200)
				.body(equalTo("Hello true"));
	}

}
