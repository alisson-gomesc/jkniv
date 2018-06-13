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

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import net.sf.jkniv.acme.domain.Book;
import net.sf.jkniv.reflect.ReflectionException;

public class ObjectProxyTest
{
    
    @Test
    public void whenHaveInstance()
    {
        ObjectProxy<Book> bean = new DefaultObjectProxy<Book>(new Book());
        Assert.assertTrue(bean.hasInstance());
    }
    
    @Test
    public void whenHaveClassInstancingWithDefaultConstructor()
    {
        ObjectProxy<Book> bean = new DefaultObjectProxy<Book>(Book.class);
        bean.newInstance();
        Assert.assertTrue(bean.hasInstance());
    }
    
    @Test
    public void whenHasnotInstance()
    {
        ObjectProxy<Book> bean = new DefaultObjectProxy<Book>(Book.class);
        Assert.assertFalse(bean.hasInstance());
    }
    
    @Test//(expected = NullPointerException.class)
    //@Ignore(value = "A null instance can be exists because now a scalar value could be generated")
    //public void whenHavenotInstanceThrowException()
    public void whenGetInstanceIsNull()
    {
        ObjectProxy<Book> bean = new DefaultObjectProxy<Book>(Book.class);
        Book b = bean.getInstance();
        assertThat(b, nullValue());
    }
    
    @Test
    public void whenConstructorHaveArgumentsOmittingTypes()
    {
        Object[] initArgs =
        { Long.valueOf(1), "Name", "ISBN", "Author" };
        ObjectProxy<Book> bean = new DefaultObjectProxy<Book>(Book.class);
        bean.setConstructorArgs(initArgs);
        bean.newInstance();
        Assert.assertTrue(bean.hasInstance());
        Assert.assertTrue(bean.getTargetClass().getName().equals(Book.class.getName()));
    }
    
    @Test
    public void whenConstructorHaveWrongArgumentsButTypeMatch()
    {
        Object[] initArgs =
        { Long.valueOf(1), "Name", "ISBN", "Author" };
        ObjectProxy<Book> bean = new DefaultObjectProxy<Book>(Book.class);
        bean.setConstructorArgs(initArgs);
        bean.setConstructorTypes(Long.class, Long.class, String.class, String.class);
        bean.newInstance();
        Assert.assertTrue(bean.hasInstance());
        Assert.assertTrue(bean.getTargetClass().getName().equals(Book.class.getName()));
    }
    
    @Test
    public void whenConstructorHaveArgumentsAndConstructorTypes()
    {
        Object[] initArgs =
        { Long.valueOf(1), "Name", "ISBN", "Author" };
        ObjectProxy<Book> bean = new DefaultObjectProxy<Book>(Book.class);
        bean.setConstructorArgs(initArgs);
        bean.setConstructorTypes(Long.class, String.class, String.class, String.class);
        bean.newInstance();
        Assert.assertTrue(bean.hasInstance());
        Assert.assertTrue(bean.getTargetClass().getName().equals(Book.class.getName()));
    }
    
    @Test
    public void whenConstructorObjectHasSuperType()
    {
        ObjectProxy<ReflectionException> bean = new DefaultObjectProxy<ReflectionException>(ReflectionException.class);
        bean.setConstructorArgs("My Exception Message", new RuntimeException("Im cause Exception"));
        ReflectionException exception = bean.newInstance();
        Assert.assertNotNull("Cannot create Exception with super type", exception);
    }
    
    @Test
    public void whenConstructorObjectHasSuperType2()
    {
        ObjectProxy<ReflectionException> bean = new DefaultObjectProxy<ReflectionException>(ReflectionException.class);
        bean.setConstructorArgs("My Exception Message", new NoSuchMethodException("Im cause Exception"));
        ReflectionException exception = bean.newInstance();
        Assert.assertNotNull("Cannot create Exception with super type", exception);
    }

}
