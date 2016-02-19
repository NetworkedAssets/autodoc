package com.networkedassets.autodoc.configuration;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class TransformerSettingsException extends WebApplicationException {

	private static final long serialVersionUID = 1L;

	public TransformerSettingsException(String message) {
		super(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).type(MediaType.APPLICATION_JSON)
				.build());
	}
}
