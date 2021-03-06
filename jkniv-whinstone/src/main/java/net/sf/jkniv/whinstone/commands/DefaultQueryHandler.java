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
package net.sf.jkniv.whinstone.commands;

import java.sql.Statement;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.sf.jkniv.cache.Cacheable;
import net.sf.jkniv.sqlegance.LanguageType;
import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.sqlegance.Selectable;
import net.sf.jkniv.sqlegance.Sql;
import net.sf.jkniv.sqlegance.builder.xml.SqlTag;
import net.sf.jkniv.sqlegance.builder.xml.TagFactory;
import net.sf.jkniv.sqlegance.dialect.SqlFeatureSupport;
import net.sf.jkniv.whinstone.Param;
import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;

/**
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public abstract class DefaultQueryHandler extends DefaultCommandHandler
{
    public DefaultQueryHandler(CommandAdapter cmdAdapter)
    {
        super(cmdAdapter);
    }
    
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T> T run()
    {
        checkSqlConstraints();
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
                try
                {
                    preCallback();
                    Command command = asCommand();
                    list = command.execute();
                    if(queryable.hasFilter())
                        filtering(list);
                    if(queryable.hasSorter())
                        Collections.sort(list, queryable.getSorter());                    
                    postCallback();
                    if (selectable.hasCache() && !list.isEmpty())
                        selectable.getCache().put(queryable, list);
                    
                    paging(list);
                }
                catch(Exception e)
                {
                    queryable.setTotal(Statement.EXECUTE_FAILED);
                    postException();
                    handleableException.handle(e);                    
                }
            }
            else
            {
                queryable.cached();
                queryable.setTotal(Statement.SUCCESS_NO_INFO);
                list = (List<?>) entry.getValue();
                if (LOG.isDebugEnabled())
                    LOG.debug("{} object(s) was returned from [{}] cache using query [{}] since {} reach [{}] times",
                            list.size(), selectable.getCache().getName(), sql.getName(), entry.getTimestamp(),
                            entry.hits());
            }
        }
        finally
        {
            this.cmdAdapter.close();
        }
        //queryable.setTotal(list.size());
        if (LOG.isDebugEnabled())
            LOG.debug("Executed [{}] query as {} command, {} rows fetched", queryable.getName(), sql.getSqlType(),
                    list.size());
        
        return (T) list;
    }
    
    private void filtering(List<?> list)
    {
        Iterator<?> it = list.iterator();
        while(it.hasNext())
        {
            if(!queryable.getFilter().isEqual(it.next()))
            {
                it.remove();
            }
        }
    }
    
    private void paging(List<?> list)
    {
        if (queryable.isPaging())
        {
            Sql dynamicSql = queryable.getDynamicSql(); 
            if (dynamicSql.getSqlDialect().supportsFeature(SqlFeatureSupport.PAGING_ROUNDTRIP))
            {
                try
                {
                    Command command = cmdAdapter.asSelectCommand(createQueryableForPaging(), null);
                    List<Number> rows = command.execute();
                    if (rows.isEmpty())
                        queryable.setTotal(0);
                    else
                        queryable.setTotal(rows.get(0).longValue());
                }
                catch (RepositoryException e)
                {
                    // FIXME BUG select count with ORDER BY 
                    // The ORDER BY clause is invalid in views, inline functions, derived tables, subqueries, and common table expressions, unless TOP or FOR XML is also specified.
                    queryable.setTotal(Statement.SUCCESS_NO_INFO);
                    LOG.error("Could not count the total of rows from full query [{}]", queryable.getName(), e);
                }
                /*
                StatementAdapter<Number, ResultSet> adapterStmtCount = cmdAdapter.newStatement(queryable.queryCount(), dynamicSql.getLanguageType());
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
                    queryable.setTotal(Statement.SUCCESS_NO_INFO);
                    LOG.error("Cannot count the total of rows from full query [{}]", queryable.getName(), e);
                }
                */
            }
            else
                queryable.setTotal(Statement.SUCCESS_NO_INFO);
        }
        else if (queryable.getTotal() < 0)
            queryable.setTotal(list.size());
    }
    
    private Queryable createQueryableForPaging()
    {
        String queryName = "#paging_"+System.currentTimeMillis()+"_for_"+queryable.getName();
        //String[] paramNames = queryable.getDynamicSql().extractNames(queryable.getParams());
        Param[] paramValues = queryable.values();//(paramNames);
        // TODO improve get values as array
        Object[] paramArray = new Object[paramValues.length];
        for(int i=0; i<paramValues.length; i++)
            paramArray[i] = paramValues[i].getValue();
            
        Queryable paging = QueryFactory.ofArray(queryName, queryable.getRegisterType(), paramArray);
        Selectable selectable = TagFactory.newSelect(queryName, LanguageType.NATIVE, queryable.getDynamicSql().getSqlDialect());
        if (selectable instanceof SqlTag)
        {
            SqlTag sqlTag = (SqlTag)selectable;
            sqlTag.addTag(queryable.queryCount());
            paging.bind(selectable);
        }
        paging.scalar();
        return paging;
    }
    
    private void checkSqlConstraints()
    {
        Selectable selectable = this.sql.asSelectable();
        LanguageType type = selectable.getLanguageType();
        if ((type == LanguageType.JPQL || type == LanguageType.HQL) && selectable.getGroupBy().trim().length() > 0)
        {
            throw new IllegalArgumentException("JPQL cannot have group by, just NATIVE or STORED, change the type ["
                    + type + "] from SQL [" + selectable.getName() + "] to execute");
        }
    }

}
