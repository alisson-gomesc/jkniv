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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;

import org.junit.Test;

public class NumberWrapperTypesReflectionTest
{

    @Test
    public void whenBuildBigDecimalWithString() throws Exception
    {
        String arg = "500";
        Constructor<BigDecimal> c = BigDecimal.class.getConstructor(String.class);
        BigDecimal expected = new BigDecimal(arg);
        BigDecimal number = (BigDecimal)c.newInstance(arg);
        assertThat(number, is(expected));
    }

    @Test
    public void whenBuildBigDecimalWithStringWithSpace() throws Exception
    {
        String arg = "500 ";
        Constructor<BigDecimal> c = BigDecimal.class.getConstructor(String.class);
        BigDecimal expected = new BigDecimal(arg.trim());
        BigDecimal number = (BigDecimal)c.newInstance(arg.trim());
        assertThat(number, is(expected));
    }

    @Test
    public void whenBuildBigDecimalWithDouble() throws Exception
    {
        double arg = 500.58D;
        Constructor<BigDecimal> c = BigDecimal.class.getConstructor(double.class);
        BigDecimal expected = new BigDecimal(arg);
        BigDecimal number = (BigDecimal)c.newInstance(arg);
        assertThat(number, is(expected));
    }

}
