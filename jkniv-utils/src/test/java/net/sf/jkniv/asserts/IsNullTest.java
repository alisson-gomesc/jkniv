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
import net.sf.jkniv.asserts.IsNull;

import org.junit.Assert;
import org.junit.Test;

public class IsNullTest 
{
    
    @Test(expected = IllegalArgumentException.class)
    public void whenParamenterIsNotNull()
    {
        Assertable isNull = new IsNull();
        String s1 = "A", s2 = "B", s3 = "C";
        isNull.verify(s1, s2, s3);
    }
    
    @Test
    public void whenParamenterArrayZero()
    {
        Assertable isNull = new IsNull();
        isNull.verify(new Object[0]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenArrayHaveNullValues()
    {
        Assertable isNull = new IsNull();
        String[] elements = {"A", null, "C"};
        isNull.verify(elements);
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenLeastOneParameterIsNotNull()
    {
        Assertable isNull = new IsNull();
        String s1 = "A", s2 = null;
        isNull.verify(s1, s2);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void whenParamenterIsNotNullWithSpecifcMessage()
    {
        Assertable isNull = new IsNull("Oh! my object is null");
        String s1 = "A";
        isNull.verify(s1);
    }
    
    @Test
    public void whenIsNullParameters()
    {
        Assertable isNull = new IsNull();
        String s1 = null, s2 = null, s3 = null;
        isNull.verify(s1, s2, s3);
        Assert.assertTrue(true);
    }
    
    // ------------------------- verifyArray tests --------------------
    

    @Test
    public void whenArrayIsNull()
    {
        Assertable isNull = new IsNull();
        String[] elements = null;
        isNull.verifyArray(elements);
        Assert.assertTrue(true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void whenArrayHaveNullValuesButArrayIsNotNull()
    {
        Assertable isNull = new IsNull();
        String[] elements = {"A", null, "C"};
        isNull.verifyArray(elements);
        Assert.assertTrue(true);
    }
    
}
