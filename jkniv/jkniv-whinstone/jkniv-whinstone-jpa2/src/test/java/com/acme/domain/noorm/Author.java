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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import net.sf.jkniv.sqlegance.validation.UpdateValidate;

//import javax.validation.constraints.NotNull;
//import javax.validation.constraints.Size;

public class Author implements Serializable
{
    @NotNull(groups=UpdateValidate.class)
    private Long       id;
    @NotNull(groups=UpdateValidate.class)
    private String     name;
    private List<Book> books;
    
    
    public Author()
    {
        super();
        this.books = new ArrayList<Book>();
    }

    
    public Author(Number id, String name, Number bookId, String bookName, String bookIsbn)
    {
        this();
        this.id = id.longValue();
        this.name = name;
        this.books.add( new Book(bookId, bookName, bookIsbn, this));
        /*
    select a.id as id, a.name as name
    ,b.ID as "books.id", b.ISBN as "books.isbn", b.NAME as "books.name"

         */
    }


    public Author(List<Book> books)
    {
        this.books = books;
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
    
    public List<Book> getBooks()
    {
        return books;
    }
    
    public void setBooks(List<Book> books)
    {
        this.books = books;
    }

    public void setBook(Book book)
    {
        this.books.add(book);
    }

    @Override
    public String toString()
    {
        return "Author [id=" + id + ", name=" + name + ", books=" + (books != null ? books.size() : 0) + "-> " +books+ "]";
    }
    
    
}
