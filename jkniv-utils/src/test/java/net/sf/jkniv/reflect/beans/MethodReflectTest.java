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
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.lang.reflect.Method;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sf.jkniv.reflect.ObjectNotFoundException;

public class MethodReflectTest
{
    @Rule
    public ExpectedException catcher = ExpectedException.none();  

    @Test
    public void whenMethodFromParentClass()
    {
        MethodReflect getter = new MethodReflect();
        Method method = getter.getMethod("name", A.class);
        assertThat(method, notNullValue());
        assertThat(method.getName(), is("getName"));
        assertThat(method.getDeclaringClass().getName(), is(A.class.getName()));
    }

    @Test
    public void whenMethodFromFirstSubClass()
    {
        MethodReflect getter = new MethodReflect();
        Method method = getter.getMethod("b.name", A.class);
        assertThat(method, notNullValue());
        assertThat(method.getName(), is("getName"));
        assertThat(method.getDeclaringClass().getName(), is(B.class.getName()));
    }

    @Test
    public void whenMethodFromSecondSubClass()
    {
        MethodReflect getter = new MethodReflect();
        Method method = getter.getMethod("b.c.name", A.class);
        assertThat(method, notNullValue());
        assertThat(method.getName(), is("getName"));
        assertThat(method.getDeclaringClass().getName(), is(C.class.getName()));
    }

    @Test
    public void whenMethodFromSecondSubClassDifferentName()
    {
        MethodReflect getter = new MethodReflect();
        Method method = getter.getMethod("b.c.active", A.class);
        assertThat(method, notNullValue());
        assertThat(method.getName(), is("getActive"));
        assertThat(method.getDeclaringClass().getName(), is(C.class.getName()));
    }

    @Test
    public void whenMethodAccessIsboolean()
    {
        MethodReflect getter = new MethodReflect();
        Method method = getter.getMethod("b.c.cancel", A.class);
        assertThat(method, notNullValue());
        assertThat(method.getName(), is("isCancel"));
        assertThat(method.getDeclaringClass().getName(), is(C.class.getName()));
    }

    @Test
    public void whenMethodWithOverrideName()
    {
        MethodReflect getter = new MethodReflect();
        Method method = getter.getMethod("b.name", A.class);
        assertThat(method, notNullValue());
        assertThat(method.getName(), is("getName"));
        assertThat(method.getDeclaringClass().getName(), is(B.class.getName()));
    }
    
    @Test
    public void whenMethodNotExist()
    {
        catcher.expect(ObjectNotFoundException.class);
        catcher.expectMessage("[NoSuchMethodException] Can not found the method [net.sf.jkniv.reflect.beans.A.isNoname()]");
        new MethodReflect().getMethod("noname", A.class);
    }
}
