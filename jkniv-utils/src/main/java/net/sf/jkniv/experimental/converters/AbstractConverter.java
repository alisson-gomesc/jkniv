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
package net.sf.jkniv.experimental.converters;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO document me
 * TODO Test case all
 * 
 * @author Alisson Gomes
 *
 */
abstract class AbstractConverter implements TypeConvertible
{
    protected boolean      allowNull;
    protected List<String> patterns;
    
    public AbstractConverter(boolean allowNull)
    {
        this.allowNull = allowNull;
        this.patterns = new ArrayList<String>();
    }

    public boolean nullable()
    {
        return this.allowNull;
    }
    
    public void withPattern(String pattern)
    {
        this.patterns.add(0, pattern);
    }
    
    public void cleanPatterns()
    {
        this.patterns.clear();
    }
    
}
