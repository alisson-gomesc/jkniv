/*
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
package com.acme.domain.noorm;

public class Book
{
    private Long   id;
    private String name;
    private String isbn;
    private Author author;
    
    
    
    public Book()
    {
        super();
    }    

    public Book(Number id, String name, String isbn)
    {
        this();
        this.id = id.longValue();
        this.name = name;
        this.isbn = isbn;
    }
    
    public Book(Number id, String name, String isbn, Author author)
    {
        this(id, name, isbn);
        this.author = author;
    }

    public Long getId()
    {
        return id;
    }
    
    public void setId(Long id)
    {
        this.id = id;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getIsbn()
    {
        return isbn;
    }
    
    public void setIsbn(String isbn)
    {
        this.isbn = isbn;
    }
    
    public Author getAuthor()
    {
        return author;
    }
    
    public void setAuthor(Author author)
    {
        this.author = author;
    }
    
    @Override
    public String toString()
    {
        return "Book [id=" + id + ", name=" + name + ", isbn=" + isbn + ", author=" + author + "]";
    }
    
}
