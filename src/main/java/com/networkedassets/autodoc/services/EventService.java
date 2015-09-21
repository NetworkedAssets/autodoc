package com.networkedassets.autodoc.services;

import com.networkedassets.autodoc.EventHandler;
import com.networkedassets.autodoc.event.Event;
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

    @Inject
    private EventHandler eventHandler;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String addEvent(Event event) {
        log.info("New event information received: " + event.toString());

        return SUCCESS;
    }

}
