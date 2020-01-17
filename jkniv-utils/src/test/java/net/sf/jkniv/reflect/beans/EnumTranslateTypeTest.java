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
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import net.sf.jkniv.acme.domain.FooColor;

public class EnumTranslateTypeTest
{
    @Test
    public void whenHaveEnumRuntimeAsString() throws ClassNotFoundException
    {
        EnumTranslateType converter = new EnumTranslateType();
        FooColor RED = converter.convert(FooColor.class, "RED");
        FooColor WHITE = converter.convert(FooColor.class, 2);
        assertThat(RED, is(FooColor.RED));
        assertThat(WHITE, is(FooColor.WHITE));
    }
}
