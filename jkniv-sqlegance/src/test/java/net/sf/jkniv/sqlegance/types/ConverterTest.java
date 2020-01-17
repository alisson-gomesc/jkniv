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

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

import net.sf.jkniv.reflect.beans.ObjectProxy;
import net.sf.jkniv.reflect.beans.ObjectProxyFactory;
import net.sf.jkniv.sqlegance.types.Converter;

public class ConverterTest
{

    @Test 
    public void whenCheckIfAnnotationIsPresent()
    {
        ObjectProxy<MixModelType> proxy = ObjectProxyFactory.of(MixModelType.class);
        Converter converter = proxy.getAnnotationMethod(Converter.class, "getActive");
        assertThat(converter, notNullValue());
        //assertThat(converter.allowNull(), is(true));
        assertThat(converter.pattern(), is("1"));
        assertThat(converter.converter().getName(), is(BooleanCharType.class.getName()));

        converter = proxy.getAnnotationMethod(Converter.class, "getCreatedAt");
        assertThat(converter, notNullValue());
        //assertThat(converter.allowNull(), is(true));
        assertThat(converter.pattern(), is("yyyyMMdd"));

        converter = proxy.getAnnotationField(Converter.class, "timestamp");
        assertThat(converter, notNullValue());
        //assertThat(converter.allowNull(), is(true));
        assertThat(converter.pattern(), is("yyyyMMddHHmmss.MMM"));
    
    }

    @Test 
    public void whenCheckIfAnnotationIsPresentWithNestedObject()
    {
        ObjectProxy<A> proxy = ObjectProxyFactory.of(A.class);
        Converter converter = proxy.getAnnotationField(Converter.class, "b.c.makeMeTrue");
        assertThat(converter, notNullValue());
        assertThat(converter.pattern(), is("1|0"));
    }
    
}
