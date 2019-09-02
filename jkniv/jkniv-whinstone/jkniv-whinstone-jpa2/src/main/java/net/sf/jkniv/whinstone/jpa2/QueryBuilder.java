/*
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
package net.sf.jkniv.whinstone.jpa2;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.sqlegance.IQuery;
import net.sf.jkniv.sqlegance.Sql;
import net.sf.jkniv.sqlegance.LanguageType;
import net.sf.jkniv.sqlegance.parser.ParameterParser;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.params.ParameterNotFoundException;

@Deprecated
class QueryBuilder
{
    private static final Logger LOG = LoggerFactory.getLogger(QueryBuilder.class);

    public static Query newQuery(Sql isql, EntityManager em, IQuery query)
    {
        String sql = isql.getSql(query.getParams());
        Query queryJpa = create(isql.getLanguageType(), sql, em);
        setParams(queryJpa, sql, query.getParams());
        if (query.getMax() != Integer.MAX_VALUE)
        {
            queryJpa.setFirstResult(query.getStart());
            queryJpa.setMaxResults(query.getMax());
        }
        return queryJpa;
    }

    public static Query newQuery(Sql isql, EntityManager em, Queryable query)
    {
        String sql = isql.getSql(query.getParams());
        Query queryJpa = create(isql.getLanguageType(), sql, em);
        setParams(queryJpa, sql, query.getParams());
        if (query.getMax() != Integer.MAX_VALUE)
        {
            queryJpa.setFirstResult(query.getOffset());
            queryJpa.setMaxResults(query.getMax());
        }
        return queryJpa;
    }

    public static Query newQuery(String sql, LanguageType languageType, EntityManager em, IQuery query)
    {
        Query queryJpa = create(languageType, sql, em);
        setParams(queryJpa, sql, query.getParams());
        if (query.getMax() != Integer.MAX_VALUE)
        {
            queryJpa.setFirstResult(query.getStart());
            queryJpa.setMaxResults(query.getMax());
        }
        return queryJpa;
    }

    public static Query newQuery(String sql, LanguageType languageType, EntityManager em, Queryable query)
    {
        Query queryJpa = create(languageType, sql, em);
        setParams(queryJpa, sql, query.getParams());
        if (query.getMax() != Integer.MAX_VALUE)
        {
            queryJpa.setFirstResult(query.getOffset());
            queryJpa.setMaxResults(query.getMax());
        }
        return queryJpa;
    }

    public static Query newQuery(EntityManager em, IQuery query)
    {
        Query queryJpa = em.createNamedQuery(query.getName());
        if (query.getParams() != null) 
        {
        
            if (query.getParams() instanceof Map) 
            {
                setMapParams(queryJpa, (Map)query.getParams());
            }
            else if (query.getClass().isArray())
            {
                setArrayParams(queryJpa, (Object[])query.getParams());
            }
        }        
        if (query.getMax() != Integer.MAX_VALUE)
        {
            queryJpa.setFirstResult(query.getStart());
            queryJpa.setMaxResults(query.getMax());
        }
        return queryJpa;
    }

    public static Query newQuery(EntityManager em, Queryable query)
    {
        Query queryJpa = em.createNamedQuery(query.getName());
        if (query.getParams() != null) 
        {
            if (query.getParams() instanceof Map) 
            {
                setMapParams(queryJpa, (Map)query.getParams());
            }
            else if (query.getParams().getClass().isArray())
            {
                setArrayParams(queryJpa, (Object[])query.getParams());
            }
        }
        if (query.getMax() != Integer.MAX_VALUE)
        {
            queryJpa.setFirstResult(query.getOffset());
            queryJpa.setMaxResults(query.getMax());
        }
        return queryJpa;
    }
    
    private static void setMapParams(Query query, Map<String, Object> params)
    {
        int i=0;
        for (String s : params.keySet())
        {
            Object o = params.get(s);
            if (LOG.isDebugEnabled())// FIXME making data sqlLogger.mask
                LOG.debug("Setting SQL Parameter from index [{}] with name [{}] with value of [{}] type of [{}]", i++,
                        s, o, (o == null ? "NULL" : o.getClass()));
            
            query.setParameter(s, o);
        }
    }

    private static void setArrayParams(Query query, Object[] params)
    {
        int i = 0;
        for (Object o : params)
        {
            if (LOG.isDebugEnabled())// FIXME making data sqlLogger.mask
                LOG.debug("Setting SQL Parameter from index [{}] with name [{}] with value of [{}] type of [{}]", i,
                        "?", o, (o == null ? "NULL" : o.getClass()));
            query.setParameter(i, o);
            i++;
        }        
    }

    private static void setParams(Query query, String sql, Object params)
    {
        String[] names = ParameterParser.extract(sql);
        
        if (names.length > 0)
        {
            // TODO test me with junit case
            if ("?".equals(names[0])) // parameters based at array position
            {
                if (params.getClass().isArray())
                {
                    Object[] objs = (Object[]) params;
                    int i = 0;
                    if (objs.length != names.length)
                        // TODO re-design Exception
                        throw new RuntimeException("parameters based on an array can not be much different from the SQL [" + sql + "] have");
                    
                    for (Object o : objs)
                    {
                        if (LOG.isDebugEnabled())// FIXME making data sqlLogger.mask
                            LOG.debug("Setting SQL Parameter from index [{}] with name [{}] with value of [{}] type of [{}]", i,
                                    "?", o, (o == null ? "NULL" : o.getClass()));
                        query.setParameter(i, o);
                        i++;
                    }
                }
                else
                    // TODO re-design Exception
                    throw new RuntimeException("The parameters of sql [" + sql + "] is based at array but the parameters is not array");
            }
            else
            {
                int i=0;
                for (String s : names)
                {
                    Object o = getProperty(params, s);
                    if (LOG.isDebugEnabled())// FIXME making data sqlLogger.mask
                        LOG.debug("Setting SQL Parameter from index [{}] with name [{}] with value of [{}] type of [{}]", i++,
                                s, o, (o == null ? "NULL" : o.getClass()));
                    query.setParameter(s, o);
                }
            }
        }
    }
    
    private static Query create(LanguageType languageType, String sql, EntityManager em)
    {
        Query query = null;
        if (languageType == LanguageType.JPQL)
        {
            query = em.createQuery(sql);
        }
        else if (languageType == LanguageType.NATIVE)
        {
            query = em.createNativeQuery(sql);
        }
        else
        {
            // TODO re-design Exception
            throw new RuntimeException("This implementation just supports LanguageType.JPQL and LanguageType.NATIVE");
        }
        return query;
    }
    
    private static Object getProperty(Object params, String name)
    {
        Object ret = null;
        try
        {
            ret = PropertyUtils.getProperty(params, name);// FIXME implements getProperty access get method not class field!
        }
        catch (Exception e)
        {            
            throw new ParameterNotFoundException(
                "Cannot find the property [" + name + "] at param object [" + (params != null ? params.getClass().getName() : "null") + "] ");
        }
        return ret;
    }
}
