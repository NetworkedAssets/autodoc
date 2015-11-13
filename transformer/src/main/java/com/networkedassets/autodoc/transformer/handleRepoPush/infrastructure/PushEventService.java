package com.networkedassets.autodoc.transformer.handleRepoPush.infrastructure;

import com.networkedassets.autodoc.transformer.handleRepoPush.provide.in.PushEventProcessor;
import com.networkedassets.autodoc.transformer.util.RestService;
import com.networkedassets.autodoc.transformer.handleRepoPush.PushEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


/**
 * REST service receiving information about new events in Stash
 */
@Path("/event")
public class PushEventService extends RestService {

	static final Logger log = LoggerFactory.getLogger(PushEventService.class);

	private final PushEventProcessor eventProcessor;

	@Inject
	public PushEventService(PushEventProcessor eventProcessor) {
		this.eventProcessor = eventProcessor;
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String addEvent(PushEvent pushEvent) {
		log.info("New EVENT information received: {}", pushEvent.toString());
		try {

			eventProcessor.process(pushEvent);

			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Error while handling event: ", e);
			return ERROR;
		}
	}

}
