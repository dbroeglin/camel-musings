package fr.broeglin.camel.musings.csvtoxml;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.flatpack.FlatpackDataFormat;
import org.apache.commons.lang3.Validate;

/**
 * Creates the route that:
 * <ul>
 * <li>Consumes files;</li>
 * <li>Extracts a timestamp from the filename;</li>
 * <li>Parses the file as CSV with Flatpack;</li>
 * <li>Splits the result <code>List&gt;Map&lt;<code> into Map messages;</li>
 * <li>Marshalls the Map messages to XML;</li>
 * <li>Transforms each XML into the final document with an XSLT transformation;</li>
 * </ul>
 */
final class CsvToXmlRouteBuilder extends RouteBuilder {

	public String fromUri;
	public String toUri;

	@Override
	public void configure() throws Exception {
		validate();

		onException(Exception.class)
				.handled(true)
				.log("EXCEPTION: ${body}\n${headers}");

		from(fromUri)
				.validate().groovy("request.headers.get('CamelFileName') =~ /^([0-9]{8}T[0-9]{6}Z).*$/")
				.setHeader("TestTimestamp").groovy(FILENAME_TO_TIMESTAMP_CONVERSION_EXPRESSION)
				.unmarshal(createFlatpackDataFormat())
				.log("AFTER UNMARSHALL: ${body}")
				.split(body())
				.marshal().xstream()
				.to("xslt:classpath:fr/broeglin/camel/musings/csvtoxml/test.xsl")
				.to(toUri);
	}

	public CsvToXmlRouteBuilder withFromUri(String fromUri) {
		this.fromUri = fromUri;

		return this;
	}

	public CsvToXmlRouteBuilder withToUri(String toUri) {
		this.toUri = toUri;

		return this;
	}

	private static final String FILENAME_TO_TIMESTAMP_CONVERSION_EXPRESSION =
			"Date.parse(\"yyyyMMdd'T'HHmmss'Z'\", "
					+ "(request.headers.get('CamelFileName') =~ /^([0-9]{8}T[0-9]{6}Z).*$/)[0][1])"
					+ ".format(\"yyyy-MM-dd'T'HH:mm:ssZ\")";

	private void validate() {
		Validate.notEmpty(fromUri, "fromUri is mandatory");
		Validate.notEmpty(toUri, "toUri is mandatory");
	}

	private FlatpackDataFormat createFlatpackDataFormat() {
		FlatpackDataFormat flatpackDataformat = new FlatpackDataFormat();

		flatpackDataformat.setDelimiter(';');
		return flatpackDataformat;
	}
}