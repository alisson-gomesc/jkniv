/* 
 * JKNIV, whinstone one contract to access your database.
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
package net.sf.jkniv.whinstone.jdbc.domain.flat;

import java.util.ArrayList;
import java.util.List;

public class Color
{
    private String       name;
    private Long          code;
    private List<String> priorities;
    
    public Color()
    {
        this(null, 0);
    }
    
    public Color(String name, long code)
    {
        this.name = name;
        this.code = code;
        this.priorities = new ArrayList<String>();
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public Long getCode()
    {
        return code;
    }
    
    public void setCode(Long code)
    {
        this.code = code;
    }
    
    public List<String> getPriorities()
    {
        return priorities;
    }
        
    public boolean setPriority(String priority)
    {
        return priorities.add(priority);
    }
    
    public void setPriorities(List<String> priorities)
    {
        this.priorities = priorities;
    }
    
    @Override
    public String toString()
    {
        return "Color [name=" + name + ", code=" + code + ", priorities=" + priorities + "]";
    }
    
}
