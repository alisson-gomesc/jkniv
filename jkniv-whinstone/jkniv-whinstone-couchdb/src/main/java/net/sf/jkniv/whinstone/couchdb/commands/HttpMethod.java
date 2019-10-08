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

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;

import net.sf.jkniv.sqlegance.RepositoryException;

/**
 * HTTP methods
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public enum HttpMethod
{
    GET, POST, PUT, DELETE,  HEAD , TRACE, OPTIONS, CONNECT;
    
    
    HttpRequestBase newHttp(String url)
    {
        HttpRequestBase http = null;
        if (this == GET)
            http = new HttpGet(url);
        else if (this == POST)
            http = new HttpPost(url);
        else if (this == PUT)
            http = new HttpPut(url);
        else if (this == DELETE)
            http = new HttpDelete(url);
        else if (this == HEAD)
            http = new HttpHead(url);
        else 
            throw new RepositoryException("CouchDb Repository just supports GET | POST | PUT | DELETE | HEAD http methods"); 
        
        return http;
        
    }
}
