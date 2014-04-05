package fr.broeglin.camel.musings.csvtoxml;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.xmlmatchers.XmlMatchers.hasXPath;
import static org.xmlmatchers.transform.XmlConverters.xml;

import javax.xml.namespace.NamespaceContext;
import javax.xml.transform.Source;

import org.apache.camel.EndpointInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.xmlmatchers.namespace.SimpleNamespaceContext;

public class CsvToXmlRouteTest extends CamelTestSupport {

	@Override
	protected RouteBuilder createRouteBuilder() throws Exception {
		return new CsvToXmlRouteBuilder()
				.withFromUri("file:" + inputDirectory.getRoot().getAbsolutePath())
				.withToUri(out.getEndpointUri());
	}

	@Test
	public void shouldTransformCsvIntoTwoXmlMessages() throws Exception {
		out.expectedMessageCount(2);

		FileUtils.write(
				inputDirectory.newFile("20140601T180532Z-vca-stores.csv"),
				"a;b\n1.1;1.2\n2.1;2.2");

		out.assertIsSatisfied();

		// TODO: the timestamp depends on daylight saving!
		assertDocument(outputtedXmlBody(0), "2014-06-01T18:05:32+0200", "1.1", "1.2");
		assertDocument(outputtedXmlBody(1), "2014-06-01T18:05:32+0200", "2.1", "2.2");
	}

	private static final NamespaceContext NSs = new SimpleNamespaceContext()
			.withBinding("t", "urn:test:csv-to-xml");

	@EndpointInject(uri = "mock:out")
	private MockEndpoint out;

	@Rule
	public TemporaryFolder inputDirectory = new TemporaryFolder();

	private Source outputtedXmlBody(int index) {
		return xml(out.getExchanges().get(index).getIn().getBody(String.class));
	}

	private void assertDocument(Source xml, String ts, String field1, String field2) {
		assertThat(xml, hasXPath("/t:Document/@timestamp", equalTo(ts), NSs));
		assertThat(xml, hasXPath("/t:Document/t:Field1", equalTo(field1), NSs));
		assertThat(xml, hasXPath("/t:Document/t:Field2", equalTo(field2), NSs));
	}
}