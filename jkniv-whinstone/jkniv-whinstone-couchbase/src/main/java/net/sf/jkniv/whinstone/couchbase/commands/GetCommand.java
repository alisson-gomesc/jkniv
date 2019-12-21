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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.Document;
import com.couchbase.client.java.document.EntityDocument;
import com.couchbase.client.java.document.JsonDocument;

import net.sf.jkniv.exception.HandleableException;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.commands.Command;
import net.sf.jkniv.whinstone.commands.CommandHandler;

/**
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public class GetCommand implements Command
{
    private Bucket bucket;
    private final Queryable queryable;
    
    public GetCommand(Queryable queryable)
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

    @SuppressWarnings("unchecked")
    @Override
    public <T> T execute()
    {
        // Document, AbstractDocument, EntityDocument, RawJsonDocument, JsonDocument
        List list = new ArrayList<>();//(T) stmt.rows();
        Document document = null;
        boolean returnTypeIsDoc = Document.class.isAssignableFrom(queryable.getReturnType());
        boolean paramTypeIsDoc = (queryable.getParams() instanceof Document);
        CouchbaseDocument couchbaseDocument = new CouchbaseDocument(queryable);

        if (Map.class.isAssignableFrom(queryable.getReturnType()))
        {
            JsonDocument ret = bucket.get(couchbaseDocument.getId());
            list.add(JsonMapper.mapper(ret.content(), queryable.getReturnType()));
        }
        else if (returnTypeIsDoc)
        {
            //Class<? extends Document> entity = queryable.getReturnType();
            //document = bucket.get(couchbaseDocument.getId(), entity);
            document = bucket.get(couchbaseDocument.getId());
            list.add(document);
        }
        else
        {
            EntityDocument<?> ret = bucket.repository().get(couchbaseDocument.getId(), queryable.getReturnType());
            list.add(ret.content());
        }
        /*
        if (paramTypeIsDoc)
        {
            document = bucket.get((Document)queryable.getParams());
        }
        else if (returnTypeIsDoc)
        {
            Class<? extends Document> entity = queryable.getReturnType();
            document = bucket.get((String)queryable.getParams(), entity);
            //list.add(document);
        }
//        else if (Map.class.isAssignableFrom(queryable.getReturnType()))
//        {
//            JsonDocument ret = bucket.get((String)queryable.getParams());
//            list.add(JsonMapper.mapper(ret.content(), queryable.getReturnType()));
//        }
        else if (queryable.isTypeOfPojo())
        {            
            CouchbaseDocument mydocument = new CouchbaseDocument(queryable);
            EntityDocument<?> ret = bucket.repository().get(mydocument.getId(), queryable.getReturnType());
            list.add(ret.content());
        }
        else
        {
            // com.couchbase.client.java.repository.mapping.RepositoryMappingException: Could not instantiate entity.
            EntityDocument<?> ret = bucket.repository().get((String)queryable.getParams(), queryable.getReturnType());
            list.add(ret.content());
        }
        */
        // handling return type
        /*
        if (returnTypeIsDoc)
        {
            list.add(document);            
        }
        else if (Map.class.isAssignableFrom(queryable.getReturnType()))
        {
            list.add(JsonMapper.mapper(document, queryable.getReturnType()));            
        }
        */
        /*
        if (document != null)
        {
            if (returnTypeIsDoc)
                list.add(document);
            else
            {
                if(document.getClass().isAssignableFrom(queryable.getReturnType()))
                {
                    list.add(document);
                }
                else
                {
                    list.add(JsonMapper.mapper(document.content(), queryable.getReturnType()));
                }
            }
        }
        */
        return (T)list;
    }
}
