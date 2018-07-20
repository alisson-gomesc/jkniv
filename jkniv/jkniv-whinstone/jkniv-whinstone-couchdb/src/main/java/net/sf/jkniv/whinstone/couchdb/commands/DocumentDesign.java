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

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a design view from CouchDB that contains N views
 *  
 * <pre>
 * {
 *   "_id": "_design/viewer-docs",
 *   "_rev": "1-49b6d7936ce1886691833db68904a129",
 *   "views": {
 *     "natio": {
 *       "map": "// saved by jkniv Fri Jul 20 13:44:30 BRT 2018\nfunction(doc){ emit(doc.nationality, doc)}"
 *     },
 *     "natio2": {
 *       "map": "// saved by jkniv Fri Jul 20 13:44:30 BRT 2018\nfunction(doc){ emit(doc.nationality, doc)}"
 *     }
 *   }
 * }
 * </pre>
 *
 * @author Alisson Gomes
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocumentDesign
{
    @JsonProperty("_id")
    private String              id;
    @JsonProperty("_rev")
    private String              rev;
    @JsonProperty("language")
    private String              language;
    @JsonProperty("views")
    private Map<String, ViewFunction> views = new HashMap<String, ViewFunction>();

    public DocumentDesign()
    {
        this.language = "javascript";
    }


    public String getId()
    {
        return id;
    }


    public void setId(String id)
    {
        this.id = id;
    }


    public String getRev()
    {
        return rev;
    }


    public void setRev(String rev)
    {
        this.rev = rev;
    }

    public Map<String, ViewFunction> getViews()
    {
        return views;
    }

    public void setViews(Map<String, ViewFunction> views)
    {
        this.views = views;
    }
    
    public ViewFunction add(ViewFunction view)
    {
        return this.views.put(view.getName(), view);
    }

    public String getLanguage()
    {
        return language;
    }

    public void setLanguage(String language)
    {
        this.language = language;
    }
    
    
}
