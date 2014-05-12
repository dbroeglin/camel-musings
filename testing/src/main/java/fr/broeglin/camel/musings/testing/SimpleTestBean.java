package fr.broeglin.camel.musings.testing;

public class SimpleTestBean {

	public String doit(String body) {
		return body + "/" + body;
	}
}
