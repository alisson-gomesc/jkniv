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
package net.sf.jkniv.whinstone.params;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.whinstone.Queryable;

class NamedParamsOld extends AbstractParam implements AutoBindParams
{
    private static final Logger LOG = LoggerFactory.getLogger(NamedParamsOld.class);
    private StatementAdapterOld stmtAdapter;
    private Queryable queryable;
    
    public NamedParamsOld(StatementAdapterOld stmtAdapter, Queryable queryable)
    {
        super();
        this.stmtAdapter = stmtAdapter;
        this.queryable = queryable;
    }
    
    @Override
    public StatementAdapterOld parameterized(String[] paramsNames)
    {
        int i =0;
        for (String s : paramsNames) 
        {
            Object o = null;
            if (hasInClause(s))
            {
                String paramName = s.substring(3, s.length());// strip substring ":in"
                Object paramValue = queryable.getProperty(paramName);
                if (paramValue != null && paramValue.getClass().isArray())
                    o = (Object[]) paramValue;
                else if (paramValue instanceof Collection)
                    o = ((Collection<?>) paramValue).toArray();
            }
            else
                o = queryable.getProperty(s);

            // FIXME log sql parameters
            LOG.error("Setting SQL Parameter from index [{}] with name [{}] with value of [{}] type of [{}]", i,
                    s, o, (o == null ? "NULL" : o.getClass()));
            
            stmtAdapter.setParameter(s, o);            
        }
        return stmtAdapter;
    }

    @Override
    public void on()
    {
        throw new UnsupportedOperationException("Old implmentation doesn't supports auto bind implementation!");        
    }

    @Override
    public int onBatch()
    {
        throw new UnsupportedOperationException("Old implmentation doesn't supports auto bind implementation!");
    }
    
}
