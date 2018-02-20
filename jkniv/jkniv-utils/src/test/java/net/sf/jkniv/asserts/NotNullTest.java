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

import net.sf.jkniv.asserts.Assertable;
import net.sf.jkniv.asserts.NotNull;

import org.junit.Assert;
import org.junit.Test;

public class NotNullTest
{
    
    @Test(expected = IllegalArgumentException.class)
    public void whenParameterIsNull()
    {
        Assertable notNull = new NotNull();
        notNull.verify(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void whenLeastOneParameterIsNull()
    {
        Assertable notNull = new NotNull();
        String s1 = "A", s2 = null;
        notNull.verify(s1, s2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenArrayHaveNullValues()
    {
        Assertable notNull = new NotNull();
        String[] elements = {"A", null, "C"};
        notNull.verify(elements);
    }

    @Test
    public void whenParameterIsNullWithSpecifcMessage()
    {
        Assertable notNull = new NotNull("Oh! my object is null");
        String s1 = null;
        try 
        {
            notNull.verify(s1);
        }
        catch(IllegalArgumentException e)
        {
            Assert.assertEquals("Oh! my object is null", e.getMessage());
        }
    }

    @Test
    public void whenIsNotNullOneParameter()
    {
        Assertable notNull = new NotNull();
        String s1 = "A";
        notNull.verify(s1);
        Assert.assertTrue(true);
    }

    @Test
    public void whenIsNotNullManyParameters()
    {
        Assertable notNull = new NotNull();
        String s1 = "A", s2 = "B", s3 = "C";
        notNull.verify(s1, s2, s3);
        Assert.assertTrue(true);
    }

    @Test
    public void whenParameterArrayZero()
    {
        Assertable notNull = new NotNull();
        notNull.verify(new Object[0]);
        Assert.assertTrue(true);
    }
    // ------------------------- verifyArray tests --------------------

    @Test(expected = IllegalArgumentException.class)
    public void whenArrayIsNull()
    {
        Assertable notNull = new NotNull();
        String[] elements = null;
        notNull.verifyArray(elements);
        Assert.assertTrue(true);
    }

    @Test
    public void whenArrayHaveNullValuesButArrayIsNotNull()
    {
        Assertable notNull = new NotNull();
        String[] elements = {"A", null, "C"};
        notNull.verifyArray(elements);
        Assert.assertTrue(true);
    }

}
