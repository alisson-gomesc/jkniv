/* 
 * JKNIV ,
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

public class AssertsFactory
{
    private static Assertable isNull;
    private static Assertable notNull;
    private static Assertable notEmpty;
    private static Assertable isTrue;
    private static Assertable instanceOf;

    public static Assertable getIsNull()
    {
        if (isNull == null)
            isNull = new IsNull();
        
        return isNull;
    }
    
    public static Assertable getIsTrue()
    {
        if (isTrue == null)
            isTrue = new IsTrue();
        
        return isTrue;
    }
    
    
    public static Assertable getNotNull()
    {
        if (notNull == null)
            notNull = new NotNull();
        
        return notNull;
    }
    
    public static Assertable getNotEmpty()
    {
        if (notEmpty == null)
            notEmpty = new NotEmpty();
        
        return notEmpty;
    }

    public static Assertable getInstanceOf()
    {
        if (instanceOf == null)
            instanceOf = new InstanceOf();
        
        return instanceOf;
    }

    public static Assertable newIsNull(String message)
    {
        return new IsNull(message);
    }

    public static Assertable newNotNull(String message)
    {
        return new NotNull(message);
    }

    public static Assertable newIsTrue(String message)
    {
        return new IsTrue(message);
    }

    public static Assertable newNotEmpty(String message)
    {
        return new NotEmpty(message);
    }

    public static Assertable newInstanceOf(String message)
    {
        return new InstanceOf(message);
    }

}
