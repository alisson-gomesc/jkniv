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

import java.util.Iterator;

import net.sf.jkniv.whinstone.Param;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.statement.StatementAdapter;

/**
 * Parameters are a collection of array where a named query isn't supported,
 * so the query must contains positional values "?"
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 *
 */
class PositionalCollectionArrayParams extends AbstractParam implements AutoBindParams
{
    private StatementAdapter<?, ?> stmtAdapter;
    private Iterator<Param>        it;
    private String                 queryName;
    private String[]               paramsNames;
    
    public PositionalCollectionArrayParams(StatementAdapter<?, ?> stmtAdapter, Queryable queryable)
    {
        super();
        this.stmtAdapter = stmtAdapter;
        this.it = queryable.iterator();
        this.queryName = queryable.getName();
        this.paramsNames = queryable.getParamsNames();
    }
    
    @Override
    public void on()
    {
        onBulk();//salient client don't get rows affected
    }
    
    @Override
    public int onBulk()
    {
        // FIXME implements batch using executeBatch
        int rowsAfftected = 0;
        while (it.hasNext())
        {
            Object[] params = (Object[])it.next().getValue();
            if ((paramsNames.length > 0 && params == null) || (paramsNames.length != params.length))
                throw new ParameterNotFoundException(
                        "Query [" + queryName + "] expect [" + paramsNames.length + "] parameter(s) but have "
                                + (params == null ? "NULL" : String.valueOf(params.length)) + " value(s)");
            
            for (int i = 0; i < paramsNames.length; i++)
            {
                stmtAdapter.bind(new Param(params[i], i, paramsNames[i]));
            }
            rowsAfftected += stmtAdapter.execute();
            stmtAdapter.reset();
        }
        return rowsAfftected;
    }
}
