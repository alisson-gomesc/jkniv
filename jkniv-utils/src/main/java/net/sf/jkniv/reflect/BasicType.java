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
import java.net.URL;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.swing.JApplet;

import net.sf.jkniv.asserts.Assertable;
import net.sf.jkniv.asserts.AssertsFactory;
import sun.util.calendar.JulianCalendar;

@SuppressWarnings("rawtypes")
public class BasicType
{
    private static final Assertable         notNull      = AssertsFactory.getNotNull();
    private static final BasicType          instance     = new BasicType();
    private static final Map<String, Class> BASIC_TYPES  = new HashMap<String, Class>();
    private static final Map<String, Class> NUMBER_TYPES = new HashMap<String, Class>();
    
    static
    {
        BASIC_TYPES.put(String.class.getCanonicalName(), String.class);
        BASIC_TYPES.put(Integer.class.getCanonicalName(), Integer.class);
        BASIC_TYPES.put(int.class.getCanonicalName(), int.class);
        BASIC_TYPES.put(Long.class.getCanonicalName(), Long.class);
        BASIC_TYPES.put(long.class.getCanonicalName(), long.class);
        BASIC_TYPES.put(Double.class.getCanonicalName(), Double.class);
        BASIC_TYPES.put(double.class.getCanonicalName(), double.class);
        BASIC_TYPES.put(Float.class.getCanonicalName(), Float.class);
        BASIC_TYPES.put(float.class.getCanonicalName(), float.class);
        BASIC_TYPES.put(Boolean.class.getCanonicalName(), Boolean.class);
        BASIC_TYPES.put(boolean.class.getCanonicalName(), boolean.class);
        BASIC_TYPES.put(Date.class.getCanonicalName(), Date.class);
        BASIC_TYPES.put(Calendar.class.getCanonicalName(), Calendar.class);
        BASIC_TYPES.put(GregorianCalendar.class.getCanonicalName(), GregorianCalendar.class);
        BASIC_TYPES.put(JulianCalendar.class.getCanonicalName(), JulianCalendar.class);
        BASIC_TYPES.put(BigDecimal.class.getCanonicalName(), BigDecimal.class);
        BASIC_TYPES.put(Short.class.getCanonicalName(), Short.class);
        BASIC_TYPES.put(short.class.getCanonicalName(), short.class);
        BASIC_TYPES.put(Byte.class.getCanonicalName(), Byte.class);
        BASIC_TYPES.put(byte.class.getCanonicalName(), byte.class);
        BASIC_TYPES.put(Character.class.getCanonicalName(), Character.class);
        BASIC_TYPES.put(char.class.getCanonicalName(), char.class);
        BASIC_TYPES.put(BigInteger.class.getCanonicalName(), BigInteger.class);
        BASIC_TYPES.put(AtomicInteger.class.getCanonicalName(), AtomicInteger.class);
        BASIC_TYPES.put(AtomicLong.class.getCanonicalName(), AtomicLong.class);
        
        BASIC_TYPES.put(URL.class.getCanonicalName(), URL.class);
        BASIC_TYPES.put(Currency.class.getCanonicalName(), Currency.class);
        
        // new java 8 number types
        BASIC_TYPES.put("java.util.concurrent.atomic.DoubleAccumulator", FakeJdk8Type.class);
        BASIC_TYPES.put("java.util.concurrent.atomic.DoubleAdder", FakeJdk8Type.class);
        BASIC_TYPES.put("java.util.concurrent.atomic.LongAccumulator", FakeJdk8Type.class);
        BASIC_TYPES.put("java.util.concurrent.atomic.LongAdder", FakeJdk8Type.class);
        BASIC_TYPES.put("java.time.Duration", FakeJdk8Type.class);
        BASIC_TYPES.put("java.time.Instant", FakeJdk8Type.class);
        BASIC_TYPES.put("java.time.LocalDate", FakeJdk8Type.class);
        BASIC_TYPES.put("java.time.LocalDateTime", FakeJdk8Type.class);
        BASIC_TYPES.put("java.time.LocalTime", FakeJdk8Type.class);
        BASIC_TYPES.put("java.time.ZonedDateTime", FakeJdk8Type.class);
        BASIC_TYPES.put("java.time.ZonaId", FakeJdk8Type.class);
        BASIC_TYPES.put("java.time.OffsetDateTime", FakeJdk8Type.class);
        BASIC_TYPES.put("java.time.OffsetTime", FakeJdk8Type.class);
        
        NUMBER_TYPES.put(Integer.class.getCanonicalName(), Integer.class);
        NUMBER_TYPES.put(int.class.getCanonicalName(), int.class);
        NUMBER_TYPES.put(Long.class.getCanonicalName(), Long.class);
        NUMBER_TYPES.put(long.class.getCanonicalName(), long.class);
        NUMBER_TYPES.put(Double.class.getCanonicalName(), Double.class);
        NUMBER_TYPES.put(double.class.getCanonicalName(), double.class);
        NUMBER_TYPES.put(Float.class.getCanonicalName(), Float.class);
        NUMBER_TYPES.put(float.class.getCanonicalName(), float.class);
        NUMBER_TYPES.put(BigDecimal.class.getCanonicalName(), BigDecimal.class);
        NUMBER_TYPES.put(Short.class.getCanonicalName(), Short.class);
        NUMBER_TYPES.put(short.class.getCanonicalName(), short.class);
        NUMBER_TYPES.put(Byte.class.getCanonicalName(), Byte.class);
        NUMBER_TYPES.put(byte.class.getCanonicalName(), byte.class);
        NUMBER_TYPES.put(BigInteger.class.getCanonicalName(), BigInteger.class);
        NUMBER_TYPES.put(AtomicInteger.class.getCanonicalName(), AtomicInteger.class);
        NUMBER_TYPES.put(AtomicLong.class.getCanonicalName(), AtomicLong.class);
        NUMBER_TYPES.put("java.util.concurrent.atomic.DoubleAccumulator", FakeJdk8Type.class);
        NUMBER_TYPES.put("java.util.concurrent.atomic.DoubleAdder", FakeJdk8Type.class);
        NUMBER_TYPES.put("java.util.concurrent.atomic.LongAccumulator", FakeJdk8Type.class);
        NUMBER_TYPES.put("java.util.concurrent.atomic.LongAdder", FakeJdk8Type.class);
        
    }
    
    public static BasicType getInstance()
    {
        return instance;
    }
    
    public boolean isNumberType(Class<?> type)
    {
        notNull.verify(type);
        return NUMBER_TYPES.containsKey(type.getCanonicalName());
    }
    
    public boolean isBasicType(Class<?> type)
    {
        notNull.verify(type);
        boolean answer = false;
        Class clazz = BASIC_TYPES.get(type.getCanonicalName());
        if (clazz == null)
            answer = false;
        else if (clazz.getCanonicalName().equals(FakeJdk8Type.class.getName()))
            answer = true;
        else
            answer = clazz.isAssignableFrom(type);
        return answer;
    }
    
    public class FakeJdk8Type
    {
        String className;
        public FakeJdk8Type(String className)
        {
            this.className = className;
        }
        
        public boolean isAssignableFrom()
        {
            return true;
        }
    }
}
