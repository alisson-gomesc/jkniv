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

public class IsNull extends AbstractAssert
{
    IsNull()
    {
        super("[Assertion failed] - the object argument must be null");
    }
    
    /**
     * @param message the exception message to use if the assertion fails
     */
    IsNull(String message)
    {
        super(message);
    }
    
    /**
     * Assert that an object is {@code null} .
     * <pre class="code">Assert.isNull(value, "The value must be null");</pre>
     * @param objects the object to check
     * @throws IllegalArgumentException if the object is not {@code null}
     */
    public void verify(Object... objects)
    {
        if (verifyObjects(objects))
            throwDefaultException();
    }
    
    public void verify(RuntimeException e, Object... objects)
    {
        if (verifyObjects(objects))
            throw e;
    }
    
    public void verifyArray(Object[] object)
    {
        if (verifyObject(object))
            throwDefaultException();
    }
    
    public void verifyArray(RuntimeException e, Object[] object)
    {
        if (verifyObject(object))
            throw e;
    }
    
    private boolean verifyObjects(Object... objects)
    {
        boolean ret = false;
        if (objects != null && objects.length > 0)
        {
            for (Object o : objects)
            {
                if (verifyObject(o))
                {
                    ret = true;
                    break;
                }
            }
        }
        return ret;
    }
    
    private boolean verifyObject(Object object)
    {
        boolean ret = false;
        if (object != null)
            ret = true;
        
        return ret;
    }
}
