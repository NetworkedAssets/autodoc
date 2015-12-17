package com.networkedassets.autodoc.transformer.settings;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.networkedassets.autodoc.transformer.settings.Source.SourceType;

public class SourceCheckingDeserializer extends JsonDeserializer<Source> {
	 
    @Override
    public Source deserialize(JsonParser jp, DeserializationContext ctxt) 
      throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);
       
		Source source = new Source();
		source.setName(node.get("name").asText());
        source.setUrl(node.get("url").asText());
        source.setSourceType(SourceType.valueOf(node.get("sourceType").asText()));
        source.setUsername(node.get("username").asText());
        source.setPassword(node.get("password").asText());
        source.setHookKey(node.get("hookKey").asText());
        return source;
    }

	
}
