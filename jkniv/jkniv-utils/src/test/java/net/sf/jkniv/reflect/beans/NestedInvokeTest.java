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

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sf.jkniv.acme.domain.Author;
import net.sf.jkniv.acme.domain.Book;
import net.sf.jkniv.acme.domain.ContainerType;
import net.sf.jkniv.acme.domain.MyConcreteForAbstract;
import net.sf.jkniv.acme.domain.NestedContainerType;
import net.sf.jkniv.exception.HandlerException;

public class NestedInvokeTest
{
    @Rule
    public ExpectedException catcher = ExpectedException.none();  

    @Test
    public void whenBuildNestedObjecLevelOne()
    {
        Book book = new Book();
        Invokable call = new NestedInvoker(new HandlerException());
        
        call.invoke("author.name", book, "john");
        
        assertThat(book.getAuthor(), notNullValue());
        assertThat(book.getAuthor().getName(), notNullValue());
        assertThat(book.getAuthor().getName(), is("john"));
    }

    @Test
    public void whenBuildAbstractWithInstaceTypeLevelOne()
    {
        Invokable call = new NestedInvoker(new HandlerException());
        NestedContainerType nestedContainer = new NestedContainerType();
        ContainerType container = new ContainerType();
        container.setMyAbstractClass(new MyConcreteForAbstract());
        nestedContainer.setTypes(container);
        call.invoke("types.myAbstractClass.name", nestedContainer, "john");
        call.invoke("types.myAbstractClass.id", nestedContainer, 1L);
        
        assertThat(nestedContainer.getTypes().getMyAbstractClass(), notNullValue());
        assertThat(nestedContainer.getTypes().getMyAbstractClass().getName(), is("john"));
        assertThat(nestedContainer.getTypes().getMyAbstractClass().getId(), is(1L));
    }

    @Test
    public void whenBuildNestedSeveralSimpleTypesLevelOne()
    {
        Invokable call = new NestedInvoker(new HandlerException());
        NestedContainerType nestedContainer = new NestedContainerType();
        call.invoke("types.author.name", nestedContainer, "john");
        call.invoke("types.book.author.name", nestedContainer, "john");
        call.invoke("types.book.name", nestedContainer, "john's saga");
        call.invoke("types.doubleNumber", nestedContainer, 2D);
        call.invoke("types.doubleNumberD", nestedContainer, Double.valueOf("3.3"));
        call.invoke("types.floatNumber", nestedContainer, 2F);
        call.invoke("types.floatNumberF", nestedContainer, Float.valueOf("3.3"));
        call.invoke("types.longNumber", nestedContainer, 2L);
        call.invoke("types.longNumberL", nestedContainer, Long.valueOf("3"));
        call.invoke("types.intNumber", nestedContainer, 2);
        call.invoke("types.integerNumberI", nestedContainer, Integer.valueOf("3"));
        call.invoke("types.booleanB", nestedContainer, true);
        call.invoke("types.booleanB2", nestedContainer, Boolean.FALSE);
        call.invoke("types.shortNumber", nestedContainer, (short)2);
        call.invoke("types.shortNumberS", nestedContainer, Short.valueOf("3"));
        call.invoke("types.charNumber", nestedContainer, 'A');
        call.invoke("types.characterNumberC", nestedContainer, new Character('B'));
        call.invoke("types.byteNumber", nestedContainer, (byte)2);
        call.invoke("types.byteNumberB", nestedContainer, new Byte("3"));
        call.invoke("types.date", nestedContainer, new Date());
        call.invoke("types.string", nestedContainer, "my string");
        call.invoke("types.gregorianCalendar", nestedContainer, new GregorianCalendar());


        assertThat(nestedContainer.getTypes().getAuthor().getName(), is("john"));
        assertThat(nestedContainer.getTypes().getBook().getAuthor().getName(), is("john"));
        assertThat(nestedContainer.getTypes().getBook().getName(), is("john's saga"));
        assertThat(nestedContainer.getTypes().getDoubleNumber(), is(2D));
        assertThat(nestedContainer.getTypes().getDoubleNumberD(), is(Double.valueOf("3.3")));
        assertThat(nestedContainer.getTypes().getFloatNumber(), is(2F));
        assertThat(nestedContainer.getTypes().getFloatNumberF(), is(Float.valueOf("3.3")));
        assertThat(nestedContainer.getTypes().getLongNumber(), is(2L));
        assertThat(nestedContainer.getTypes().getLongNumberL(), is(Long.valueOf("3")));
        assertThat(nestedContainer.getTypes().getIntNumber(), is(2));
        assertThat(nestedContainer.getTypes().getIntegerNumberI(), is(Integer.valueOf("3")));
        assertThat(nestedContainer.getTypes().isBooleanB(), is(true));
        assertThat(nestedContainer.getTypes().getBoolean2B(), is(Boolean.FALSE));
        assertThat(nestedContainer.getTypes().getShortNumber(), is((short)2));
        assertThat(nestedContainer.getTypes().getShortNumberS(), is(Short.valueOf("3")));
        assertThat(nestedContainer.getTypes().getCharNumber(), is('A'));
        assertThat(nestedContainer.getTypes().getCharacterNumberC(), is(new Character('B')));
        assertThat(nestedContainer.getTypes().getByteNumber(), is((byte)2));
        assertThat(nestedContainer.getTypes().getByteNumberB(), is(new Byte("3")));
        
        assertThat(nestedContainer.getTypes().getDate(), notNullValue());
        assertThat(nestedContainer.getTypes().getString(), is("my string"));
        assertThat(nestedContainer.getTypes().getGregorianCalendar(), notNullValue());

    }

    @Test
    public void whenBuildNestedExistsObjecLevelOne()
    {
        Invokable call = new NestedInvoker(new HandlerException());
        Book book = new Book();
        book.setAuthor(new Author());
        
        call.invoke("author.name", book, "john");
        
        assertThat(book.getAuthor(), notNullValue());
        assertThat(book.getAuthor().getName(), notNullValue());
        assertThat(book.getAuthor().getName(), is("john"));
    }
    
    @Test
    public void whenReflectionNestedInvokeBasicMethodUnsupportedOperation()
    {
        Invokable call = new NestedInvoker(new HandlerException());
        catcher.expect(UnsupportedOperationException.class);
        call.invoke(String.class, "hello Exception");
    }
    
    @Test
    public void whenReflectionNestedInvokeMethodUnsupportedOperation() throws SecurityException, NoSuchMethodException
    {
        Invokable call = new NestedInvoker(new HandlerException());
        catcher.expect(UnsupportedOperationException.class);
        call.invoke(Map.class.getMethod("size"), "hello Exception");
    }


}
