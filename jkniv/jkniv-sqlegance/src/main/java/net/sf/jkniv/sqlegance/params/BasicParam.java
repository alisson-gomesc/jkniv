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

import javax.swing.text.StyleContext.SmallAttributeSet;

import net.sf.jkniv.sqlegance.Queryable;
import net.sf.jkniv.sqlegance.statement.StatementAdapter;

class BasicParam implements AutoBindParams
{
    private StatementAdapter<?,?> stmtAdapter;
    private Object params;
    private String[] paramsNames;

    public BasicParam(StatementAdapter<?,?> stmtAdapter, Queryable queryable)
    {
        super();
        this.stmtAdapter = stmtAdapter;
        this.params = queryable.getParams();
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
        if ("?".equals(paramsNames[0]))
            stmtAdapter.bind(params);
        else 
        {
            stmtAdapter.bind(paramsNames[0], params);
        }
    }

    @Override
    public int onBatch()
    {
        on();
        return stmtAdapter.execute();
    }
    
}
