package fr.broeglin.camel.musings.testing;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class TestProcessor implements Processor {

	public void process(Exchange exchange) throws Exception {
		String oldBody = exchange.getIn().getBody(String.class);

		exchange.getIn().setBody(oldBody + "/" + oldBody);
	}

}
