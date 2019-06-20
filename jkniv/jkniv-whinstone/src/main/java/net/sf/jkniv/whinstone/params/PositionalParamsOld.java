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

class PositionalParamsOld extends AbstractParam implements AutoBindParams
{
    private StatementAdapterOld stmtAdapter;
    private Object                 params;
    private String queryName;
    
    public PositionalParamsOld(StatementAdapterOld stmtAdapter, Object params, String queryName)
    {
        super();
        this.stmtAdapter = stmtAdapter;
        this.params = params;
        this.queryName = queryName;
    }
    
    public StatementAdapterOld parameterized(String[] paramsNames)
    {
        if (params.getClass().isArray())
        {
            Object[] objs = (Object[]) params;
            int i = 1;
            if (objs.length != paramsNames.length && !hasInClause(paramsNames))
                throw new ParameterException("A query [" + queryName
                        + "] with positional parameters needs an array exactly have the same number of parameters from query.");
            
            for (Object o : objs)
                stmtAdapter.setParameter(i++, o);
        }
        else
            throw new ParameterException(
                    "The parameters of sql [" + queryName + "] is based at array but the parameters is not array");
        return stmtAdapter;
    }

    @Override
    public void on()
    {
        throw new UnsupportedOperationException("Old implmentation doesn't supports auto bind implementation!");        
    }

    @Override
    public int onBulk()
    {
        throw new UnsupportedOperationException("Old implmentation doesn't supports auto bind implementation!");
    }
}
