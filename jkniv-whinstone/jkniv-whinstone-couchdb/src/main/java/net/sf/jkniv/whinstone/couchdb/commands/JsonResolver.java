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

import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.whinstone.couchdb.CouchResult;
import net.sf.jkniv.whinstone.couchdb.CouchResultImpl;

public class JsonResolver
{
    final Class<?> returnType;
    final CouchResult answer;

    private JsonResolver(String json, Class<?> returnType)
    {
        this.returnType = returnType;
        try
        {
            this.answer = JsonMapper.MAPPER.readerFor(CouchResultImpl.class).readValue(json);
        }
        catch (Exception e)// JsonProcessingException | IOException
        {
            throw new RepositoryException("Cannot process JSON content to CouchResultImpl", e);
        }
    }

    public static JsonResolver of(String json, Class<?> returnType)
    {
        return new JsonResolver(json, returnType);
    }
}
