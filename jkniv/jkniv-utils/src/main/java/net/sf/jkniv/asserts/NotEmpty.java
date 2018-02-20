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

import java.util.Collection;
import java.util.Map;

public class NotEmpty extends AbstractAssert
{
    NotEmpty()
    {
        super("[Assertion failed] - this collection must not be empty: it must contain at least 1 element");
    }
    
    /**
     * @param message the exception message to use if the assertion fails
     */
    NotEmpty(String message)
    {
        super(message);
    }
    
    /**
     * Assert that an object is not {@code null} .
     * <pre class="code">Assert.notNull(clazz, "The class must not be null");</pre>
     * @param objects the object to check
     * @throws IllegalArgumentException if the object is {@code null}
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
        if (objects == null || objects.length == 0)
            ret = true;
        else
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
        if (object == null)
            ret = true;
        else
        {
            if (object instanceof String && ((String) object).trim().length() == 0)
                ret = true;
            else if (object instanceof Collection && ((Collection<?>) object).isEmpty())
                ret = true;
            else if (object instanceof Map && ((Map<?,?>) object).isEmpty())
                ret = true;
            else if (object.getClass().isArray() && ((Object[]) object).length == 0)
                ret = true;
        }
        return ret;
    }
}
