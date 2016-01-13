package com.networkedassets.autodoc.transformer.handleRepoPush.infrastructure;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.networkedassets.autodoc.transformer.handleRepoPush.PushEvent;
import com.networkedassets.autodoc.transformer.handleRepoPush.provide.in.PushEventProcessor;

/**
 * REST service receiving information about new events in Stash
 */
@Path("/event")
public class PushEventService {

	private static final Logger log = LoggerFactory.getLogger(PushEventService.class);
	private final PushEventProcessor eventProcessor;

	@Inject
	public PushEventService(PushEventProcessor eventProcessor) {
		this.eventProcessor = eventProcessor;
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addEvent(PushEvent pushEvent) {
		log.info("New EVENT information received: {}", pushEvent.toString());
		try {

			eventProcessor.processEvent(pushEvent);

			return Response.status(Response.Status.OK).build();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Error while handling event: ", e);
			throw new PushEventServiceException(String.format("{\"error\":\"%s\"}", e.getMessage()));
		}
	}

}
