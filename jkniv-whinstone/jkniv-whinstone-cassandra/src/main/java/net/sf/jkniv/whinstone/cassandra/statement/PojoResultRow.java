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
package net.sf.jkniv.whinstone.cassandra.statement;

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
import net.sf.jkniv.reflect.beans.CapitalNameFactory;
import net.sf.jkniv.reflect.beans.Capitalize;
import net.sf.jkniv.reflect.beans.ObjectProxy;
import net.sf.jkniv.reflect.beans.ObjectProxyFactory;
import net.sf.jkniv.sqlegance.OneToMany;
import net.sf.jkniv.sqlegance.logger.DataMasking;
import net.sf.jkniv.whinstone.JdbcColumn;
import net.sf.jkniv.whinstone.ResultRow;
import net.sf.jkniv.whinstone.classification.ObjectTransform;
import net.sf.jkniv.whinstone.classification.Transformable;
import net.sf.jkniv.whinstone.statement.AbstractResultRow;

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
class PojoResultRow<T> extends AbstractResultRow implements ResultRow<T, Row>
{
    private final static Logger      LOG    = LoggerFactory.getLogger(PojoResultRow.class);
    private static final Logger      SQLLOG = net.sf.jkniv.whinstone.cassandra.LoggerFactory.getLogger();
    private static final DataMasking MASKING = net.sf.jkniv.whinstone.cassandra.LoggerFactory.getDataMasking();
    private final static Capitalize  CAPITAL_SETTER = CapitalNameFactory.getInstanceOfSetter();
    private final static Capitalize  CAPITAL_GETTER = CapitalNameFactory.getInstanceOfGetter();
    private final Class<T>          returnType;
    private final Set<OneToMany>    oneToManies;
    private final Transformable<T>  transformable;
    private JdbcColumn<Row>[]       columns;
    
    public PojoResultRow(Class<T> returnType, Set<OneToMany> oneToManies)
    {
        this(returnType, null, oneToManies);
    }
    
    @SuppressWarnings("unchecked")
    public PojoResultRow(Class<T> returnType, JdbcColumn<Row>[] columns, Set<OneToMany> oneToManies)
    {
        super(SQLLOG, MASKING);
        this.returnType = returnType;
        this.columns = columns;
        this.oneToManies = oneToManies;
        this.transformable = (Transformable<T>) new ObjectTransform();
    }
    
    public T row(Row rs, int rownum) throws SQLException
    {
        final Map<OneToMany, Object> otmValues;
        otmValues = new HashMap<OneToMany, Object>();
        ObjectProxy<T> proxyRow = ObjectProxyFactory.of(returnType);
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
            String getterName = CAPITAL_GETTER.does(attrName);
            String setterName = CAPITAL_SETTER.does(attrName);
            Collection<Object> collection = (Collection<Object>) proxyRow.invoke(getterName);
            if (collection == null)
            {
                collection = (Collection<Object>) ObjectProxyFactory.of(entry.getKey().getImpl()).newInstance();
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
        Injectable<T> reflect = InjectableFactory.of(proxy);
        Object jdbcObject = getValueOf(column, rs);
//        if (column.isBinary())
//            jdbcObject = column.getBytes(rs);
//        else
//            jdbcObject = column.getValue(rs);

        if(SQLLOG.isTraceEnabled())
            SQLLOG.trace("Mapping index [{}] column [{}] type of [{}] to value [{}]", 
                    column.getIndex(), column.getAttributeName(), 
                    (jdbcObject != null ? jdbcObject.getClass().getName() : "null"), 
                    MASKING.mask(column.getAttributeName(), jdbcObject));

        if (column.isNestedAttribute())
            reflect.inject(column.getAttributeName(), jdbcObject);
        else
        {
            String method = column.getMethodName();
            if (proxy.hasMethod(method))
                reflect.inject(method, jdbcObject);
            else
                LOG.warn("Method [{}] doesn't exists for [{}] to set value [{}]", method,
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
                    ObjectProxy<?> proxy = ObjectProxyFactory.of(otm.getTypeOf());
                    otmValues.put(otm, proxy.newInstance());
                }
                break;
            }
        }
        return otm;
    }
    
    private void prepareOneToManyValue(OneToMany otm, JdbcColumn column, Row rs, final Map<OneToMany, Object> otmValues)
            throws SQLException
    {
        ObjectProxy<?> proxy = ObjectProxyFactory.of(otmValues.get(otm));
        Injectable<?> reflect = InjectableFactory.of(proxy);
        Object jdbcObject = getValueOf(column, rs);
        // otm.property : 'book', JdbcColumn: book.name, capitalize -> setName
        String method = CAPITAL_SETTER.does(column.getName().substring(otm.getProperty().length() + 1));
        
        if(SQLLOG.isTraceEnabled())
            SQLLOG.trace("Mapping index [{}] column [{}] type of [{}] to value [{}]", 
                    column.getIndex(), column.getAttributeName(), 
                    (jdbcObject != null ? jdbcObject.getClass().getName() : "null"), 
                    MASKING.mask(column.getAttributeName(), jdbcObject));

        reflect.inject(method, jdbcObject);
    }

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
