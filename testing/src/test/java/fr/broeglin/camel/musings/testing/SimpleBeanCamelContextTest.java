package fr.broeglin.camel.musings.testing;

import static org.hamcrest.CoreMatchers.equalTo;

import org.apache.camel.model.RouteDefinition;
import org.junit.Test;

public class SimpleBeanCamelContextTest extends AbstractCamelContextTest {

	@Test
	public void simple_bean_should_change_body() throws Exception {
		out.expectedMessageCount(1);

		sendBody(in.getEndpointUri(), "test");

		out.assertIsSatisfied();

		assertThatFirstMessageBody(String.class, equalTo("test/test"));
	}

	@Override
	protected void customizeRoute(RouteDefinition def) {
		def.bean(new SimpleTestBean());
	}
}
