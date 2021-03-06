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
package net.sf.jkniv.whinstone.jdbc.domain.acme;

import net.sf.jkniv.whinstone.types.BooleanCharType;
import net.sf.jkniv.whinstone.types.Converter;

public class FlatBook
{
    private Long   id;
    private String name;
    private String isbn;
    private String author;
    private Long authorId;
    @Converter(converter = BooleanCharType.class,pattern = "Y|N")
    private boolean inStock;
    
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
    
    public String getAuthor()
    {
        return author;
    }
    
    public void setAuthor(String author)
    {
        this.author = author;
    }
    
    public void setAuthorId(Long authorId)
    {
        this.authorId = authorId;
    }
    
    public Long getAuthorId()
    {
        return authorId;
    }
    
    public boolean isInStock()
    {
        return inStock;
    }

    public void setInStock(boolean inStock)
    {
        this.inStock = inStock;
    }

    @Override
    public String toString()
    {
        return "Book [id=" + id + ", name=" + name + ", isbn=" + isbn + ", author =" + author + "]";
    }
    
}
