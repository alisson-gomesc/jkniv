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

import java.sql.Statement;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.jkniv.reflect.beans.MethodName;
import net.sf.jkniv.reflect.beans.MethodNameFactory;
import net.sf.jkniv.reflect.beans.ObjectProxy;
import net.sf.jkniv.reflect.beans.ObjectProxyFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.statement.StatementAdapter;

@SuppressWarnings("unchecked")
class PositionalCollectionPojoParams extends AbstractParam implements AutoBindParams
{
	private final static MethodName SETTER =  MethodNameFactory.getInstanceSetter();
	private final static MethodName GETTER =  MethodNameFactory.getInstanceGetter();
	private StatementAdapter<?, ?> stmtAdapter;
    private Iterator<Object>          it;
    private String                 queryName;
    private String[]               paramsNames;
    
    public PositionalCollectionPojoParams(StatementAdapter<?, ?> stmtAdapter, Queryable queryable)
    {
        super();
        this.stmtAdapter = stmtAdapter;
        this.it = queryable.iterator();
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
        onBatch();//salient client don't get rows affected
    }

    @Override
    public int onBatch()
    {
        // FIXME implements batch using executeBatch
        int rowsAfftected = 0;
        while(it.hasNext())
        {
            Object pojo = it.next();
            ObjectProxy<?> proxy = ObjectProxyFactory.newProxy(pojo);
            for(String paramName : paramsNames)
            {
            	String getterName = GETTER.capitalize(paramName);
            	Object value = proxy.invoke(getterName, pojo);
                stmtAdapter.bind(paramName, value);
            }
            //Statement.SUCCESS_NO_INFO;
            rowsAfftected += stmtAdapter.execute();
            stmtAdapter.reset();
        }
        return rowsAfftected;
    }

	public static <T,R> AutoBindParams newPositionalCollectionMapParams(StatementAdapter<T,R> adapter, Queryable queryable)
	{
	    return new PositionalCollectionPojoParams(adapter, queryable);
	}
	

}
