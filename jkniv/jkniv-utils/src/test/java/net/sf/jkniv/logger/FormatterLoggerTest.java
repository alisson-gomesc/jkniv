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
package net.sf.jkniv.logger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

public class FormatterLoggerTest
{
    @Test
    public void whenStringHaventParameter()
    {
        FormatterLogger f = new FormatterLogger();
        String msg = "The user cannot make login with password";
        assertThat(f.formatterSlf4j(msg), is(msg));
    }

    @Test
    public void whenStringHaveOneParameterInBegin()
    {
        FormatterLogger f = new FormatterLogger();
        String msg = "{} user cannot make login with password";
        String expected  = "%1$s user cannot make login with password";
        assertThat(f.formatterSlf4j(msg), is(expected));
    }

    @Test
    public void whenStringHaveOneParameterInEnd()
    {
        FormatterLogger f = new FormatterLogger();
        String msg = "user cannot make login with password {}";
        String expected  = "user cannot make login with password %1$s";
        assertThat(f.formatterSlf4j(msg), is(expected));
    }

    @Test
    public void whenStringHaveParameterInBegindAndEnd()
    {
        FormatterLogger f = new FormatterLogger();
        String msg = "{} user cannot make login with password {}";
        String expected = "%1$s user cannot make login with password %2$s";
        assertThat(f.formatterSlf4j(msg), is(expected));
    }

    @Test
    public void whenStringHaveParameterSeveral()
    {
        FormatterLogger f = new FormatterLogger();
        String msg = "The sum {}+{}+{}+{}={} its correct when {}";
        String expected = "The sum %1$s+%2$s+%3$s+%4$s=%5$s its correct when %6$s";
        assertThat(f.formatterSlf4j(msg), is(expected));
    }

    @Test
    public void whenStringHaveParameterInMidlle()
    {
        FormatterLogger f = new FormatterLogger();
        String msg = "The user {} cannot make login with password [{}]";
        String expected = "The user %1$s cannot make login with password [%2$s]";
        assertThat(f.formatterSlf4j(msg), is(expected));
    }
}
