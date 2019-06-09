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
package net.sf.jkniv.whinstone;

import java.sql.ResultSet;
import java.util.Collections;
import java.util.List;

import net.sf.jkniv.cache.Cacheable;
import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.sqlegance.Selectable;
import net.sf.jkniv.whinstone.statement.StatementAdapter;

public abstract class DefaultQueryHandler extends DefaultCommandHandler
{
    public DefaultQueryHandler(ConnectionAdapter connAdapter)
    {
        super(connAdapter);
    }
    
    @Override
    @SuppressWarnings(
    { "unchecked", "rawtypes" })
    public <T> T run()
    {
        if (LOG.isTraceEnabled())
            LOG.trace("Executing [{}] as {} command", queryable, sql.getSqlType());
        
        List<?> list = Collections.emptyList();
        sql.getValidateType().assertValidate(queryable.getParams());
        Selectable selectable = sql.asSelectable();
        if (!queryable.isBoundSql())
            queryable.bind(selectable);
        
        Cacheable.Entry entry = null;
        
        if (!queryable.isCacheIgnore())
            entry = selectable.getCache().getEntry(queryable);
        try
        {
            if (entry == null)
            {
                preCallback();
                Command command = adapterConn.asSelectCommand(queryable, overloadResultRow);
                list = command.execute();
                postCallback();
                if (selectable.hasCache() && !list.isEmpty())
                    selectable.getCache().put(queryable, list);
                
                paging(list);
            }
            else
            {
                queryable.cached();
                list = (List<?>) entry.getValue();
                if (LOG.isDebugEnabled())
                    LOG.debug("{} object(s) was returned from [{}] cache using query [{}] since {} reach [{}] times",
                            list.size(), selectable.getCache().getName(), sql.getName(), entry.getTimestamp(),
                            entry.hits());
            }
        }
        finally
        {
            this.adapterConn.close();
        }
        //queryable.setTotal(list.size());
        if (LOG.isDebugEnabled())
            LOG.debug("Executed [{}] query as {} command, {} rows fetched", queryable.getName(), sql.getSqlType(),
                    list.size());
        
        return (T) list;
    }
    
    private void paging(List<?> list)
    {
        if (adapterConn.supportsPagingByRoundtrip())
        {
            if (queryable.isPaging())
            {
                StatementAdapter<Number, ResultSet> adapterStmtCount = adapterConn.newStatement(queryable.queryCount());
                queryable.bind(adapterStmtCount).on();
                adapterStmtCount.returnType(Number.class).scalar();
                try
                {
                    Long rows = adapterStmtCount.rows().get(0).longValue();
                    queryable.setTotal(rows);
                }
                catch (RepositoryException e)
                {
                    // FIXME BUG select count with ORDER BY 
                    // The ORDER BY clause is invalid in views, inline functions, derived tables, subqueries, and common table expressions, unless TOP or FOR XML is also specified.
                    LOG.error("Cannot count the total of rows from full query [{}]", queryable.getName(), e);
                }
            }
            else
                queryable.setTotal(list.size());
        }
    }
}
