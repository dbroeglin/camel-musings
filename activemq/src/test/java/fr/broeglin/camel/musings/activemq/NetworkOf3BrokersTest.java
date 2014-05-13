package fr.broeglin.camel.musings.activemq;

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
import org.junit.After;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetworkOf3BrokersTest extends CamelTestSupport {

	public static final Logger log = LoggerFactory.getLogger(NetworkOf3BrokersTest.class);
	private static final String REMOTE_BROKER_URL1 = "tcp://127.0.0.1:41616";
	private static final String REMOTE_BROKER_URL2 = "tcp://127.0.0.1:51616";
	private static final String LOCAL_BROKER_URL = "tcp://127.0.0.1:61616";

	@EndpointInject(uri = "direct:in1")
	Endpoint in1;

	@EndpointInject(uri = "direct:in2")
	Endpoint in2;

	@EndpointInject(uri = "mock:out")
	MockEndpoint out;

	BrokerService localBroker;

	private void setUpLocalBroker() throws Exception {
		localBroker = createActivemqBroker("local", LOCAL_BROKER_URL);

		NetworkConnector connector = localBroker.addNetworkConnector("static://"
				+ REMOTE_BROKER_URL1);

		connector.setDuplex(true);
		// connector.setDynamicOnly(true);
		// connector.setStaticBridge(true);
		// connector.setStaticallyIncludedDestinations(asList(
		// createDestination("TEST", QUEUE_TYPE),
		// createDestination("TEST_REPLY_TO", QUEUE_TYPE)));

		connector = localBroker.addNetworkConnector("static://"
				+ REMOTE_BROKER_URL2);

		connector.setDuplex(true);

		localBroker.start();
	}

	BrokerService b1;

	BrokerService b2;

	private void setUpRemoteBroker() throws Exception {
		(b1 = createActivemqBroker("remote1", REMOTE_BROKER_URL1)).start();
		(b2 = createActivemqBroker("remote2", REMOTE_BROKER_URL2)).start();
	}

	@After
	public void after() throws Exception {
		localBroker.stop();
		b1.stop();
		b2.stop();
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

		addActivemqComponent("activemqRemote1", REMOTE_BROKER_URL1);
		addActivemqComponent("activemqRemote2", REMOTE_BROKER_URL2);
		addActivemqComponent("activemqLocal", LOCAL_BROKER_URL);

		return new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				from(in1)
						.to("activemqRemote1:queue:TEST?requestTimeout=2000&replyTo=TEST_REPLY_TO")
						.log(INFO, "About to send message on 1 ${body}...")
						.to(out);

				from(in2)
						.to("activemqRemote2:queue:TEST?requestTimeout=2000&replyTo=TEST_REPLY_TO")
						.log(INFO, "About to send message on 2 ${body}...")
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
		template.requestBody(in1, "test message 1");

		out.message(0).body().isEqualTo("test message 1 X");
		out.assertIsSatisfied();

	}

	@Test
	public void should_forward_message2() throws Exception {
		out.expectedMessageCount(1);
		template.requestBody(in2, "test message 2");

		out.message(0).body().isEqualTo("test message 2 X");
		out.assertIsSatisfied();

	}
}
