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

@SuppressWarnings("rawtypes")
public class InstanceOf extends AbstractAssert
{
    private static final Assertable notNull = new NotNull();
    
    InstanceOf()
    {
        super("[Assertion failed] - this argument must be instance of %s, found %s");
    }
    
    /**
     * @param message the exception message to use if the assertion fails
     */
    InstanceOf(String message)
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
        notNull.verify(objects);
        if (verifyObjects(objects))
            throwDefaultException();
    }

    public void verify(RuntimeException e, Object... objects)
    {
        notNull.verify(objects);
        if (verifyObjects(objects))
            throw e;
    }

    public void verifyArray(Object[] object)
    {
        throw new IllegalArgumentException("[Programming failed] - instance of arrays isn't implemented");
//        if (verifyObject(object))
//            throwDefaultException();
//        else if (!object.getClass().isArray())
//        throw new IllegalArgumentException("[Assertion failed] - this argument is not array");
    }

    public void verifyArray(RuntimeException e, Object[] object)
    {
        throw new IllegalArgumentException("[Programming failed] - instance of arrays isn't implemented");
//        if (verifyObject(object))
//            throw e;
//        else if (!object.getClass().isArray())
//          throw new IllegalArgumentException("[Assertion failed] - this argument is not array");
    }

    private boolean verifyObjects(Object... objects)
    {
        boolean ret = false;
        if (objects == null)
            ret = true;
        else if (objects.length%2 != 0)
            throw new IllegalArgumentException("[Programming failed] - instance of must have pair (Object, Class...) to check right instance");
        else
        {
            for (int i=0; i<objects.length; i++)
            {
                Object o = objects[i];
                Object clazz = objects[++i];
                if ( !(clazz instanceof Class) )
                    new IllegalArgumentException(String.format("[Programming failed] - argument [%i] must be instance of Class to check right instanceof", i));
                
                if(verifyObject(o, (Class)clazz))
                    throwDefaultException(((Class)clazz).getName(), o.getClass().getName());
            }
        }
        return ret;
    }

    private boolean verifyObject(Object object, Class clazz)
    {
        boolean answer = false;
        if (!clazz.isInstance(object))
            answer = true;
        return answer;
    }
}
