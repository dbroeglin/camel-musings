package fr.broeglin.camel.musings.testing;

import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.hamcrest.Matcher;

public abstract class AbstractCamelContextTest extends CamelTestSupport {

	protected <T> void assertThatFirstMessageBody(Class<T> klass, Matcher<? super T> matcher) {
		assertThat(out.getExchanges().get(0).getIn().getBody(klass), matcher);
	}

	// plumbing

	protected abstract void customizeRoute(RouteDefinition def);

	@EndpointInject(uri = "direct:in")
	Endpoint in;

	@EndpointInject(uri = "mock:out")
	MockEndpoint out;

	@Override
	protected RouteBuilder createRouteBuilder() throws Exception {
		return new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				RouteDefinition def = from(in);

				customizeRoute(def);
				def.to(out);
			}
		};
	}

	protected void bindBean(String beanName, SimpleTestBean bean) {
		((JndiRegistry)context.getRegistry()).bind(beanName, bean);
	}
}
