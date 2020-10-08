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
package net.sf.jkniv.whinstone.jdbc.domain.flat;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import net.sf.jkniv.whinstone.types.BooleanCharType;
import net.sf.jkniv.whinstone.types.Converter;
import net.sf.jkniv.whinstone.types.DateIntType;
import net.sf.jkniv.whinstone.types.Converter.EnumType;

public class MyTypes
{
    private Long    id;
    private Short   mySmallint;
    private Integer myInteger;
    private Long    myBigint;
    private Float   myFloat;
    private Double  myDecimal;
    private String  myVarchar;
    private String  myChar;
    //MY_BLOB       BLOB,
    //MY_CLOB       CLOB,
    private Date    myDate;
    private Date    myTime;
    private Date    myTimestamp;
    @Converter(converter = BooleanCharType.class, pattern = "S|N")
    private Boolean myBoolChar;
    @Converter(converter = BooleanCharType.class, pattern = "S|N")
    private Boolean myBoolCharOverride;
    @Converter(converter = DateIntType.class, pattern = "yyyyMMdd")
    private Date myDateInt;

    @Converter(converter = TimeUnit.class, isEnum = EnumType.STRING)
    private TimeUnit timeUnit1;

    @Converter(converter = TimeUnit.class, isEnum = EnumType.ORDINAL)
    private TimeUnit timeUnit2;

    public Long getId()
    {
        return id;
    }
    
    public void setId(Long id)
    {
        this.id = id;
    }
    
    public Short getMySmallint()
    {
        return mySmallint;
    }
    
    public void setMySmallint(Short mySmallint)
    {
        this.mySmallint = mySmallint;
    }
    
    public Integer getMyInteger()
    {
        return myInteger;
    }
    
    public void setMyInteger(Integer myInteger)
    {
        this.myInteger = myInteger;
    }
    
    public Long getMyBigint()
    {
        return myBigint;
    }
    
    public void setMyBigint(Long myBigint)
    {
        this.myBigint = myBigint;
    }
    
    public Float getMyFloat()
    {
        return myFloat;
    }
    
    public void setMyFloat(Float myFloat)
    {
        this.myFloat = myFloat;
    }
    
    public Double getMyDecimal()
    {
        return myDecimal;
    }
    
    public void setMyDecimal(Double myDecimal)
    {
        this.myDecimal = myDecimal;
    }
    
    public String getMyVarchar()
    {
        return myVarchar;
    }
    
    public void setMyVarchar(String myVarchar)
    {
        this.myVarchar = myVarchar;
    }
    
    public String getMyChar()
    {
        return myChar;
    }
    
    public void setMyChar(String myChar)
    {
        this.myChar = myChar;
    }
    
    public Date getMyDate()
    {
        return myDate;
    }
    
    public void setMyDate(Date myDate)
    {
        this.myDate = myDate;
    }
    
    public Date getMyTime()
    {
        return myTime;
    }
    
    public void setMyTime(Date myTime)
    {
        this.myTime = myTime;
    }
    
    public Date getMyTimestamp()
    {
        return myTimestamp;
    }
    
    public void setMyTimestamp(Date myTimestamp)
    {
        this.myTimestamp = myTimestamp;
    }
    
    public Boolean getMyBoolChar()
    {
        return myBoolChar;
    }
    
    public void setMyBoolChar(Boolean myBoolChar)
    {
        this.myBoolChar = myBoolChar;
    }
    
    @Converter(converter = BooleanCharType.class, pattern = "YES|NO")
    public Boolean getMyBoolCharOverride()
    {
        return myBoolCharOverride;
    }
    
    @Converter(converter = BooleanCharType.class, pattern = "YES|NO")
    public void setMyBoolCharOverride(Boolean myBoolCharOverride)
    {
        this.myBoolCharOverride = myBoolCharOverride;
    }
    
    public Date getMyDateInt()
    {
        return myDateInt;
    }
    
    public void setMyDateInt(Date myDateInt)
    {
        this.myDateInt = myDateInt;
    }

    public TimeUnit getTimeUnit1()
    {
        return timeUnit1;
    }

    public void setTimeUnit1(TimeUnit timeUnit1)
    {
        this.timeUnit1 = timeUnit1;
    }

    public TimeUnit getTimeUnit2()
    {
        return timeUnit2;
    }

    public void setTimeUnit2(TimeUnit timeUnit2)
    {
        this.timeUnit2 = timeUnit2;
    }

}
