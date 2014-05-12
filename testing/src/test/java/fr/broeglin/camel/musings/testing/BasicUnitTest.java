package fr.broeglin.camel.musings.testing;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.junit.Test;

public class BasicUnitTest {

	TestProcessor testProcessor = new TestProcessor();
	SimpleTestBean testBean = new SimpleTestBean();

	@Test
	public void processor_should_set_body_on_in_message() throws Exception {
		DefaultExchange exchange = new DefaultExchange((CamelContext) null);

		exchange.getIn().setBody("test");
		testProcessor.process(exchange);

		assertThat(exchange.getIn().getBody(String.class), equalTo("test/test"));
	}

	@Test
	public void bean_should_return_new_body() throws Exception {
		assertThat(testBean.doit("test"), equalTo("test/test"));
	}
}
