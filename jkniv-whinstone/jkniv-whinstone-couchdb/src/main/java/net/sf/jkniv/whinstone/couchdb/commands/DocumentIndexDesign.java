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

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a index into CouchDB
 *  
 * <pre>
 * {
 *    "index": {
 *       "fields": [
 *          "foo"
 *       ]
 *    },
 *    "name": "foo-json-index",
 *    "type": "json"
 * }
 * </pre>
 *
 * @author Alisson Gomes
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
class DocumentIndexDesign
{
    @JsonProperty("ddoc")
    private String              ddoc;
    @JsonProperty("name")
    private String              name;
    @JsonProperty("type")
    private String              type;
    
    @JsonProperty("partitioned")
    private Boolean partitioned ;

    @JsonProperty("partial_filter_selector")
    private Map<String, Object> partialFilterSelector ;

    @JsonProperty("index")
    private Map<String, Object> index;
    

    public DocumentIndexDesign()
    {
    }


    public String getDdoc()
    {
        return ddoc;
    }


    public void setDdoc(String ddoc)
    {
        this.ddoc = ddoc;
    }


    public String getName()
    {
        return name;
    }


    public void setName(String name)
    {
        this.name = name;
    }


    public String getType()
    {
        return type;
    }


    public void setType(String type)
    {
        this.type = type;
    }


    public Boolean getPartitioned()
    {
        return partitioned;
    }


    public void setPartitioned(Boolean partitioned)
    {
        this.partitioned = partitioned;
    }


    public Map<String, Object> getPartialFilterSelector()
    {
        return partialFilterSelector;
    }


    public void setPartialFilterSelector(Map<String, Object> partialFilterSelector)
    {
        this.partialFilterSelector = partialFilterSelector;
    }


    public Map<String, Object> getIndex()
    {
        return index;
    }


    public void setIndex(Map<String, Object> index)
    {
        this.index = index;
    }


    @Override
    public String toString()
    {
        return "DocumentIndexDesign [ddoc=" + ddoc + ", name=" + name + ", type=" + type + ", partitioned="
                + partitioned + ", partialFilterSelector=" + partialFilterSelector + ", index=" + index + "]";
    }
}
