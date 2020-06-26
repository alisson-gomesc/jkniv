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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import net.sf.jkniv.reflect.beans.ObjectProxy;
import net.sf.jkniv.reflect.beans.ObjectProxyFactory;
import net.sf.jkniv.reflect.beans.PropertyAccess;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.couchdb.CouchResultImpl;

@SuppressWarnings(
{ "unchecked", "rawtypes" })
public class CouchDbJsonDeserialization extends JsonDeserializer<CouchResultImpl>
{
    private final static Logger LOG = LoggerFactory.getLogger(CouchDbJsonDeserialization.class);
    private final static PropertyAccess DEFAULT_ACCESS_ID = new PropertyAccess("id");
    @Override
    public CouchResultImpl deserialize(JsonParser json, DeserializationContext ctxt)
            throws IOException, JsonProcessingException
    {
        ObjectCodec codec = json.getCodec();
        JsonNode field = codec.readTree(json);
        Queryable queryable = JsonMapper.getCurrentQuery();
        Class<?> returnType = queryable.getReturnType();
        PropertyAccess accessId = DEFAULT_ACCESS_ID;
        if(queryable.getDynamicSql() != null)
            accessId = queryable.getDynamicSql().getSqlDialect().getAccessId();
        
        final Long totalRows = 0L;
        final Long offset = 0L;
        String bookmark = null;
        String warning = null;
        if (field.hasNonNull("bookmark"))
            bookmark = field.get("bookmark").asText();
        
        if (field.hasNonNull("warning")) {
            warning = field.get("warning").asText();
            LOG.warn("Query [{}] warnning message: {}", queryable.getName(), warning);
        }
        List rows = Collections.emptyList();
        if (field.has("rows"))
        {
            final JsonNode nodeRows = field.get("rows");
            rows = parserViewResult(nodeRows, returnType, accessId);
        }
        else if (field.has("docs"))
        {
            final JsonNode nodeDocs = field.get("docs");
            rows = parserFindResult(nodeDocs, returnType, accessId);
        }
        return CouchResultImpl.of(totalRows, offset, bookmark, warning, rows);
    }
    
    private List parserViewResult(final JsonNode nodeRows, Class<?> returnType, PropertyAccess accessId)
    {
        final List rows = new ArrayList();
        if (nodeRows.isArray())
        {
            Iterator<JsonNode> it = nodeRows.elements();
            while (it.hasNext())
            {
                JsonNode element = it.next();
                JsonNode row = element.get("value");
                if(element.has("doc"))
                    row = element.get("doc");
                
                Object object = null;
                if (row.isNumber()) 
                {
                    object = row.isDouble() ? row.asDouble() : row.asLong();                
                }
                else if(row.isTextual() && !row.isNull())                        
                    object = row.asText();
                else
                    object = parserRow(returnType, accessId, element, row);
                rows.add(object);
            }
        }
        return rows;
    }

    private List parserFindResult(final JsonNode nodeRows, Class<?> returnType, PropertyAccess accessId)
    {
        final List rows = new ArrayList();
        if (nodeRows.isArray())
        {
            Iterator<JsonNode> it = nodeRows.elements();
            while (it.hasNext())
            {
                JsonNode row = it.next();
                Object object = parserRow(returnType, accessId, null, row);
                rows.add(object);
            }
        }
        return rows;
    }
    
    private Object parserRow(Class<?> returnType, 
            PropertyAccess accessId,  
            JsonNode element, 
            JsonNode row)
    {
        Object object = null;
        if (row.isObject())
        {
            object = JsonMapper.mapper(row.toString(), returnType);
            if(element != null)
                setIdentity(element, object, accessId);
        }
        else
        {
            LOG.error("the value node from json result isn't an object. Cannot parse it to returnType {}", returnType);
        }
        return object;
    }
    
    private void setIdentity(JsonNode element, Object row, PropertyAccess accessId)
    {
        String id = null;
        String key = null;
        if(element.hasNonNull("id"))
            id = element.get("id").asText();
        if(element.hasNonNull("key"))
            key = element.get("key").asText();
        
        if (row instanceof Map)
        {
            ((Map) row).put("id", id);
            ((Map) row).put("key", key);
        }
        else
        {
            ObjectProxy<?> proxy = ObjectProxyFactory.of(row);
            if (proxy.hasMethod(accessId.getWriterMethodName()))
                proxy.invoke(accessId.getWriterMethodName(), id);
            if (proxy.hasMethod("setKey"))
                proxy.invoke("setKey", key);
        }
        
    }
    
}
