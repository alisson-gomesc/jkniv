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

import java.lang.reflect.Field;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sf.jkniv.reflect.ObjectNotFoundException;

public class FieldReflectTest
{
    @Rule
    public ExpectedException catcher = ExpectedException.none();  

    @Test
    public void whenFieldFromParentClass()
    {
        FieldReflect getter = new FieldReflect();
        Field field = getter.getField("name", A.class);
        assertThat(field, notNullValue());
        assertThat(field.getName(), is("name"));
        assertThat(field.getDeclaringClass().getName(), is(A.class.getName()));
    }

    @Test
    public void whenFieldFromFirstSubClass()
    {
        FieldReflect getter = new FieldReflect();
        Field field = getter.getField("b.name", A.class);
        assertThat(field, notNullValue());
        assertThat(field.getName(), is("name"));
        assertThat(field.getDeclaringClass().getName(), is(B.class.getName()));
    }

    @Test
    public void whenFieldFromSecondSubClass()
    {
        FieldReflect getter = new FieldReflect();
        Field field = getter.getField("b.c.name", A.class);
        assertThat(field, notNullValue());
        assertThat(field.getName(), is("name"));
        assertThat(field.getDeclaringClass().getName(), is(C.class.getName()));
    }

    @Test
    public void whenFieldFromSecondSubClassDifferentName()
    {
        FieldReflect getter = new FieldReflect();
        Field field = getter.getField("b.c.active", A.class);
        assertThat(field, notNullValue());
        assertThat(field.getName(), is("active"));
        assertThat(field.getDeclaringClass().getName(), is(C.class.getName()));
    }

    @Test
    public void whenFieldWithOverrideName()
    {
        FieldReflect getter = new FieldReflect();
        Field field = getter.getField("b.name", A.class);
        assertThat(field, notNullValue());
        assertThat(field.getName(), is("name"));
        assertThat(field.getDeclaringClass().getName(), is(B.class.getName()));
    }
    
    @Test
    public void whenFieldNotExist()
    {
        catcher.expect(ObjectNotFoundException.class);
        catcher.expectMessage("Can not found the field [noname]");
        new FieldReflect().getField("noname", A.class);
    }


}
