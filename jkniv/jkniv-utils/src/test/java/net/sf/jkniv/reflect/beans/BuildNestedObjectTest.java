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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sf.jkniv.acme.domain.Author;
import net.sf.jkniv.acme.domain.Book;
import net.sf.jkniv.acme.domain.ContainerType;
import net.sf.jkniv.acme.domain.MyConcreteForAbstract;
import net.sf.jkniv.acme.domain.NestedContainerType;
import net.sf.jkniv.reflect.ReflectionException;

public class BuildNestedObjectTest
{
    @Rule
    public ExpectedException catcher = ExpectedException.none();  
    
    @Test
    public void whenBuildNestedObjecLevelOne()
    {
        Book book = new Book();
        BuildNestedObject build = new BuildNestedObject(book);
        
        build.setter("author.name", "john");
        
        assertThat(book.getAuthor(), notNullValue());
        assertThat(book.getAuthor().getName(), notNullValue());
        assertThat(book.getAuthor().getName(), is("john"));
    }

    @Test
    public void whenBuildAbstractWithInstaceTypeLevelOne()
    {
        NestedContainerType nestedContainer = new NestedContainerType();
        BuildNestedObject build = new BuildNestedObject(nestedContainer);
        ContainerType container = new ContainerType();
        container.setMyAbstractClass(new MyConcreteForAbstract());
        nestedContainer.setTypes(container);
        build.setter("types.myAbstractClass.name", "john");
        build.setter("types.myAbstractClass.id", 1L);
        
        assertThat(nestedContainer.getTypes().getMyAbstractClass(), notNullValue());
        assertThat(nestedContainer.getTypes().getMyAbstractClass().getName(), is("john"));
        assertThat(nestedContainer.getTypes().getMyAbstractClass().getId(), is(1L));
    }

    @Test
    public void whenBuildNestedSeveralSimpleTypesLevelOne()
    {
        NestedContainerType nestedContainer = new NestedContainerType();
        BuildNestedObject build = new BuildNestedObject(nestedContainer);
        build.setter("types.author.name", "john");
        build.setter("types.book.author.name", "john");
        build.setter("types.book.name", "john's saga");
        build.setter("types.doubleNumber", 2D);
        build.setter("types.doubleNumberD", Double.valueOf("3.3"));
        build.setter("types.floatNumber", 2F);
        build.setter("types.floatNumberF", Float.valueOf("3.3"));
        build.setter("types.longNumber", 2L);
        build.setter("types.longNumberL", Long.valueOf("3"));
        build.setter("types.intNumber", 2);
        build.setter("types.integerNumberI", Integer.valueOf("3"));
        build.setter("types.booleanB", true);
        build.setter("types.booleanB2", Boolean.FALSE);
        build.setter("types.shortNumber", (short)2);
        build.setter("types.shortNumberS", Short.valueOf("3"));
        build.setter("types.charNumber", 'A');
        build.setter("types.characterNumberC", new Character('B'));
        build.setter("types.byteNumber", (byte)2);
        build.setter("types.byteNumberB", new Byte("3"));
        build.setter("types.date", new Date());
        build.setter("types.string", "my string");
        build.setter("types.gregorianCalendar", new GregorianCalendar());

        //private Calendar calendar;

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
        Book book = new Book();
        book.setAuthor(new Author());
        BuildNestedObject build = new BuildNestedObject(book);
        
        build.setter("author.name", "john");
        
        assertThat(book.getAuthor(), notNullValue());
        assertThat(book.getAuthor().getName(), notNullValue());
        assertThat(book.getAuthor().getName(), is("john"));
    }


   /////////////////  fail tests
    @Test
    public void whenBuildMismatchTypeLevelOneFail()
    {
        NestedContainerType container = new NestedContainerType();
        BuildNestedObject build = new BuildNestedObject(container);
        catcher.expect(ReflectionException.class);
        //catcher.expectCause(new CauseMatcher(IllegalArgumentException.class, "argument type mismatch"));
        build.setter("types.string", new Date());
    }

    @Test
    public void whenBuildAbstractTypeLevelOneFail()
    {
        NestedContainerType container = new NestedContainerType();
        BuildNestedObject build = new BuildNestedObject(container);
        catcher.expect(ReflectionException.class);
        build.setter("types.myAbstractClass.name", "some value");
    }

}
