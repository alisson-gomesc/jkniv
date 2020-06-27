package net.sf.jkniv.whinstone.couchdb.commands;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import net.sf.jkniv.whinstone.couchdb.CouchDbResult;

import com.fasterxml.jackson.databind.JsonNode;

public class JsonFindDeserialization extends StdDeserializer<CouchDbResult>
{
    public JsonFindDeserialization() {
        this(null);
    }
 
    public JsonFindDeserialization(Class<?> vc) {
        super(vc);
    }
    
    @Override
    public CouchDbResult deserialize(JsonParser json, DeserializationContext ctxt)
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
