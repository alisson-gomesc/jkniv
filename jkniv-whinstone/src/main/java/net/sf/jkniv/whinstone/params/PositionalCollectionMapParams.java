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
import java.util.Map;

import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.statement.StatementAdapter;

@SuppressWarnings("unchecked")
class PositionalCollectionMapParams extends AbstractParam implements AutoBindParams
{
    private StatementAdapter<?, ?> stmtAdapter;
    private Iterator<Object>          it;
    private String                 queryName;
    private String[]               paramsNames;
    
    public PositionalCollectionMapParams(StatementAdapter<?, ?> stmtAdapter, Queryable queryable)
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
        while(it.hasNext())
        {
            Map<String, Object> params = (Map<String, Object>) it.next();
            for(String paramName : paramsNames)
            {
                stmtAdapter.bind(paramName, params.get(paramName));
            }
            //Statement.SUCCESS_NO_INFO;
            rowsAfftected += stmtAdapter.execute();
            stmtAdapter.reset();
        }
        return rowsAfftected;
    }
}
