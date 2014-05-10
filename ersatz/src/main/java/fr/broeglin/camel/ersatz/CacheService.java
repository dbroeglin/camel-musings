package fr.broeglin.camel.ersatz;

import org.apache.camel.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheService {

	public static final Logger log = LoggerFactory.getLogger(CacheService.class);

	public Message process(Message message) {

		return message;
	}
}
