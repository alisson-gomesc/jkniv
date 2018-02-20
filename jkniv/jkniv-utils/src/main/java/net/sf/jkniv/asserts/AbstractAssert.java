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

/**
 * 
 * Assertion utility class that assists in validating arguments.
 * Useful for identifying programmer errors early and clearly at runtime.
 * 
 * @author Alisson Gomes
 *
 */
abstract class AbstractAssert implements Assertable
{
    protected final String message;
    
    abstract public void verify(Object...o);

    
    public AbstractAssert()
    {
        this("[Assertion failed] - the object argument is not asserted");
    }
    
    public AbstractAssert(String message)
    {
        this.message = message;
    }
    
    
    protected void throwDefaultException()
    {
        throw new IllegalArgumentException(message);
    }
    
    protected void throwDefaultException(String... args)
    {
        throw new IllegalArgumentException(String.format(message, args));
    }
    
    protected void throwDefaultException(String message)
    {
        throw new IllegalArgumentException(message);
    }

}
