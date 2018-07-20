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

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Represent a view function from CouchDB
 * 
 * @author Alisson Gomes 
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
class ViewFunction
{
    @JsonIgnore
    private String name;
    private String map;
    private String reduce;
   
    public ViewFunction()
    {
        super();
    }
    public ViewFunction(String name)
    {
        this();
        this.name = name;
    }

    
    public String getName()
    {
        return name;
    }
    
    public void setMap(String map)
    {
        //this.mapfun = mapfun.replaceAll("\n", "").replaceAll("\"", "'").replaceAll("\\{", "\\{ \\\r");
        this.map = map.replaceAll("\"", "'");
    }
    
    public String getMap()
    {
       return "// saved by jkniv "+new Date()+"\n" + map; 
        //return  mapfun;
    }

    public void setReduce(String reduce)
    {
        this.reduce = reduce.replaceAll("\"", "'");
    }
    
    public String getReduce()
    {
        return reduce;
    }
    
    public boolean hasReduce()
    {
        return (reduce != null && reduce.trim().length() > 0);
    }
    
}
