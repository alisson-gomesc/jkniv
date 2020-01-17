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
package net.sf.jkniv.whinstone.statement;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.reflect.beans.ObjectProxy;
import net.sf.jkniv.reflect.beans.ObjectProxyFactory;
import net.sf.jkniv.reflect.beans.PropertyAccess;
import net.sf.jkniv.sqlegance.types.Converter;
import net.sf.jkniv.sqlegance.types.Converter.EnumType;
import net.sf.jkniv.whinstone.JdbcColumn;
import net.sf.jkniv.sqlegance.types.Convertible;
import net.sf.jkniv.sqlegance.types.EnumNameType;
import net.sf.jkniv.sqlegance.types.EnumOrdinalType;
import net.sf.jkniv.sqlegance.types.NoConverterType;
import net.sf.jkniv.sqlegance.types.UnknowType;

/**
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public class ConvertibleFactory
{
    private final static Logger                    LOG = LoggerFactory.getLogger(ConvertibleFactory.class);
    private final static Map<TypeMap, Convertible<Object, Object>> REGISTRY = new HashMap<TypeMap, Convertible<Object, Object>>();
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void register(Convertible convertible)
    {
        LOG.info("Registering converter {} {}", convertible);
        Convertible<Object, Object> c = REGISTRY.put(new TypeMap(convertible.getType(), convertible.getColumnType()), convertible);
        if (c != null && c != convertible)
            LOG.warn("The converter {} was replaced by {}", c, convertible);
        
        TypeMap typeMapByClass = new TypeMap(convertible.getType(), UnknowType.getInstance());
        if (!REGISTRY.containsKey(typeMapByClass))
        {
            c = REGISTRY.put(typeMapByClass, convertible);
            if (c != null && c != convertible)
                LOG.warn("The class converter {} was replaced by {}", c, convertible);
        }
    }
    
    /**
     * Retrieve a {@link Convertible} instance to customize the
     * value of database to class field.
     * @param <T> type of object into proxy reference
     * @param access for property
     * @param proxy of return type from query
     * @return A convertible instance if found into class proxy or {@link NoConverterType}
     * instance when the field or method is not annotated.
     */
    public static <T> Convertible<Object, Object> toJdbc(PropertyAccess access, ObjectProxy<T> proxy)
    {
        Convertible<Object, Object> convertible = getConverterByAnnotation(access.getField(), access.getReadMethod(), proxy);
        if (convertible == null)
        {
            if(access.hasField())
                convertible = REGISTRY.get(new TypeMap(access.getField().getType(), UnknowType.getInstance()));
            if (convertible == null)
                convertible = NoConverterType.getInstance();
        }
        return convertible;
    }

    public static Convertible<Object, Object> getConverter(Class classType)
    {
        Convertible<Object, Object> convertible = REGISTRY.get(new TypeMap(classType, UnknowType.getInstance()));
        if (convertible == null)
            convertible = NoConverterType.getInstance();

        return convertible;
    }
    
    /**
     * Retrieve a {@link Convertible} instance to customize the
     * value of database to class field.
     * @param <T> type of object into proxy reference
     * @param <R> the result of a query (like a {@link java.sql.ResultSet}
     * @param column metadata of it
     * @param proxy of return type from query
     * @return A convertible instance if found into class proxy or {@link NoConverterType}
     * instance when the field or method is not annotated.
     */
    public static <T,R> Convertible<Object, Object> toAttribute(JdbcColumn<R> column, ObjectProxy<T> proxy)
    {
        PropertyAccess access = column.getPropertyAccess();
        Convertible<Object, Object> convertible = getConverterByAnnotation(access.getField(), access.getWriterMethod(), proxy);
        if (convertible == null)
        {
            if (column.getPropertyAccess().hasField())
                convertible = REGISTRY.get(new TypeMap(column.getPropertyAccess().getField().getType(), column.getType()));
            else if (column.getPropertyAccess().hasWriterMethod())
                convertible = REGISTRY.get(new TypeMap(column.getPropertyAccess().getWriterMethod().getParameterTypes()[0], column.getType()));
            
            if (convertible == null)
                convertible = NoConverterType.getInstance();
        }
        return convertible;
    }
    
    /**
     * Retrieve a {@link Convertible} instance to customize the
     * value of database to class field.
     * @param <T> type of proxy instance
     * @param field attribute name from class
     * @param method getter or setter method
     * @param proxy of return type from query
     * @return A convertible instance if found into class proxy or {@link NoConverterType}
     * instance when the field or method is not annotated.
     */
    @SuppressWarnings(
    { "unchecked", "rawtypes" })
    private static <T> Convertible<Object, Object> getConverterByAnnotation(Field field, Method method,
            ObjectProxy<T> proxy)
    {
        Convertible convertible = null;
        Converter converter = null;
        
        if (field == null || method == null || Map.class.isAssignableFrom(proxy.getTargetClass())
                || Collection.class.isAssignableFrom(proxy.getTargetClass()) || proxy.getTargetClass().isArray())
            return convertible;
        
        converter = (Converter) method.getAnnotation(Converter.class);
        if (converter == null)
            converter = (Converter) field.getAnnotation(Converter.class);
        
        if (converter != null)
        {
            ObjectProxy proxyConvertible = null;
            if (converter.converter().isEnum())
            {
                if (converter.isEnum() == EnumType.ORDINAL)
                    convertible = new EnumOrdinalType(converter.converter());
                else
                    convertible = new EnumNameType(converter.converter());
            }
            else
            {
                proxyConvertible = ObjectProxyFactory.of(converter.converter());
                if (converter.pattern() != null)
                    proxyConvertible.setConstructorArgs(converter.pattern());
                convertible = (Convertible) proxyConvertible.newInstance();
            }
        }
        return convertible;
    }
    
    private static <T> Converter getConverterByMethod(Method method, ObjectProxy<T> proxy)
    {
        Converter converter = (Converter) method.getAnnotation(Converter.class);
        return converter;
    }

    private static <T> Converter getConverterByField(Field field, ObjectProxy<T> proxy)
    {
        Converter converter = (Converter) field.getAnnotation(Converter.class);
        return converter;
    }

}
