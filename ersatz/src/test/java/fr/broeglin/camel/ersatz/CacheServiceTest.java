package fr.broeglin.camel.ersatz;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.apache.camel.Message;
import org.apache.camel.impl.DefaultMessage;
import org.junit.Test;

public class CacheServiceTest {

	CacheService service  = new CacheService();

	@Test
	public void shouldReturnSameMessage() {
		Message msg = new DefaultMessage();

		assertThat(service.process(msg), sameInstance(msg));
	}

}
