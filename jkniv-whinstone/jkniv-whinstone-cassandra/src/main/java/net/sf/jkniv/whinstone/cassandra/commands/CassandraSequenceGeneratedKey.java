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
package net.sf.jkniv.whinstone.cassandra.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.Statement;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;

import net.sf.jkniv.exception.HandleableException;
import net.sf.jkniv.sqlegance.Insertable;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.statement.AutoKey;

/**
 * Call an database sequence and put the value into parameter object from {@link Queryable}
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public class CassandraSequenceGeneratedKey implements AutoKey<Object>
{
    private static final Logger SQLLOG = net.sf.jkniv.whinstone.cassandra.LoggerFactory.getLogger();

    private List<Object> keys;
    
    public CassandraSequenceGeneratedKey(Insertable isql, CqlSession session, HandleableException handlerException)
    {
        this.keys = new ArrayList<Object>();
        //try
        //{
            String query = isql.getAutoGeneratedKey().getText();
            SQLLOG.info(query);
            Statement statement = SimpleStatement.newInstance(query);
            ResultSet generatedKeys= session.execute(statement);
            for(Row row : generatedKeys.all())
            {
                Object id = row.getObject(0);
                this.keys.add(String.valueOf(id));
            }
        //}
        //catch (SQLException e)
        //{
        //    handlerException.handle(e);
        //}
    }
    
    @Override
    public Object getId()
    {
        if (isEmpty())
            return null;

        return (Long) this.keys.get(0);
    }
    
    @Override
    public String getUId()
    {
        if (isEmpty())
            return null;

        return String.valueOf(this.keys.get(0));
    }
    
    @Override
    public Iterator<Object> iterator()
    {
        return this.keys.iterator();
    }
    
    @Override
    public boolean hasItem()
    {
        return this.keys.size() > 0;
    }

    @Override
    public boolean isEmpty()
    {
        return this.keys.isEmpty();
    }

    @Override
    public int size()
    {
        return this.keys.size();
    }

}
