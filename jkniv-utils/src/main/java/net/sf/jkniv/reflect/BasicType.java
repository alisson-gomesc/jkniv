/* 
 * JKNIV, utils - Helper utilities for jdk code.
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
package net.sf.jkniv.reflect;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import net.sf.jkniv.asserts.Assertable;
import net.sf.jkniv.asserts.AssertsFactory;

public class BasicType
{
    private static final Assertable notNull = AssertsFactory.getNotNull();
    private static final BasicType instance = new BasicType();

    
    public static BasicType getInstance()
    {
        return instance;
    }
    
    /*
     TODO thinking about, primitive types needs overload
    public boolean isNumber(Object value)
    {
        notNull.verify(value);
        return isNumberType(value.getClass());
    }
    */
    
    public boolean isNumberType(Class<?> type)
    {
        notNull.verify(type);
        
        boolean isNumber = false;
        
        if (Integer.class.getCanonicalName().equals(type.getCanonicalName()) || int.class.getCanonicalName().equals(type.getCanonicalName()))
            isNumber = true;
        else if (Long.class.getCanonicalName().equals(type.getCanonicalName())  || long.class.getCanonicalName().equals(type.getCanonicalName()))
            isNumber = true;
        else if (Double.class.getCanonicalName().equals(type.getCanonicalName()) || double.class.getCanonicalName().equals(type.getCanonicalName()))
            isNumber = true;
        else if (Float.class.getCanonicalName().equals(type.getCanonicalName()) || float.class.getCanonicalName().equals(type.getCanonicalName()))
            isNumber = true;        
        else if (BigDecimal.class.getCanonicalName().equals(type.getCanonicalName()))
            isNumber = true;
        else if (Short.class.getCanonicalName().equals(type.getCanonicalName()) || short.class.getCanonicalName().equals(type.getCanonicalName()))
            isNumber = true;
        else if (Byte.class.getCanonicalName().equals(type.getCanonicalName()) || byte.class.getCanonicalName().equals(type.getCanonicalName()))
            isNumber = true;
        else if (BigInteger.class.getCanonicalName().equals(type.getCanonicalName()))
            isNumber = true;
        else if (AtomicInteger.class.getCanonicalName().equals(type.getCanonicalName()))
            isNumber = true;
        else if (AtomicLong.class.getCanonicalName().equals(type.getCanonicalName()))
            isNumber = true;
        // new java 8 number types
        else if ("java.util.concurrent.atomic.DoubleAccumulator".equals(type.getCanonicalName()))
            isNumber = true;
        else if ("java.util.concurrent.atomic.DoubleAdder".equals(type.getCanonicalName()))
            isNumber = true;
        else if ("java.util.concurrent.atomic.LongAccumulator".equals(type.getCanonicalName()))
            isNumber = true;
        else if ("java.util.concurrent.atomic.LongAdder".equals(type.getCanonicalName()))
            isNumber = true;
        return isNumber;
    }

    public boolean isNumberTypeWrapped(Class<?> type)
    {
        notNull.verify(type);
        
        boolean isNumber = false;
        
        if (Integer.class.getCanonicalName().equals(type.getCanonicalName()))
            isNumber = true;
        else if (Long.class.getCanonicalName().equals(type.getCanonicalName()))
            isNumber = true;
        else if (Double.class.getCanonicalName().equals(type.getCanonicalName()))
            isNumber = true;
        else if (Float.class.getCanonicalName().equals(type.getCanonicalName()))
            isNumber = true;        
        else if (BigDecimal.class.getCanonicalName().equals(type.getCanonicalName()))
            isNumber = true;
        else if (Short.class.getCanonicalName().equals(type.getCanonicalName()))
            isNumber = true;
        else if (Byte.class.getCanonicalName().equals(type.getCanonicalName()))
            isNumber = true;
        else if (BigInteger.class.getCanonicalName().equals(type.getCanonicalName()))
            isNumber = true;
        else if (AtomicInteger.class.getCanonicalName().equals(type.getCanonicalName()))
            isNumber = true;
        else if (AtomicLong.class.getCanonicalName().equals(type.getCanonicalName()))
            isNumber = true;
        // new java 8 number types
        else if ("java.util.concurrent.atomic.DoubleAccumulator".equals(type.getCanonicalName()))
            isNumber = true;
        else if ("java.util.concurrent.atomic.DoubleAdder".equals(type.getCanonicalName()))
            isNumber = true;
        else if ("java.util.concurrent.atomic.LongAccumulator".equals(type.getCanonicalName()))
            isNumber = true;
        else if ("java.util.concurrent.atomic.LongAdder".equals(type.getCanonicalName()))
            isNumber = true;
        
        return isNumber;
    }
    

    public boolean isBasicType(Class<?> type)// TODO test me
    {
        notNull.verify(type);
        
        boolean isBasic = false;
        
        if(String.class.getCanonicalName().equals(type.getCanonicalName()))
            isBasic = true;
        else if (Integer.class.getCanonicalName().equals(type.getCanonicalName()) || int.class.getCanonicalName().equals(type.getCanonicalName()))
            isBasic = true;
        else if (Long.class.getCanonicalName().equals(type.getCanonicalName()) || long.class.getCanonicalName().equals(type.getCanonicalName()))
            isBasic = true;
        else if (Double.class.getCanonicalName().equals(type.getCanonicalName()) || double.class.getCanonicalName().equals(type.getCanonicalName()))
            isBasic = true;
        else if (Float.class.getCanonicalName().equals(type.getCanonicalName()) || float.class.getCanonicalName().equals(type.getCanonicalName()))
            isBasic = true;        
        else if (Boolean.class.getCanonicalName().equals(type.getCanonicalName()) || boolean.class.getCanonicalName().equals(type.getCanonicalName()))
            isBasic = true;
        else if(Date.class.isAssignableFrom(type))
            isBasic = true;
        else if(Calendar.class.isAssignableFrom(type))
            isBasic = true;
        else if (BigDecimal.class.getCanonicalName().equals(type.getCanonicalName()))
            isBasic = true;
        else if (Short.class.getCanonicalName().equals(type.getCanonicalName()) || short.class.getCanonicalName().equals(type.getCanonicalName()))
            isBasic = true;
        else if (Byte.class.getCanonicalName().equals(type.getCanonicalName()) || byte.class.getCanonicalName().equals(type.getCanonicalName()))
            isBasic = true;
        else if (Character.class.getCanonicalName().equals(type.getCanonicalName()) || char.class.getCanonicalName().equals(type.getCanonicalName()))
            isBasic = true;
        else if (BigInteger.class.getCanonicalName().equals(type.getCanonicalName()))
            isBasic = true;
        else if (AtomicInteger.class.getCanonicalName().equals(type.getCanonicalName()))
            isBasic = true;
        else if (AtomicLong.class.getCanonicalName().equals(type.getCanonicalName()))
            isBasic = true;
        // new java 8 number types
        else if ("java.util.concurrent.atomic.DoubleAccumulator".equals(type.getCanonicalName()))
            isBasic = true;
        else if ("java.util.concurrent.atomic.DoubleAdder".equals(type.getCanonicalName()))
            isBasic = true;
        else if ("java.util.concurrent.atomic.LongAccumulator".equals(type.getCanonicalName()))
            isBasic = true;
        else if ("java.util.concurrent.atomic.LongAdder".equals(type.getCanonicalName()))
            isBasic = true;
        
        return isBasic;
    }

}
