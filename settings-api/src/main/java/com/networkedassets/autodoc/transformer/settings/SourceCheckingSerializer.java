package com.networkedassets.autodoc.transformer.settings;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;


public class SourceCheckingSerializer extends StdSerializer<Source> {

	private static final long serialVersionUID = 1L;

	public SourceCheckingSerializer(Class<Source> t) {
        super(t);
    }

    @Override
    public void serialize(Source source, 
                          JsonGenerator jgen,
                          SerializerProvider sp) throws IOException, JsonGenerationException {
        
        jgen.writeStartObject();      
        jgen.writeStringField("name", source.getName());
        jgen.writeStringField("slug", source.getName());      
        jgen.writeStringField("url", source.getName());    
        jgen.writeStringField("sourceType", source.getName()); 
        jgen.writeBooleanField("sourceExists", source.getSourceExists()); 
        jgen.writeBooleanField("credentialsCorrect",source.getCredentialsCorrect()); 
        jgen.writeBooleanField("verified", source.getVerified()); 
        jgen.writeBooleanField("slugUnique", source.getSlugUnique()); 
        jgen.writeEndObject();
    }
}
