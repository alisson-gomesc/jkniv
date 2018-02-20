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
package net.sf.jkniv.reflect.beans;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

public class ObjectProxyForPrimitiveAndWrapperTypesTest
{
    
    @Test
    public void whenNameOfTYPEandClass()
    {
        System.out.println(Double.class.getCanonicalName());
        System.out.println(Double.TYPE.getCanonicalName());
        System.out.println(Double.class.getName());
        System.out.println(Double.TYPE.getName());

        assertThat(Double.TYPE.getName(), is("double"));
        assertThat(Double.TYPE.getCanonicalName(), is("double"));
        assertThat(Double.class.getName(), is("java.lang.Double"));
        assertThat(Double.class.getCanonicalName(), is("java.lang.Double"));
    }

    @Test
    public void whenHaveBooleanValue()
    {
        ObjectProxy<Boolean> p = new DefaultObjectProxy<Boolean>(Boolean.class);
        p.invoke("valueOf", true);
        assertThat(p.getInstance(), is(true));
        
    }
    
    @Test
    public void whenHaveBooleanNull()
    {
        ObjectProxy<Boolean> p = new DefaultObjectProxy<Boolean>(Boolean.class);
        p.invoke("valueOf", null);
        assertThat(p.getInstance(), is(nullValue()));
    }

    @Test
    public void whenHaveShortValue()
    {
        ObjectProxy<Short> p = new DefaultObjectProxy<Short>(Short.class);
        p.invoke("valueOf", Short.valueOf("10"));
        assertThat(p.getInstance(), is(Short.valueOf("10")));
    }
    
    @Test
    public void whenHaveShortNull()
    {
        ObjectProxy<Short> p = new DefaultObjectProxy<Short>(Short.class);
        p.invoke("valueOf", null);
        assertThat(p.getInstance(), is(nullValue()));
    }
    
    @Test
    public void whenHaveLongValue()
    {
        ObjectProxy<Long> p = new DefaultObjectProxy<Long>(Long.class);
        p.invoke("valueOf", 10L);
        assertThat(p.getInstance(), is(10L));
    }
    
    @Test
    public void whenHaveLongNull()
    {
        ObjectProxy<Long> p = new DefaultObjectProxy<Long>(Long.class);
        p.invoke("valueOf", null);
        assertThat(p.getInstance(), is(nullValue()));
    }

    @Test
    public void whenHaveFloatValue()
    {
        ObjectProxy<Float> p = new DefaultObjectProxy<Float>(Float.class);
        p.invoke("valueOf", 10F);
        assertThat(p.getInstance(), is(10F));
    }
    
    @Test
    public void whenHaveFloatNull()
    {
        ObjectProxy<Float> p = new DefaultObjectProxy<Float>(Float.class);
        p.invoke("valueOf", null);
        assertThat(p.getInstance(), is(nullValue()));
    }

    @Test
    public void whenHaveIntegerValue()
    {
        ObjectProxy<Integer> p = new DefaultObjectProxy<Integer>(Integer.class);
        p.invoke("valueOf", 10);
        assertThat(p.getInstance(), is(10));
    }
    
    @Test
    public void whenHaveIntegerNull()
    {
        ObjectProxy<Integer> p = new DefaultObjectProxy<Integer>(Integer.class);
        p.invoke("valueOf", null);
        assertThat(p.getInstance(), is(nullValue()));
    }

    @Test
    public void whenHaveDoubleValue()
    {
        ObjectProxy<Double> p = new DefaultObjectProxy<Double>(Double.class);
        p.invoke("valueOf", 10D);
        assertThat(p.getInstance(), is(10D));
    }
    
    @Test
    public void whenHaveDoubleNull()
    {
        ObjectProxy<Double> p = new DefaultObjectProxy<Double>(Double.class);
        p.invoke("valueOf", null);
        assertThat(p.getInstance(), is(nullValue()));
    }
    
    @Test
    public void whenHaveStringValue()
    {
        ObjectProxy<String> p = new DefaultObjectProxy<String>(String.class);
        p.invoke("valueOf", "A");
        assertThat(p.getInstance(), is("A"));
    }
    
    @Test
    public void whenHaveStringNull()
    {
        ObjectProxy<String> p = new DefaultObjectProxy<String>(String.class);
        p.invoke("valueOf", null);
        assertThat(p.getInstance(), is(nullValue()));
    }
}
