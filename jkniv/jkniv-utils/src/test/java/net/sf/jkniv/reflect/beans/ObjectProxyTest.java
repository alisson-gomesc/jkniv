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
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.instanceOf;

import java.lang.reflect.Method;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sf.jkniv.acme.domain.Book;
import net.sf.jkniv.domain.orm.Animal;
import net.sf.jkniv.domain.orm.Cat;
import net.sf.jkniv.reflect.ReflectionException;

public class ObjectProxyTest
{
    @Rule
    public ExpectedException catcher = ExpectedException.none();  

    // TODO test me
    //java.lang.NoSuchMethodException: br.com.rwit.clsiv.api.ClsiPdvDTO.<init>(java.math.BigDecimal, java.lang.String, java.lang.String, java.math.BigDecimal, java.math.BigDecimal, java.lang.String, java.math.BigDecimal, null, java.lang.Character)
    // at java.lang.Class.getConstructor0(Class.java:3082)
    // at java.lang.Class.getConstructor(Class.java:1825)
    // at net.sf.jkniv.reflect.beans.DefaultObjectProxy.newInstance(DefaultObjectProxy.java:276)
    // at net.sf.jkniv.whinstone.jpa2.AbstractQueryJpaAdapter.cast(AbstractQueryJpaAdapter.java:173)
    // at net.sf.jkniv.whinstone.jpa2.QueryJpaAdapter.getResultList(QueryJpaAdapter.java:142)
    // at net.sf.jkniv.whinstone.jpa2.RepositoryJpa.list(RepositoryJpa.java:513)
    // at br.com.rwit.clsiv.api.services.RouteTagService.listPlanPdvs(RouteTagService.java:81)

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
        assertThat("Cannot create Exception with super type", exception, notNullValue());
    }
    
    @Test
    public void whenLookingForAnnotationMethods()
    {
        ObjectProxy<Animal> proxy = ObjectProxyFactory.newProxy(Animal.class);
        List<Method> methods = proxy.getAnnotationMethods(Before.class);
        
        assertThat(methods.size(), is(1));
        assertThat(methods.get(0).getAnnotations()[0], instanceOf(Before.class));
        
        
        ObjectProxy<Cat> proxyCat = ObjectProxyFactory.newProxy(Cat.class);
        List<Method> methodBefore = proxyCat.getAnnotationMethods(Before.class);
        List<Method> methodDeprecated = proxyCat.getAnnotationMethods(Deprecated.class);
        
        assertThat(methodBefore.size(), is(1));
        assertThat(methodDeprecated.size(), is(1));
        assertThat(methodBefore.get(0).getAnnotations()[0], instanceOf(Before.class));
        assertThat(methodDeprecated.get(0).getAnnotations()[0], instanceOf(Deprecated.class));
    }

    @Test
    public void whenTryInvokeMethodThatNotExists()
    {
        catcher.expect(ReflectionException.class);
        catcher.expectMessage("[ClassNotFoundException] -> no definition for the class with the specified name could be found");

        ObjectProxy<Animal> proxy = ObjectProxyFactory.newProxy(Animal.class);
        proxy.hasAnnotation("net.sf.jkniv.AnnotationNotExist");
    }

    @Test
    public void whenTryInvokeMethodThatNotExistsButIsMuted()
    {
        ObjectProxy<Animal> proxy = ObjectProxyFactory.newProxy(Animal.class);
        proxy.mute(ClassNotFoundException.class);
        proxy.hasAnnotation("net.sf.jkniv.AnnotationNotExist");
        assertThat(true, is(true));
    }

    @Test
    public void whenCheckIfAnnotationIsPresentAtClass()
    {
        ObjectProxy<Book> proxy = ObjectProxyFactory.newProxy(Book.class);
        proxy.newInstance();
        assertThat(proxy.hasAnnotation("javax.annotation.Resource"), is(true));
        assertThat(proxy.hasAnnotation(javax.annotation.Resource.class), is(true));
    }

}
