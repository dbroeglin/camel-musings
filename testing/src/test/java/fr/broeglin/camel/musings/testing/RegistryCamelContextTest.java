package fr.broeglin.camel.musings.testing;

import static org.hamcrest.CoreMatchers.equalTo;

import org.apache.camel.model.RouteDefinition;
import org.junit.Test;

public class RegistryCamelContextTest extends AbstractCamelContextTest {

	@Test
	public void bean_ref_should_change_body() throws Exception {
		out.expectedMessageCount(1);

		sendBody(in.getEndpointUri(), "test");


		out.assertIsSatisfied();

		assertThatFirstMessageBody(String.class, equalTo("test/test"));
	}

	// plumbing

	@Override
	protected void customizeRoute(RouteDefinition def) {
		bindBean("testBean", new SimpleTestBean());

		def.beanRef("testBean");
	}
}
