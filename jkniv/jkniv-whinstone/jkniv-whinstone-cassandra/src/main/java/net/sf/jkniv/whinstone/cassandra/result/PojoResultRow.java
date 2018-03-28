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
package net.sf.jkniv.whinstone.cassandra.result;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Row;

import net.sf.jkniv.reflect.Injectable;
import net.sf.jkniv.reflect.InjectableFactory;
import net.sf.jkniv.reflect.beans.MethodName;
import net.sf.jkniv.reflect.beans.MethodNameFactory;
import net.sf.jkniv.reflect.beans.ObjectProxy;
import net.sf.jkniv.reflect.beans.ObjectProxyFactory;
import net.sf.jkniv.sqlegance.JdbcColumn;
import net.sf.jkniv.sqlegance.OneToMany;
import net.sf.jkniv.sqlegance.ResultRow;
import net.sf.jkniv.sqlegance.classification.ObjectTransform;
import net.sf.jkniv.sqlegance.classification.Transformable;
import net.sf.jkniv.sqlegance.logger.SqlLogger;

/**
 * 
 * Inject the ResultSet at flat Object, basic types like: String, Date, Numbers, Clob, Blob...
 * <p>
 * <strong>This class doesn't supports inject value at Oriented-Object model, like nested objects.</strong>
 * 
 * @author Alisson Gomes
 *
 * @param <T> generic type of {@code Class} object to inject value of <code>ResultSet</code>
 */
public class PojoResultRow<T> implements ResultRow<T, Row>
{
    private final static Logger     LOG    = LoggerFactory.getLogger(PojoResultRow.class);
    private final static MethodName SETTER = MethodNameFactory.getInstanceSetter();
    private final static MethodName GETTER = MethodNameFactory.getInstanceGetter();
    private final SqlLogger         sqlLogger;
    private final Class<T>          returnType;
    private final Set<OneToMany> oneToManies;
    private final Transformable<T>  transformable;
    private JdbcColumn<Row>[] columns;
    
    public PojoResultRow(Class<T> returnType, Set<OneToMany> oneToManies, SqlLogger log)
    {
        this(returnType, null, oneToManies, log);
    }
    
    @SuppressWarnings("unchecked")
    public PojoResultRow(Class<T> returnType, JdbcColumn<Row>[] columns, Set<OneToMany> oneToManies,
            SqlLogger log)
    {
        this.returnType = returnType;
        this.columns = columns;
        this.oneToManies = oneToManies;
        this.sqlLogger = log;
        this.transformable = (Transformable<T>) new ObjectTransform();
    }
    
    public T row(Row rs, int rownum) throws SQLException
    {
        final Map<OneToMany, Object> otmValues;
        otmValues = new HashMap<OneToMany, Object>();
        ObjectProxy<T> proxyRow = ObjectProxyFactory.newProxy(returnType);
        for (JdbcColumn<Row> column : columns)
        {
            OneToMany otm = getOneToMany(column, otmValues);
            if (otm == null)
                setValueOf(column, rs, proxyRow);
            else
            {
                prepareOneToManyValue(otm, column, rs, otmValues);
            }
        }
        for (Entry<OneToMany, Object> entry : otmValues.entrySet())
        {
            
            String attrName = entry.getKey().getProperty();
            String getterName = GETTER.capitalize(attrName);
            String setterName = SETTER.capitalize(attrName);
            Collection<Object> collection = (Collection<Object>) proxyRow.invoke(getterName);
            if (collection == null)
            {
                collection = (Collection<Object>) ObjectProxyFactory.newProxy(entry.getKey().getImpl()).newInstance();
                proxyRow.invoke(setterName, collection);
            }
            //System.out.println("adding to collection: " + entry.getValue());
            collection.add(entry.getValue());
            //proxyRow.invoke(SETTER.capitalize(entry.getKey().getProperty()), entry.getValue());
        }
        //System.out.println(proxyRow.getInstance());
        return proxyRow.getInstance();
    }
    
    private void setValueOf(JdbcColumn<Row> column, Row rs, ObjectProxy<T> proxy) throws SQLException
    {
        Injectable<T> reflect = InjectableFactory.newMethodInjection(proxy);
        Object jdbcObject = null;
        if (column.isBinary())
            jdbcObject = column.getBytes(rs);
        else
            jdbcObject = column.getValue(rs);
        
        if (column.isNestedAttribute())
            reflect.inject(column.getAttributeName(), jdbcObject);
        else
        {
            String method = column.getMethodName();
            if (proxy.hasMethod(method))
                reflect.inject(method, jdbcObject);
            else
                LOG.info("Method [{}] doesn't exists for [{}] to set value [{}]", method,
                        proxy.getTargetClass().getName(), jdbcObject);
        }
    }
    
    private OneToMany getOneToMany(JdbcColumn<Row> jdbcColumn, final Map<OneToMany, Object> otmValues)
    {
        OneToMany otm = null;
        for (OneToMany m : oneToManies)
        {
            if (jdbcColumn.getName().startsWith(m.getProperty() + "."))
            {
                otm = m;
                if (!otmValues.containsKey(otm))
                {
                    ObjectProxy<?> proxy = ObjectProxyFactory.newProxy(otm.getTypeOf());
                    otmValues.put(otm, proxy.newInstance());
                }
                break;
            }
        }
        return otm;
    }
    
    private void prepareOneToManyValue(OneToMany otm, JdbcColumn column, Row rs,
            final Map<OneToMany, Object> otmValues) throws SQLException
    {
        ObjectProxy<?> proxy = ObjectProxyFactory.newProxy(otmValues.get(otm));
        Injectable<?> reflect = InjectableFactory.newMethodInjection(proxy);
        Object jdbcObject = null;
        if (column.isBinary())
            jdbcObject = column.getBytes(rs);
        else
            jdbcObject = column.getValue(rs);
        // otm.property : 'book', JdbcColumn: book.name, capitalize -> setName
        String method = SETTER.capitalize(column.getName().substring(otm.getProperty().length() + 1));
        reflect.inject(method, jdbcObject);
    }
    /*
    private void __prepareOneToManyValue__(OneToMany otm, JdbcColumn column, Row rs) throws SQLException
    {
        ObjectProxy<?> proxy = ObjectProxyFactory.newProxy(otmValues.get(otm));
        Injectable<?> reflect = InjectableFactory.newMethodInjection(proxy);
        Object jdbcObject = null;
        if (column.isBinary())
            jdbcObject = column.getBytes(rs);
        else
            jdbcObject = column.getValue(rs);
        
        // otm.property : 'book', JdbcColumn: book.name, capitalize -> setName
        String attrName = column.getName().substring(otm.getProperty().length()+1);
        String getterName = GETTER.capitalize(attrName);
        String setterName = SETTER.capitalize(attrName);
        Collection<?> collection = (Collection<?>) reflect.inject(getterName, jdbcObject);
        if (collection ==    null)
        {
            collection = (Collection<?>) ObjectProxyFactory.newProxy(otm.getImpl()).newInstance();
        }
        //String method = SETTER.capitalize(column.getName().substring(otm.getProperty().length()+1));
        reflect.inject(method, jdbcObject);
    }
    */
    /*
     * Append prefix <code>set<code> to attributeColumnName and capitalize it.
     * @param attributeColumnName attribute name to capitalize with <code>set</code> prefix
     * @return return capitalize attribute name, sample: identityName -> setIdentityName
     *
    private String capitalizeSetter(String attributeColumnName)// TODO design config capitalize algorithm
    {
        String capitalize = "";
        
        if (attributeColumnName != null)
        {
            int length = attributeColumnName.length();
            capitalize = attributeColumnName.substring(0, 1).toUpperCase(Locale.ENGLISH);
            if (length > 1)
                capitalize += attributeColumnName.substring(1, length);
        }
        //sqlLogger.log(LogLevel.RESULTSET, "Mapping column [{}] to property [{}]", attributeColumnName, "set" + capitalize);
        return "set" + capitalize;
    }
    */
    
    @Override
    public Transformable<T> getTransformable()
    {
        return transformable;
    }
    
    @Override
    public void setColumns(JdbcColumn<Row>[] columns)
    {
        this.columns = columns;
    }
    
}
