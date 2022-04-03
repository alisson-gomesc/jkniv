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
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

import net.sf.jkniv.exception.HandlerException;
import net.sf.jkniv.reflect.beans.ObjectProxy;
import net.sf.jkniv.reflect.beans.ObjectProxyFactory;
import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;

public class JsonMapper
{
    private static final Logger LOG = LoggerFactory.getLogger(JsonMapper.class);
    private static HandlerException handlerException;
    static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Map<String, String> JACKSON_MODULES = new HashMap<String, String>();
    private static final ThreadLocal<Queryable> CURRENT_QUERY = new ThreadLocal<Queryable>();
    
    static
    {
        handlerException = new HandlerException(RepositoryException.class, "Cannot set parameter [%s] value [%s]");
        // JsonParseException | JsonMappingException | IOException
        handlerException.config(JsonParseException.class, "Error to parser json non-well-formed content [%s]");
        handlerException.config(JsonMappingException.class, "Error to deserialization content [%s]");
        handlerException.config(UnsupportedEncodingException.class, "Error at json content encoding unsupported [%s]");
        handlerException.config(IOException.class, "Error from I/O json content [%s]");
        
        JACKSON_MODULES.put("ParameterNamesModule", "com.fasterxml.jackson.module.paramnames.ParameterNamesModule");
        JACKSON_MODULES.put("JavaTimeModule",       "com.fasterxml.jackson.datatype.jsr310.JavaTimeModule"); 
        JACKSON_MODULES.put("Jdk8Module",           "com.fasterxml.jackson.datatype.jdk8.Jdk8Module"); 
        JACKSON_MODULES.put("JSR310TimeModule",     "com.fasterxml.jackson.datatype.jsr310.JSR310TimeModule"); 
        JACKSON_MODULES.put("ThreeTenModule",       "com.fasterxml.jackson.datatype.threetenbp.ThreeTenModule");
        
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addDeserializer(CouchDbResultImpl.class, new CouchDbJsonDeserialization());
        MAPPER.registerModule(simpleModule);
        // pretty print
        //String prettyStaff1 = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(staff2);
        //MAPPER.writerWithDefaultPrettyPrinter();

    }
    
    private JsonMapper()
    {
    }
    
    static void setCurrentQuery(Queryable queryable)
    {
        CURRENT_QUERY.set(queryable);
    }

    static Queryable getCurrentQuery()
    {
        Queryable queryable = CURRENT_QUERY.get();
        CURRENT_QUERY.remove();
        if(queryable == null)
        {
            queryable = QueryFactory.of("dummy", Map.class);
        }
        return queryable;
    }
    
    public static void config(JsonInclude.Include include)
    {
        MAPPER.setSerializationInclusion(include);
    }

    public static void config(SerializationFeature feature, boolean state)
    {
        MAPPER.configure(feature, state);
        LOG.info("Jackson serialization feature {} was {}", feature, (state ? "ENABLED" : "DISABLED"));
    }

    public static void config(DeserializationFeature feature, boolean state)
    {
        MAPPER.configure(feature, state);
        LOG.info("Jackson deserialization feature {} was {}", feature, (state ? "ENABLED" : "DISABLED"));
    }

    public static void config(PropertyAccessor property, Visibility visibility)
    {
        MAPPER.setVisibility(property, visibility);
        LOG.info("Jackson setting visibility {} was {}", property, visibility);
    }

    public static void register(String moduleName, boolean state)
    {
        if (!state)
            return;
        
        Module module = null;
        ObjectProxy<Module> proxy = null;
        String classNameOfModule = JACKSON_MODULES.get(moduleName);
        if (classNameOfModule !=  null)
        {
            proxy = ObjectProxyFactory.of(classNameOfModule);
            module = proxy.newInstance();
            if (module != null)
            {
                MAPPER.registerModule(module);
                LOG.info("Jackson module {} was registered with success by class {} ", moduleName , classNameOfModule);
            }
        }
        if (module == null)
            LOG.error("Cannot register the module {}! {} ", moduleName, (classNameOfModule == null ?  "ParameterNamesModule, JavaTimeModule and Jdk8Module are supported to register" : "the class "+classNameOfModule +" was not found at classpath"));

    }

//    public static ObjectMapper newMapper()
//    {
//        //ObjectMapper mapper = new ObjectMapper();
//        // FIXME design jackson json properties config
//        //mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        return MAPPER;
//    }
    
    public static <T> T mapper(String content, Class<T> valueType)
    {
        try
        {
            return MAPPER.readValue(content, valueType);
        }
        catch (Exception e)
        {
            // JsonParseException | JsonMappingException | IOException
            handlerException.handle(e);
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    public static <T> T mapper(String content, TypeReference valueType)
    {
        try
        {
            return MAPPER.readValue(content, valueType);
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
            return MAPPER.convertValue(content, valueType);
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
            return MAPPER.writeValueAsString(o);
        }
        catch (Exception e)
        {
            // JsonParseException | JsonMappingException | IOException
            handlerException.handle(e);
        }
        return null;
    }
}
