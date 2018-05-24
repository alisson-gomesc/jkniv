/* 
 * JKNIV, whinstone one contract to access your database.
 * 
 * Copyright (C) 2017, the original author or authors.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software Foundation, Inc., 
 * 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package net.sf.jkniv.whinstone.couchdb.commands;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import net.sf.jkniv.exception.HandlerException;
import net.sf.jkniv.sqlegance.RepositoryException;

public class JsonMapper
{
    private static HandlerException handlerException;
    
    static
    {
        handlerException = new HandlerException(RepositoryException.class, "Cannot set parameter [%s] value [%s]");
        
        // JsonParseException | JsonMappingException | IOException
        handlerException.config(JsonParseException.class, "Error to parser json non-well-formed content [%s]");
        handlerException.config(JsonMappingException.class, "Error to deserialization content [%s]");
        handlerException.config(UnsupportedEncodingException.class, "Error at json content encoding unsupported [%s]");
        handlerException.config(IOException.class, "Error from I/O json content [%s]");
    }
    
    private JsonMapper()
    {
    }
    
    public static ObjectMapper newMapper()
    {
        ObjectMapper mapper = new ObjectMapper();
        // FIXME design jackson json properties config
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }
    
    public static <T> T mapper(String content, Class<T> valueType)
    {
        try
        {
            return newMapper().readValue(content, valueType);
        }
        catch (Exception e)
        {
            // JsonParseException | JsonMappingException | IOException
            handlerException.handle(e);
        }
        return null;
    }
    
    public static <T> T mapper(Map<String, Object> content, Class<T> valueType)
    {
        try
        {
            //final ObjectMapper mapper = new ObjectMapper(); // jackson's objectmapper
            return newMapper().convertValue(content, valueType);
            //return newMapper().readValue(content, valueType);
        }
        catch (Exception e)
        {
            // JsonParseException | JsonMappingException | IOException
            handlerException.handle(e);
        }
        return null;
    }
    
    public static String mapper(Object o)
    {
        try
        {
            return newMapper().writeValueAsString(o);
        }
        catch (Exception e)
        {
            // JsonParseException | JsonMappingException | IOException
            handlerException.handle(e);
        }
        return null;
    }
}
