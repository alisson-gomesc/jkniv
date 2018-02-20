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
package net.sf.jkniv.sqlegance.params;

import java.util.Collection;

import net.sf.jkniv.sqlegance.Queryable;
import net.sf.jkniv.sqlegance.statement.StatementAdapter;

class NamedParams extends AbstractParam implements AutoBindParams
{
    private StatementAdapter<?, ?> stmtAdapter;
    private Queryable              queryable;
    private String[]               paramsNames;
    
    public NamedParams(StatementAdapter<?, ?> stmtAdapter, Queryable queryable)
    {
        super();
        this.stmtAdapter = stmtAdapter;
        this.queryable = queryable;
        this.paramsNames = queryable.getParamsNames();
    }
    
    @Override
    public StatementAdapterOld parameterized(String[] paramsNames)
    {
        on();
        return null;
    }
    
    @Override
    public void on()
    {
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
                    o = ((Collection) paramValue).toArray();
            }
            else
                o = queryable.getProperty(s);
            stmtAdapter.bind(s, o);
        }
    }
    
    @Override
    public int onBatch()
    {
        on();
        return stmtAdapter.execute();
    }
}
