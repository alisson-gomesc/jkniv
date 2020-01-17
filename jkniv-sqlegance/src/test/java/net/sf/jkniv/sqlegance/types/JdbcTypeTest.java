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
package net.sf.jkniv.sqlegance.types;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.junit.Ignore;
import org.junit.Test;

public class JdbcTypeTest
{
    final static String PI        = "3.141592653589793115997963468544185161590576171875";
    final static String PI_FLOAT  = "3.1415927410125732";// WRONG VALUE FOR PI conversion between Float and Double lost precision
    
    @Test
    public void whenTrueIsOneAndFalseIsZero()  
    {
        BooleanCharType type = new BooleanCharType("1|0");
        
        assertThat(type.toAttribute("0"), is(Boolean.FALSE));
        assertThat(type.toAttribute("1"), is(Boolean.TRUE));
        assertThat(type.toAttribute("2"), is(Boolean.FALSE));
        assertThat(type.toAttribute("A"), is(Boolean.FALSE));

        assertThat(type.toJdbc(Boolean.FALSE), is("0"));
        assertThat(type.toJdbc(Boolean.TRUE), is("1"));
        assertThat(type.toJdbc(true), is("1"));
        assertThat(type.toJdbc(false), is("0"));

        assertThat(Boolean.TRUE, instanceOf(type.getType()));
        assertThat(type.getColumnType(), instanceOf(JdbcType.CHAR.getClass()));
    }
    
    @Test
    public void whenBooleanIsSaveAsBIT()  
    {
        BooleanBitType type = new BooleanBitType("1|0");
        assertThat(type.toAttribute(0), is(Boolean.FALSE));
        assertThat(type.toAttribute(1), is(Boolean.TRUE));
        assertThat(type.toAttribute(2), is(Boolean.FALSE));
        assertThat(type.toJdbc(Boolean.TRUE), is(1));
        assertThat(type.toJdbc(Boolean.FALSE), is(0));
        
        BooleanBitType type2 = new BooleanBitType("0|1");
        assertThat(type2.toAttribute(0), is(Boolean.TRUE));
        assertThat(type2.toAttribute(1), is(Boolean.FALSE));
        assertThat(type2.toAttribute(2), is(Boolean.FALSE));
        assertThat(type2.toJdbc(Boolean.TRUE), is(0));
        assertThat(type2.toJdbc(Boolean.FALSE), is(1));
        
        assertThat(Boolean.TRUE, instanceOf(type.getType()));
        assertThat(type.getColumnType(), instanceOf(JdbcType.BIT.getClass()));
    }
    
    @Test
    public void whenBooleanIsSaveAsInt()  
    {
        BooleanIntType type = new BooleanIntType("1|0");
        assertThat(type.toAttribute(0), is(Boolean.FALSE));
        assertThat(type.toAttribute(1), is(Boolean.TRUE));
        assertThat(type.toAttribute(2), is(Boolean.FALSE));
        assertThat(type.toJdbc(Boolean.TRUE), is(1));
        assertThat(type.toJdbc(Boolean.FALSE), is(0));
        
        BooleanIntType type2 = new BooleanIntType("0|1");
        assertThat(type2.toAttribute(0), is(Boolean.TRUE));
        assertThat(type2.toAttribute(1), is(Boolean.FALSE));
        assertThat(type2.toAttribute(2), is(Boolean.FALSE));
        assertThat(type2.toJdbc(Boolean.TRUE), is(0));
        assertThat(type2.toJdbc(Boolean.FALSE), is(1));
        
        assertThat(Boolean.TRUE, instanceOf(type.getType()));
        assertThat(type.getColumnType(), instanceOf(JdbcType.INTEGER.getClass()));
    }
    
    @Test
    public void whenBooleanIsSaveAsChar()  
    {
        BooleanCharType type = new BooleanCharType("1|0");
        assertThat(type.toAttribute("0"), is(Boolean.FALSE));
        assertThat(type.toAttribute("1"), is(Boolean.TRUE));
        assertThat(type.toAttribute("2"), is(Boolean.FALSE));
        assertThat(type.toJdbc(Boolean.TRUE), is("1"));
        assertThat(type.toJdbc(Boolean.FALSE), is("0"));

        BooleanCharType type2 = new BooleanCharType("0|1");
        assertThat(type2.toAttribute("0"), is(Boolean.TRUE));
        assertThat(type2.toAttribute("1"), is(Boolean.FALSE));
        assertThat(type2.toAttribute("2"), is(Boolean.FALSE));
        assertThat(type2.toJdbc(Boolean.TRUE), is("0"));
        assertThat(type2.toJdbc(Boolean.FALSE), is("1"));

        BooleanCharType type3 = new BooleanCharType("a|b");
        assertThat(type3.toAttribute("a"), is(Boolean.TRUE));
        assertThat(type3.toAttribute("b"), is(Boolean.FALSE));
        assertThat(type3.toAttribute("c"), is(Boolean.FALSE));
        assertThat(type3.toJdbc(Boolean.TRUE), is("a"));
        assertThat(type3.toJdbc(Boolean.FALSE), is("b"));
        
        assertThat(Boolean.TRUE, instanceOf(type.getType()));
        assertThat(type.getColumnType(), instanceOf(JdbcType.CHAR.getClass()));
    }
    
    @Test
    public void whenBooleanIsSaveAsVarchar()  
    {
        BooleanVarcharType type = new BooleanVarcharType("1|0");
        assertThat(type.toAttribute("0"), is(Boolean.FALSE));
        assertThat(type.toAttribute("1"), is(Boolean.TRUE));
        assertThat(type.toAttribute("2"), is(Boolean.FALSE));
        assertThat(type.toJdbc(Boolean.TRUE), is("1"));
        assertThat(type.toJdbc(Boolean.FALSE), is("0"));

        BooleanVarcharType type2 = new BooleanVarcharType("0|1");
        assertThat(type2.toAttribute("0"), is(Boolean.TRUE));
        assertThat(type2.toAttribute("1"), is(Boolean.FALSE));
        assertThat(type2.toAttribute("2"), is(Boolean.FALSE));
        assertThat(type2.toJdbc(Boolean.TRUE), is("0"));
        assertThat(type2.toJdbc(Boolean.FALSE), is("1"));

        BooleanVarcharType type3 = new BooleanVarcharType("a|b");
        assertThat(type3.toAttribute("a"), is(Boolean.TRUE));
        assertThat(type3.toAttribute("b"), is(Boolean.FALSE));
        assertThat(type3.toAttribute("c"), is(Boolean.FALSE));
        assertThat(type3.toJdbc(Boolean.TRUE), is("a"));
        assertThat(type3.toJdbc(Boolean.FALSE), is("b"));
        
        assertThat(Boolean.TRUE, instanceOf(type.getType()));
        assertThat(type.getColumnType(), instanceOf(JdbcType.VARCHAR.getClass()));
    }
    
    @Test
    public void whenCalendarIsSaveAsInt()  
    {
        CalendarIntType type = new CalendarIntType("yyyyMMdd");
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(new Date(2019-1900, 0, 1));
        c2.setTime(new Date(2019-1900, 1, 10));
        
        assertThat(type.toAttribute(20190101), is(c1));
        assertThat(type.toAttribute(20190210), is(c2));

        assertThat(type.toJdbc(c1), is(20190101));
        assertThat(type.toJdbc(c2), is(20190210));
        
        assertThat(c1, instanceOf(type.getType()));
        assertThat(type.getColumnType(), instanceOf(JdbcType.INTEGER.getClass()));
    }
    
    @Test
    public void whenCalendarIsSaveAsTimestamp()  
    {
        CalendarTimestampType type = new CalendarTimestampType();
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(new Date(2019-1900, 0, 1));
        c2.setTime(new Date(2019-1900, 1, 10));
        Timestamp t1 = new Timestamp(c1.getTime().getTime());
        Timestamp t2 = new Timestamp(c2.getTime().getTime());
        
        assertThat(type.toAttribute(t1), is(c1));
        assertThat(type.toAttribute(t2), is(c2));

        assertThat(type.toJdbc(c1), is(t1));
        assertThat(type.toJdbc(c2), is(t2));
        
        assertThat(c1, instanceOf(type.getType()));
        assertThat(type.getColumnType(), instanceOf(JdbcType.TIMESTAMP.getClass()));
    }
    
    @Test
    public void whenDateIsSaveAsInt()  
    {
        DateIntType type = new DateIntType("yyyyMMdd");
        Date d1 = new Date(2019-1900, 0, 1);
        Date d2 = new Date(2019-1900, 1, 10);
        
        assertThat(type.toAttribute(20190101), is(d1));
        assertThat(type.toAttribute(20190210), is(d2));

        assertThat(type.toJdbc(d1), is(20190101));
        assertThat(type.toJdbc(d2), is(20190210));

        assertThat(d1, instanceOf(type.getType()));
        assertThat(type.getColumnType(), instanceOf(JdbcType.INTEGER.getClass()));
    }

    @Test
    public void whenDateIsSaveAsTimestamp()  
    {
        DateTimestampType type = new DateTimestampType();
        Date d1 = new Date(2019-1900, 0, 1);
        Date d2 = new Date(2019-1900, 1, 10);
        Timestamp t1 = new Timestamp(d1.getTime());
        Timestamp t2 = new Timestamp(d2.getTime());
        
        assertThat(type.toAttribute(t1), is(d1));
        assertThat(type.toAttribute(t2), is(d2));

        assertThat(type.toJdbc(d1), is(t1));
        assertThat(type.toJdbc(d2), is(t2));

        assertThat(d1, instanceOf(type.getType()));
        assertThat(type.getColumnType(), instanceOf(JdbcType.TIMESTAMP.getClass()));
    }

    @Test
    public void whenDateIsSaveAsTime()  
    {
        DateTimeType type = new DateTimeType();
        Date d1 = new Date(2019-1900, 0, 1);
        Date d2 = new Date(2019-1900, 1, 10);
        Time t1 = new Time(d1.getTime());
        Time t2 = new Time(d2.getTime());
        
        assertThat(type.toAttribute(t1), is(d1));
        assertThat(type.toAttribute(t2), is(d2));

        assertThat(type.toJdbc(d1), is(t1));
        assertThat(type.toJdbc(d2), is(t2));

        assertThat(d1, instanceOf(type.getType()));
        assertThat(type.getColumnType(), instanceOf(JdbcType.TIME.getClass()));
    }

    @Test
    public void whenDateIsSaveAsDate()  
    {
        DateType type = new DateType();
        Date d1 = new Date(2019-1900, 0, 1);
        Date d2 = new Date(2019-1900, 1, 10);
        java.sql.Date t1 = new java.sql.Date (d1.getTime());
        java.sql.Date t2 = new java.sql.Date (d2.getTime());
        
        assertThat(type.toAttribute(t1), is(d1));
        assertThat(type.toAttribute(t2), is(d2));

        assertThat(type.toJdbc(d1), is(t1));
        assertThat(type.toJdbc(d2), is(t2));

        assertThat(d1, instanceOf(type.getType()));
        assertThat(type.getColumnType(), instanceOf(JdbcType.DATE.getClass()));
    }

    
    @Test
    public void whenDoubleIsSaveAsBigDecimal()  
    {
        DoubleBigDecimalType type = new DoubleBigDecimalType();
        
        assertThat(type.toAttribute(BigDecimal.ZERO), is(0D));
        assertThat(type.toAttribute(BigDecimal.ONE), is(1D));
        assertThat(type.toAttribute(BigDecimal.TEN), is(10D));
        assertThat(type.toAttribute(new BigDecimal(PI)), is(Math.PI));

        assertThat(type.toJdbc(0D), is(BigDecimal.ZERO));
        assertThat(type.toJdbc(1D), is(BigDecimal.ONE));
        assertThat(type.toJdbc(10D), is(BigDecimal.TEN));
        assertThat(type.toJdbc(Math.PI), is(new BigDecimal(PI)));

        assertThat(Double.MIN_VALUE, instanceOf(type.getType()));
        assertThat(type.getColumnType(), instanceOf(JdbcType.DECIMAL.getClass()));
    }

    @Test
    public void whenEnumIsSaveAsName()  
    {
        EnumNameType type = new EnumNameType(TimeUnit.class);
        assertThat((TimeUnit)type.toAttribute(TimeUnit.HOURS.name()), is(TimeUnit.HOURS));
        assertThat(type.toJdbc(TimeUnit.MINUTES), is(TimeUnit.MINUTES.name()));
        assertThat(TimeUnit.MICROSECONDS, instanceOf(type.getType()));
        assertThat(type.getColumnType(), instanceOf(JdbcType.VARCHAR.getClass()));
    }

    @Test
    public void whenEnumIsSaveAsOrdinal()  
    {
        EnumOrdinalType type = new EnumOrdinalType(TimeUnit.class);
        assertThat((TimeUnit)type.toAttribute(TimeUnit.HOURS.ordinal()), is(TimeUnit.HOURS));
        assertThat(type.toJdbc(TimeUnit.MINUTES), is(TimeUnit.MINUTES.ordinal()));
        assertThat(TimeUnit.MICROSECONDS, instanceOf(type.getType()));
        assertThat(type.getColumnType(), instanceOf(JdbcType.INTEGER.getClass()));
    }

    @Test @Ignore
    public void whenFloatIsSaveAsDouble()  
    {
        FloatDoubleType type = new FloatDoubleType();
        assertThat(type.toAttribute(Math.PI), is(Float.valueOf(PI_FLOAT)));
        assertThat(type.toJdbc(Float.valueOf(PI)), is(Math.PI));
        assertThat(Float.MIN_VALUE, instanceOf(type.getType()));
        assertThat(type.getColumnType(), instanceOf(JdbcType.DOUBLE.getClass()));
    }

}
