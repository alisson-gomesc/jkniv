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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sf.jkniv.acme.domain.Book;
import net.sf.jkniv.acme.domain.Child;
import net.sf.jkniv.acme.domain.PrimitiveDataType;

public class MethodInjectTest
{
    @Rule
    public ExpectedException catcher = ExpectedException.none();  

    
    @Test
    public void whenHaveInheritanceClassInjectValueWorks()
    {
        Child instance = new Child();
        Injectable<Child> reflect = new MethodInjection<Child>(instance);
        final String SURNAME = "Silva", NAME = "Jose";
        reflect.inject("setSurname", SURNAME);
        reflect.inject("setName", NAME);
        
        assertThat(instance.getSurname(), is(SURNAME));
        assertThat(instance.getName(), is(NAME));
    }
    
    @Test
    public void whenInjectPrimitiveType()
    {
        byte myByte = 10;
        short myShort = 100;
        int myInt = 1000;
        long myLong = 10000;
        float myFloat = 10000.01F;
        double myDouble = 10000.02D;
        boolean myBoolean = true;
        char myChar = 'C';
        
        PrimitiveDataType instance = new PrimitiveDataType();
        
        Injectable<PrimitiveDataType> reflect = new MethodInjection<PrimitiveDataType>(instance);
        reflect.inject("setMyByte", myByte);
        reflect.inject("setMyShort", myShort);
        reflect.inject("setMyInt", myInt);
        reflect.inject("setMyLong", myLong);
        reflect.inject("setMyFloat", myFloat);
        reflect.inject("setMyDouble", myDouble);
        reflect.inject("setMyBoolean", myBoolean);
        reflect.inject("setMyChar", myChar);
        
        assertThat(instance.getMyByte(), is(myByte));
        assertThat(instance.getMyShort(), is(myShort));
        assertThat(instance.getMyInt(), is(myInt));
        assertThat(instance.getMyLong(), is(myLong));
        assertThat(instance.getMyFloat(), is(myFloat));
        assertThat(instance.getMyDouble(), is(myDouble));
        assertThat(instance.isMyBoolean(), is(myBoolean));
        assertThat(instance.getMyChar(), is(myChar));
        
    }
    
    @Test
    public void whenInjectArrayPrimitiveType()
    {
        // TODO test me
    }
    
    
    @Test
    public void whenInjectedNestedValues()
    {
        Book instance = new Book();
        Injectable<Book> reflect = new MethodInjection<Book>(instance);
        final String myName = "Alisson";
        final String myBookName = "Kafka Metamorphose";
        final int age =  34;
        reflect.inject("setName", myBookName);
        reflect.inject("author.setName", myName); // with set prefix
        reflect.inject("author.age", age); // without set prefix
        assertThat(instance.getName(), is(myBookName));
        assertThat(instance.getAuthor(), notNullValue());
        assertThat(instance.getAuthor().getName(), is(myName));
        assertThat(instance.getAuthor().getAge(), is(age));
    }

}
