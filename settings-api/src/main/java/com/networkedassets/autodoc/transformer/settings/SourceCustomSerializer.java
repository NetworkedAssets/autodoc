package com.networkedassets.autodoc.transformer.settings;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

//TODO: Remove object mapper will be in transformer
public class SourceCustomSerializer extends StdSerializer<Source> {

	private static final long serialVersionUID = 1L;

	public SourceCustomSerializer(Class<Source> t) {
		super(t);
	}

	@Override
	public void serialize(Source source, JsonGenerator jgen, SerializerProvider sp)
			throws IOException, JsonGenerationException {

		jgen.writeStartObject();
		jgen.writeStringField("name", source.getName());
		jgen.writeStringField("url", source.getUrl());
		jgen.writeStringField("sourceType", source.getSourceType().name());
		jgen.writeStringField("username", source.getUsername());
		jgen.writeStringField("password", source.getPassword());
		jgen.writeEndObject();
	}
}
