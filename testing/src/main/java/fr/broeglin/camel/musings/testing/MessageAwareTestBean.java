package fr.broeglin.camel.musings.testing;

import org.apache.camel.Message;

public class MessageAwareTestBean {

	public void doit(Message message) {
		String body = message.getBody(String.class);

		message.setBody(body + "/" + body);
	}
}
