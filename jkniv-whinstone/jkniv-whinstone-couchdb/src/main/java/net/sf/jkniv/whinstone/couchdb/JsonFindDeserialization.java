package net.sf.jkniv.whinstone.couchdb;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class JsonFindDeserialization extends StdDeserializer<CouchResult>
{
    public JsonFindDeserialization() {
        this(null);
    }
 
    public JsonFindDeserialization(Class<?> vc) {
        super(vc);
    }
    
    @Override
    public CouchResult deserialize(JsonParser json, DeserializationContext ctxt)
            throws IOException, JsonProcessingException
    {
        ObjectCodec codec = json.getCodec();
        JsonNode node = codec.readTree(json);
        final String bookmark = node.get("bookmark").asText();
        final String warning = node.get("warning").asText();
        final String contents = node.get("docs").asText();
        
        return null;
        //return new Program(id, name, contents, user);
    }
    
}
