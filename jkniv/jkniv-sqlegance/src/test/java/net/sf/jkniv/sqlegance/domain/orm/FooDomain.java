/* 
 * JKNIV, SQLegance keeping queries maintainable.
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
package net.sf.jkniv.sqlegance.domain.orm;

public class FooDomain
{
    private String name;
    private int    age;
    private String state;
    private String title;
    private Author author;
    
    public FooDomain(String name, int age, String state, String title, Author author)
    {
        this.name = name;
        this.age = age;
        this.state = state;
        this.title = title;
        this.author = author;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public int getAge()
    {
        return age;
    }
    
    public void setAge(int age)
    {
        this.age = age;
    }
    
    public String getState()
    {
        return state;
    }
    
    public void setState(String state)
    {
        this.state = state;
    }
    
    public String getTitle()
    {
        return title;
    }
    
    public void setTitle(String title)
    {
        this.title = title;
    }
    
    public Author getAuthor()
    {
        return author;
    }
    
    public void setAuthor(Author author)
    {
        this.author = author;
    }
}
