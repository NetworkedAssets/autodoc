package com.networkedassets.autodoc.transformer.handleRepoPush.infrastructure;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

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
	private static final int TIME_OUT = 10000;

	@Inject
	public PushEventService(PushEventProcessor eventProcessor) {
		this.eventProcessor = eventProcessor;
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public void addEvent(@Suspended final AsyncResponse asyncResponse, PushEvent pushEvent) {
		log.info("New EVENT information received: {}", pushEvent.toString());

		CompletableFuture.runAsync(() -> eventProcessor.processEvent(pushEvent))
				.thenApply((result) -> asyncResponse.resume(Response.status(Response.Status.OK).build()))
				.exceptionally(e -> {
					log.error("Event processing failed {}", pushEvent.toString(), e);
					asyncResponse.resume(Response.status(Status.INTERNAL_SERVER_ERROR).entity(e).build());
					return false;
				});

		asyncResponse.setTimeout(TIME_OUT, TimeUnit.MILLISECONDS);
		asyncResponse.setTimeoutHandler(ar -> ar.resume(Response.status(Status.ACCEPTED).build()));

	}

}
