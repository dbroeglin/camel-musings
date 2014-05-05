package fr.broeglin.camel.ersatz;

import org.apache.camel.test.blueprint.CamelBlueprintTestSupport;
import org.junit.Test;

public class ErsatzRouteTest extends CamelBlueprintTestSupport {

	@Override
	protected String getBlueprintDescriptor() {

		return "blueprint.xml";
	}

	@Test
	public void should_work()  throws Exception {
		System.err.println("COUCOU");
		Thread.sleep(20000);
	}

}
