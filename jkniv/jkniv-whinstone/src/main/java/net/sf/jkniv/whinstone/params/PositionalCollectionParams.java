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

import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.statement.StatementAdapter;

class PositionalCollectionParams extends AbstractParam implements AutoBindParams
{
    private StatementAdapter<?,?> stmtAdapter;
    private Object                 params;
    private String queryName;
    private String[] paramsNames;
    
    public PositionalCollectionParams(StatementAdapter<?,?> stmtAdapter, Queryable queryable)
    {
        super();
        this.stmtAdapter = stmtAdapter;
        this.params = queryable.getParams();
        this.queryName = queryable.getName();
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
        Object[] objs = ((Collection<?>) params).toArray(new Object[0]);
        if (objs.length != paramsNames.length && !hasInClause(paramsNames))
            throw new ParameterException("A query [" + queryName
                    + "] with positional parameters needs an array exactly have the same number of parameters from query.");
        
        for (Object o : objs)
        {
            stmtAdapter.bind(o);
        }
    }
    
    @Override
    public int onBulk()
    {
        on();
        return stmtAdapter.execute();
    }

   
}
