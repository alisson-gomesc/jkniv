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
package net.sf.jkniv.whinstone.couchbase.commands;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.Document;
import com.couchbase.client.java.error.DocumentDoesNotExistException;

import net.sf.jkniv.exception.HandleableException;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.commands.Command;
import net.sf.jkniv.whinstone.commands.CommandHandler;

/**
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public class ReplaceCommand implements Command
{
    private Bucket bucket;
    private final Queryable queryable;
    
    public ReplaceCommand(Queryable queryable)
    {
        super();
        this.queryable = queryable;
    }
    
    @Override
    public <T> Command with(T stmt)
    {
        this.bucket = (Bucket) stmt;
        return this;
    }
    
    @Override
    public Command with(HandleableException handleableException)
    {
        return this;
    }

    @Override
    public Command with(CommandHandler commandHandler)
    {
        return this;
    }

    @Override @SuppressWarnings("unchecked")
    public <T> T execute()
    {
        CouchbaseDocument baseDocument = new CouchbaseDocument(this.queryable);
        Integer rows = Integer.valueOf("0");
        try
        {
            Document<?> document = bucket.replace(baseDocument.getJsonDocument());
            baseDocument.merge(document);
            rows = Integer.valueOf("1");
        }
        catch(DocumentDoesNotExistException ignore) {}
        
        return (T) rows;
    }
}
