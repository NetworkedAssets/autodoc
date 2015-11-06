package com.networkedassets.autodoc.transformer.delivery;

import com.networkedassets.autodoc.transformer.usecases.boundary.provide.Event;
import com.networkedassets.autodoc.transformer.usecases.boundary.provide.Command;
import com.networkedassets.autodoc.transformer.usecases.boundary.provide.ProcessEventCommandFactory;

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
public class EventService extends RestService {

	static final Logger log = LoggerFactory.getLogger(EventService.class);

	private final ProcessEventCommandFactory factory;

	@Inject
	public EventService(ProcessEventCommandFactory factory) {
		this.factory = factory;
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String addEvent(Event requestModel) {
		log.info("New EVENT information received: {}", requestModel.toString());
		try {

			Command preprocessEvents = factory.createProcessEventCommand(requestModel);
			preprocessEvents.execute();

			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Error while handling event: ", e);
			return ERROR;
		}
	}

}
