package fr.broeglin.camel.musings.activemq;

import static java.util.Arrays.asList;
import static org.apache.activemq.command.ActiveMQDestination.QUEUE_TYPE;
import static org.apache.activemq.command.ActiveMQDestination.createDestination;
import static org.apache.camel.LoggingLevel.INFO;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.activemq.camel.component.ActiveMQConfiguration;
import org.apache.activemq.network.NetworkConnector;
import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetworkOfBrokersTest extends CamelTestSupport {

	public static final Logger log = LoggerFactory.getLogger(NetworkOfBrokersTest.class);
	private static final String REMOTE_BROKER_URL = "tcp://127.0.0.1:51616";
	private static final String LOCAL_BROKER_URL = "tcp://127.0.0.1:61616";

	@EndpointInject(uri = "direct:in")
	Endpoint in;

	@EndpointInject(uri = "mock:out")
	MockEndpoint out;

	private void setUpLocalBroker() throws Exception {
		BrokerService broker = createActivemqBroker("local", LOCAL_BROKER_URL);

		NetworkConnector connector = broker.addNetworkConnector("static://"
				+ REMOTE_BROKER_URL);

		connector.setDuplex(true);
		connector.setStaticBridge(true);
		connector.setStaticallyIncludedDestinations(asList(
				createDestination("TEST", QUEUE_TYPE),
				createDestination("TEST_REPLY_TO", QUEUE_TYPE)));

		broker.start();
	}

	private void setUpRemoteBroker() throws Exception {
		createActivemqBroker("remote", REMOTE_BROKER_URL).start();
	}

	private BrokerService createActivemqBroker(String brokerName, String bindAddress)
			throws Exception {
		log.debug("Setting up ActiveMQ broker {}: {}", brokerName, bindAddress);
		BrokerService broker = new BrokerService();

		broker.setBrokerName(brokerName);
		broker.setUseShutdownHook(false);
		broker.setPersistent(false);
		broker.setManagementContext(null);
		broker.setUseJmx(false);

		broker.addConnector(bindAddress);
		return broker;
	}

	@Override
	protected RouteBuilder createRouteBuilder() throws Exception {
		setUpRemoteBroker();
		setUpLocalBroker();

		addActivemqComponent("activemqRemote", REMOTE_BROKER_URL);
		addActivemqComponent("activemqLocal", LOCAL_BROKER_URL);

		return new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				from(in)
						.to("activemqRemote:queue:TEST?requestTimeout=2000&replyTo=TEST_REPLY_TO")
						.log(INFO, "About to send message ${body}...")
						.to(out);

				from("activemqLocal:queue:TEST")
						.log(INFO, "About to transform message ${body}...")
						.transform(simple("${body} X"));
			}
		};
	}

	private void addActivemqComponent(String scheme, String brokerURL) {
		ActiveMQConfiguration confRemote = new ActiveMQConfiguration();

		confRemote.setBrokerURL(brokerURL);
		ActiveMQComponent component = new ActiveMQComponent(confRemote);

		context().addComponent(scheme, component);
	}

	@Test
	public void should_forward_message() throws Exception {
		out.expectedMessageCount(1);
		template.requestBody(in, "test message");

		out.message(0).body().isEqualTo("test message X");
		out.assertIsSatisfied();
	}

}
