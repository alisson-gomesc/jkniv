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

import net.sf.jkniv.reflect.beans.CapitalNameFactory;
import net.sf.jkniv.reflect.beans.Capitalize;
import net.sf.jkniv.reflect.beans.ObjectProxy;
import net.sf.jkniv.reflect.beans.ObjectProxyFactory;
import net.sf.jkniv.reflect.beans.PropertyAccess;
import net.sf.jkniv.whinstone.Param;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.statement.StatementAdapter;
import net.sf.jkniv.whinstone.types.Convertible;
import net.sf.jkniv.whinstone.types.ConvertibleFactory;

class PositionalCollectionPojoParams extends AbstractParam implements AutoBindParams
{
	private final static Capitalize CAPITAL_GETTER =  CapitalNameFactory.getInstanceOfGetter();
	private StatementAdapter<?, ?> stmtAdapter;
    private Iterator<Param>        it;
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
            Param pojo = it.next();
            ObjectProxy<?> proxy = ObjectProxyFactory.of(pojo.getValue());
            int i = 0;
            for(String paramName : paramsNames)
            {
            	//String getterName = CAPITAL_GETTER.does(paramName);
            	PropertyAccess propertyAccess = new PropertyAccess(paramName, proxy.getTargetClass());
            	Convertible<Object,Object> convertible = ConvertibleFactory.toJdbc(propertyAccess, proxy);
            	Object value = proxy.invoke(propertyAccess.getReadMethod(), pojo.getValue());
                stmtAdapter.bind(new Param(value, convertible.toJdbc(value), paramName, i++));
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
