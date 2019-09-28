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
package net.sf.jkniv.whinstone.jdbc.commands;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;

import net.sf.jkniv.exception.HandleableException;
import net.sf.jkniv.reflect.beans.MethodName;
import net.sf.jkniv.reflect.beans.MethodNameFactory;
import net.sf.jkniv.reflect.beans.ObjectProxy;
import net.sf.jkniv.reflect.beans.ObjectProxyFactory;
import net.sf.jkniv.sqlegance.Insertable;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.statement.AutoKey;

/**
 * Call an database sequence and put the value into parameter object from {@link Queryable}
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 * @deprecated using JdbcSequenceGenerateKey
 */
class SettingAutoGeneratedKey //implements AutoKey
{
    private static final Logger SQLLOG = net.sf.jkniv.whinstone.jdbc.LoggerFactory.getLogger();
    private static final MethodName SETTER = MethodNameFactory.getInstanceSetter();
    private final HandleableException handlerException;
    public Queryable queryable;
    public Insertable                 isql;

    public SettingAutoGeneratedKey(Queryable queryable, Insertable isql, HandleableException handlerException)
    {
        this.queryable = queryable;
        this.isql = isql;
        this.handlerException = handlerException;
    }

    public void bind(PreparedStatement stmt)
    {
        //String[] columns = isql.getAutoGeneratedKeyTag().getColumnsAsArray();
        String[] properties = isql.getAutoGeneratedKey().getPropertiesAsArray();
        try
        {
            if (!this.queryable.isTypeOfArray() && !this.queryable.isTypeOfCollection())// FIXME test generate key params input as collection or array
            {
                ObjectProxy<?> proxy = ObjectProxyFactory.of(queryable.getParams());
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                
                if (generatedKeys.next())
                {
                    for(int i=0; i<properties.length; i++)
                        setValue(proxy, properties[i], generatedKeys.getObject(i+1));
                }
                while(generatedKeys.next())
                {
                    for(int i=0; i<properties.length; i++)
                        setValue(proxy, properties[i], generatedKeys.getObject(i+1));
                }
            }
            else if(this.queryable.isTypeOfMap())// FIXME test generate key params input as Map
            {
                Map<String, Object> instance = (Map)queryable.getParams();
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next())
                {
                    for(int i=0; i<properties.length; i++)
                        instance.put(properties[i], generatedKeys.getObject(i+1));
                }
                while(generatedKeys.next())
                {
                    for(int i=0; i<properties.length; i++)
                        instance.put(properties[i], generatedKeys.getObject(i+1));
                }                
            }
            else
                handlerException.throwMessage("Cannot set auto generated key for collections or array instance of parameters at query [%s]", queryable.getName());
        }
        catch (SQLException sqle)
        {
            handlerException.handle(sqle);
        }
    }
    
    private void setValue(ObjectProxy<?> proxy, String property, Object value)
    {
        Object parsedValue = value;
        
        if(value instanceof java.sql.Time)
            parsedValue = new Date(((java.sql.Time)value).getTime());
        else if (value instanceof java.sql.Date)
            parsedValue = new Date(((java.sql.Date)value).getTime());
        else if (value instanceof java.sql.Timestamp)
            parsedValue = new Date(((java.sql.Timestamp)value).getTime());
        
        proxy.invoke(SETTER.capitalize(property), parsedValue);
    }
}
