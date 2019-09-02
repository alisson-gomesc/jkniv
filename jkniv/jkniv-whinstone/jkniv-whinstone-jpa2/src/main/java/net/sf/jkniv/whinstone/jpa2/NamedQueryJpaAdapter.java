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

import net.sf.jkniv.sqlegance.QueryNotFoundException;
import net.sf.jkniv.whinstone.Queryable;

class NamedQueryJpaAdapter extends AbstractQueryJpaAdapter
{
    private static final Logger    LOG        = net.sf.jkniv.whinstone.jpa2.LoggerFactory.getLogger();

    public NamedQueryJpaAdapter(EntityManager em, Queryable queryable, Class<?> mandatoryReturnType)
    {
        super(em, queryable, null);
        try
        {
            if (mandatoryReturnType == null)
                this.queryJpa = em.createNamedQuery(queryable.getName());
            else
                this.queryJpa = em.createNamedQuery(queryable.getName(), mandatoryReturnType);
            
            this.initParams(queryJpa);
        }
        catch (IllegalArgumentException notFound)
        {
            throw new QueryNotFoundException("Named Query not found [" + queryable.getName() + "] check if orm.xml have the query named or it's annotated");
        }
    }
    
    

    private void initParams(Query queryJpa)
    {
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
            queryJpa.setFirstResult(queryable.getOffset()).setMaxResults(queryable.getMax());
    }
    
    private void setMapParams(Query query, Map<String, Object> params)
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
    
    private void setArrayParams(Query query, Object[] params)
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
