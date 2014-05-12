package fr.broeglin.camel.musings.activemq;

import org.apache.activemq.broker.BrokerPlugin;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.activemq.camel.component.ActiveMQConfiguration;
import org.apache.activemq.network.NetworkConnector;
import org.apache.activemq.security.JaasAuthenticationPlugin;
import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Before;
import org.junit.Test;

public class NetworkOfBrokersTest extends CamelTestSupport {

	@EndpointInject(uri = "direct:in")
	Endpoint in;

	@EndpointInject(uri = "mock:out")
	MockEndpoint out;

	@Before
	public void setUpBrokers() throws Exception {
		setUpRemoteBroker();
		setUpLocalBroker();
		Thread.sleep(500);
	}

	private void setUpLocalBroker() throws Exception {
		BrokerService broker = new BrokerService();
		broker.setBrokerName("local");
		broker.setUseShutdownHook(false);
		broker.setPersistent(false);

		NetworkConnector connector = broker.addNetworkConnector("static://"
				+ "tcp://127.0.0.1:51616");
		connector.setDuplex(true);
		broker.addConnector("tcp://localhost:61616");
		broker.start();
	}

	private void setUpRemoteBroker() throws Exception {
		BrokerService broker = new BrokerService();
		broker.setBrokerName("remote");
		broker.setUseShutdownHook(false);
		broker.setPersistent(false);

		broker.addConnector("tcp://localhost:51616");
		broker.start();
	}

	@Override
	protected RouteBuilder createRouteBuilder() throws Exception {
		ActiveMQConfiguration confRemote = new ActiveMQConfiguration();

		confRemote.setBrokerURL("tcp://127.0.0.1:51616");
		context().addComponent("activemqRemote", new ActiveMQComponent(confRemote));

		ActiveMQConfiguration conf = new ActiveMQConfiguration();

		conf.setBrokerURL("tcp://127.0.0.1:61616");
		context().addComponent("activemqLocal", new ActiveMQComponent(conf));

		return new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				from(in)
						.to("activemqRemote:queue:TEST")
						.to(out);

				from("activemqLocal:queue:TEST")
						.transform(simple("${body} X"));

			}
		};
	}

	@Test
	public void should_forward_message() throws Exception {
		out.expectedMessageCount(1);
		template.requestBody(in, "test message");

		out.message(0).body().isEqualTo("test message X");
		out.assertIsSatisfied();
	}

}
