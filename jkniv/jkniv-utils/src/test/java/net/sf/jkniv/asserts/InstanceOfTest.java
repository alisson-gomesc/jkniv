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
package net.sf.jkniv.asserts;

import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class InstanceOfTest
{

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static final String MESSAGE_EXPECTED = "[Assertion failed] - this argument must be instance of {}, found {}";
    private static final String MESSAGE2_EXPECTED = "[Programming failed] - instance of must have pair (Object, Class...) to check right instance";
    
    
    private void setUpException(String message)
    {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(message);
    }
    
    @Test
    public void whenParameterIsNull()
    {
        setUpException("[Assertion failed] - this argument is required; it must not be null");
        Assertable instanceOf = new InstanceOf();
        instanceOf.verify(null);
    }
    
    @Test
    public void whenIsnotPairObjectAndClass()
    {
        setUpException(MESSAGE2_EXPECTED);
        Assertable instanceOf = new InstanceOf();
        String s1 = "A";
        instanceOf.verify(s1);
    }

    @Test
    public void whenArrayHaveNullValues()
    {
        setUpException("[Assertion failed] - this argument is required; it must not be null");
        Assertable instanceOf = new InstanceOf();
        String[] elements = {"A", null};
        instanceOf.verify(elements);
    }

    @Test
    public void whenParameterIsNullWithSpecifcMessage()
    {
        setUpException("Oh! my object is not String");
        Assertable instanceOf = new InstanceOf("Oh! my object is not String");
        Integer i = 0;
        instanceOf.verify(i, String.class);
    }

    @Test
    public void whenTypeIsExpected()
    {
        Assertable instanceOf = new InstanceOf();
        instanceOf.verify("A", String.class);
        Assert.assertTrue(true);
    }

    @Test
    public void whenIsNotNullManyParameters()
    {
        Assertable instanceOf = new InstanceOf();
        instanceOf.verify("A1", String.class, Integer.valueOf(0), Integer.class, Long.valueOf(0), Long.class);
        Assert.assertTrue(true);
    }

    @Test
    public void whenParameterArrayZero()
    {
        Assertable instanceOf = new InstanceOf();
        instanceOf.verify(new Object[0]);
        Assert.assertTrue(true);
    }
    // ------------------------- verifyArray tests --------------------

    @Test
    public void whenArrayIsNull()
    {
        setUpException("[Programming failed] - instance of arrays isn't implemented");
        Assertable instanceOf = new InstanceOf();
        String[] elements = null;
        instanceOf.verifyArray(elements);
        assertThat("Woking", true);
    }

    @Test
    public void whenArrayHaveNullValuesButArrayIsNotNull()
    {
        setUpException("[Programming failed] - instance of arrays isn't implemented");
        Assertable intanceOf = new InstanceOf();
        String[] elements = {"A", null, "C"};
        intanceOf.verifyArray(elements);
        assertThat("Woking", true);
    }

}
