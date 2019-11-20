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

import java.util.Collection;
import java.util.Map;

import net.sf.jkniv.reflect.beans.ObjectProxy;
import net.sf.jkniv.reflect.beans.ObjectProxyFactory;
import net.sf.jkniv.reflect.beans.PropertyAccess;
import net.sf.jkniv.sqlegance.types.Converter;
import net.sf.jkniv.sqlegance.types.Converter.EnumType;
import net.sf.jkniv.sqlegance.types.Convertible;
import net.sf.jkniv.sqlegance.types.EnumNameType;
import net.sf.jkniv.sqlegance.types.EnumOrdinalType;
import net.sf.jkniv.sqlegance.types.NoConverterType;

/**
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public class ConvertibleFactory
{
    /**
     * Retrieve a {@link Convertible} instance to customize the
     * value of database to class field.
     * @param column Column of row
     * @param proxy of return type from query
     * @return A convertible instance if found into class proxy or {@link NoConverterType}
     * instance when the field or method is not annotated.
     */
    public static <T> Convertible<Object, Object> toJdbc(PropertyAccess access, ObjectProxy<T> proxy)
    {
        return getConverter(proxy, access.getFieldName(), access.getReadMethodName());     
    }
    
    /**
     * Retrieve a {@link Convertible} instance to customize the
     * value of database to class field.
     * @param column Column of row
     * @param proxy of return type from query
     * @return A convertible instance if found into class proxy or {@link NoConverterType}
     * instance when the field or method is not annotated.
     */
    public static <T> Convertible<Object, Object> toAttribute(PropertyAccess access, ObjectProxy<T> proxy)
    {
        return getConverter(proxy, access.getFieldName(), access.getWriterMethodName());
    }
    
    /**
     * Retrieve a {@link Convertible} instance to customize the
     * value of database to class field.
     * @param proxy of return type from query
     * @param fieldName attribute name from class
     * @param methodName getter or setter method
     * @return A convertible instance if found into class proxy or {@link NoConverterType}
     * instance when the field or method is not annotated.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static <T> Convertible<Object, Object> getConverter(
            ObjectProxy<T> proxy, String fieldName, String methodName)
    {
        // TODO cache for @Converter annotations
        Convertible convertible = NoConverterType.getInstance();
        
        if (Map.class.isAssignableFrom(proxy.getTargetClass()) || 
            Collection.class.isAssignableFrom(proxy.getTargetClass()) ||
            proxy.getTargetClass().isArray())
            return convertible;
        
        proxy.mute(NoSuchFieldException.class);
        proxy.mute(NoSuchMethodException.class);
        Converter converter = (Converter) proxy.getAnnotationMethod(Converter.class, methodName);
            
        if (converter == null)
            converter = (Converter) proxy.getAnnotationField(Converter.class, fieldName);
        
        if (converter != null)
        {
            ObjectProxy proxyConvertible = null;
            if(converter.converter().isEnum())
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
}
