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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.sqlegance.Sql;
import net.sf.jkniv.sqlegance.params.ParamParser;
import net.sf.jkniv.sqlegance.LanguageType;
import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.jpa2.params.JpaStatementAdapter;
import net.sf.jkniv.whinstone.params.AutoBindParams;
import net.sf.jkniv.whinstone.params.PrepareParamsFactory;
import net.sf.jkniv.whinstone.params.StatementAdapterOld;

@Deprecated
class QueryFactory
{
    private static final Logger    LOG        = LoggerFactory.getLogger(QueryFactory.class);
    
    
    
    private static Query newNamedQuery(EntityManager em, Queryable queryable)
    {
        Query queryJpa = em.createNamedQuery(queryable.getName());
        if (!queryable.isTypeOfNull())
        {
            if (queryable.isTypeOfMap())
            {
                //PrepareParams<Query> prepareParams = PrepareParamsFactory.newPrepareParams(queryJpa, new ParamParserColonMark(), queryable);
                setMapParams(queryJpa, (Map) queryable.getParams());
            }
            else if (queryable.getParams().getClass().isArray())
            {
                //PrepareParams<Query> prepareParams = PrepareParamsFactory.newPrepareParams(queryJpa, new ParamParserQuestionMark(), queryable);
                setArrayParams(queryJpa, (Object[]) queryable.getParams());
            }
        }
        if (queryable.isPaging())
        {
            queryJpa.setFirstResult(queryable.getOffset());
            queryJpa.setMaxResults(queryable.getMax());
        }
        return queryJpa;
    }
    
    private static void setMapParams(Query query, Map<String, Object> params)
    {
        int i = 0;
        for (String s : params.keySet())
        {
            Object o = params.get(s);
            if (LOG.isDebugEnabled())// TODO making data sqlLogger.mask
                LOG.debug("Setting SQL Parameter from index [{}] with name [{}] with value of [{}] type of [{}]", i++,
                        s, o, (o == null ? "NULL" : o.getClass()));
            
            query.setParameter(s, o);
        }
    }
    
    private static void setArrayParams(Query query, Object[] params)
    {
        int i = 1;
        for (Object o : params)
        {
            if (LOG.isDebugEnabled())// TODO making data sqlLogger.mask
                LOG.debug("Setting SQL Parameter from index [{}] with name [{}] with value of [{}] type of [{}]", i,
                        "?", o, (o == null ? "NULL" : o.getClass()));
            query.setParameter(i++, o);
        }
    }
    
    
}
