/* 
 * JKNIV,  utils - Helper utilities for jdk code.
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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.Currency;

import org.junit.Test;

public class BasicTypeTest
{

    @Test
    public void whenPrintTypes()
    {
        System.out.printf("%s - %s\n", Double.class.getName(), Double.class.getCanonicalName());
        System.out.printf("%s - %s\n", double.class.getName(), double.class.getCanonicalName());
        System.out.printf("%s - %s\n", Float.class.getName(), Float.class.getCanonicalName());
        System.out.printf("%s - %s\n", float.class.getName(), float.class.getCanonicalName());
        System.out.printf("%s - %s\n", Long.class.getName(), Long.class.getCanonicalName());
        System.out.printf("%s - %s\n", long.class.getName(), long.class.getCanonicalName());
        System.out.printf("%s - %s\n", Integer.class.getName(), Integer.class.getCanonicalName());
        System.out.printf("%s - %s\n", int.class.getName(), int.class.getCanonicalName());
        System.out.printf("%s - %s\n", Short.class.getName(), Short.class.getCanonicalName());
        System.out.printf("%s - %s\n", short.class.getName(), short.class.getCanonicalName());
        System.out.printf("%s - %s\n", Byte.class.getName(), Byte.class.getCanonicalName());
        System.out.printf("%s - %s\n", byte.class.getName(), byte.class.getCanonicalName());
        System.out.printf("%s - %s\n", BigDecimal.class.getName(), BigDecimal.class.getCanonicalName());
        System.out.printf("%s - %s\n", BigInteger.class.getName(), BigInteger.class.getCanonicalName());

        System.out.printf("%s - %s\n", Boolean.class.getName(), Boolean.class.getCanonicalName());
        System.out.printf("%s - %s\n", boolean.class.getName(), boolean.class.getCanonicalName());
        System.out.printf("%s - %s\n", Character.class.getName(), Character.class.getCanonicalName());
        System.out.printf("%s - %s\n", char.class.getName(), char.class.getCanonicalName());
    }


    @Test
    public void whenCheckNumberTypes()
    {
        BasicType types = new BasicType();
        assertThat(types.isNumberType(Double.class), is(true));
        assertThat(types.isNumberType(double.class), is(true));
        assertThat(types.isNumberType(Float.class), is(true));
        assertThat(types.isNumberType(float.class), is(true));
        assertThat(types.isNumberType(Long.class), is(true));
        assertThat(types.isNumberType(long.class), is(true));
        assertThat(types.isNumberType(Integer.class), is(true));
        assertThat(types.isNumberType(int.class), is(true));
        assertThat(types.isNumberType(Short.class), is(true));
        assertThat(types.isNumberType(short.class), is(true));
        assertThat(types.isNumberType(Byte.class), is(true));
        assertThat(types.isNumberType(byte.class), is(true));
        assertThat(types.isNumberType(BigDecimal.class), is(true));
        assertThat(types.isNumberType(BigInteger.class), is(true));
        assertThat(types.isNumberType(Boolean.class), is(false));
        assertThat(types.isNumberType(boolean.class), is(false));
        assertThat(types.isNumberType(Character.class), is(false));
        assertThat(types.isNumberType(char.class), is(false));
        

    }
    
    @Test
    public void whenCheckBasicTypes()
    {
        BasicType types = new BasicType();
        assertThat(types.isBasicType(Double.class), is(true));
        assertThat(types.isBasicType(double.class), is(true));
        assertThat(types.isBasicType(Float.class), is(true));
        assertThat(types.isBasicType(float.class), is(true));
        assertThat(types.isBasicType(Long.class), is(true));
        assertThat(types.isBasicType(long.class), is(true));
        assertThat(types.isBasicType(Integer.class), is(true));
        assertThat(types.isBasicType(int.class), is(true));
        assertThat(types.isBasicType(Short.class), is(true));
        assertThat(types.isBasicType(short.class), is(true));
        assertThat(types.isBasicType(Byte.class), is(true));
        assertThat(types.isBasicType(byte.class), is(true));
        assertThat(types.isBasicType(BigDecimal.class), is(true));
        assertThat(types.isBasicType(BigInteger.class), is(true));
        assertThat(types.isBasicType(Boolean.class), is(true));
        assertThat(types.isBasicType(boolean.class), is(true));
        assertThat(types.isBasicType(Character.class), is(true));
        assertThat(types.isBasicType(char.class), is(true));
        assertThat(types.isBasicType(URL.class), is(true));
        assertThat(types.isBasicType(Currency.class), is(true));
        
    }
/*
    @Test
    public void whenCheckNumberTypesWrapped()
    {
        BasicType types = new BasicType();
        assertThat(types.isNumberTypeWrapped(Double.class), is(true));
        assertThat(types.isNumberTypeWrapped(double.class), is(false));
        assertThat(types.isNumberTypeWrapped(Float.class), is(true));
        assertThat(types.isNumberTypeWrapped(float.class), is(false));
        assertThat(types.isNumberTypeWrapped(Long.class), is(true));
        assertThat(types.isNumberTypeWrapped(long.class), is(false));
        assertThat(types.isNumberTypeWrapped(Integer.class), is(true));
        assertThat(types.isNumberTypeWrapped(int.class), is(false));
        assertThat(types.isNumberTypeWrapped(Short.class), is(true));
        assertThat(types.isNumberTypeWrapped(short.class), is(false));
        assertThat(types.isNumberTypeWrapped(Byte.class), is(true));
        assertThat(types.isNumberTypeWrapped(byte.class), is(false));
        assertThat(types.isNumberTypeWrapped(BigDecimal.class), is(true));
        assertThat(types.isNumberTypeWrapped(BigInteger.class), is(true));

        assertThat(types.isNumberTypeWrapped(Boolean.class), is(false));
        assertThat(types.isNumberTypeWrapped(boolean.class), is(false));
        assertThat(types.isNumberTypeWrapped(Character.class), is(false));
        assertThat(types.isNumberTypeWrapped(char.class), is(false));
    }
*/
}
