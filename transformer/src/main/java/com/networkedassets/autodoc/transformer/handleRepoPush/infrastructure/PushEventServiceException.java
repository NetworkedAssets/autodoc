package com.networkedassets.autodoc.transformer.handleRepoPush.infrastructure;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class PushEventServiceException extends WebApplicationException {

	private static final long serialVersionUID = 1L;

	public PushEventServiceException(String message) {
		super(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).type(MediaType.APPLICATION_JSON)
				.build());
	}
}
