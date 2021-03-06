package fr.broeglin.camel.ersatz;

import static org.hamcrest.CoreMatchers.equalTo;

import org.apache.camel.test.blueprint.CamelBlueprintTestSupport;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

import com.jayway.restassured.RestAssured;

public class ErsatzRouteTest extends CamelBlueprintTestSupport {

	@Test
	public void should_work() throws Exception {
		RestAssured.given().baseUri("http://localhost:8282")
				.given()
				.get("/backend/ID")
				.then()
				.statusCode(200)
				.body(equalTo("Hello World"));
	}

	// plumbing

	@Before
	public void stubBackend() {
		stubFor(get(urlEqualTo("/backend/ID"))
				.withHeader("Accept", WireMock.equalTo("*/*"))
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader("Content-Type", "text/plain")
						.withBody("Hello World")));
	}

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(8089);

	@Override
	protected String getBlueprintDescriptor() {
		return "test-blueprint.xml";
	}
}
