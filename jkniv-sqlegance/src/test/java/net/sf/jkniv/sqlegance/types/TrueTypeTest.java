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

public class TrueTypeTest
{

    @Test
    public void whenTrueIsOneAndFalseIsZero()  
    {
        TrueType type = new TrueType("1|0");
        
        assertThat(type.toAttribute("0"), is(Boolean.FALSE));
        assertThat(type.toAttribute("1"), is(Boolean.TRUE));
        assertThat(type.toAttribute("2"), is(Boolean.FALSE));
        assertThat(type.toAttribute("A"), is(Boolean.FALSE));

        assertThat(type.toJdbc(Boolean.FALSE), is("0"));
        assertThat(type.toJdbc(Boolean.TRUE), is("1"));
        assertThat(type.toJdbc(true), is("1"));
        assertThat(type.toJdbc(false), is("0"));
    }
}
