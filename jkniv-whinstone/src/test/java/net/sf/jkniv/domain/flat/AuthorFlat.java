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
package net.sf.jkniv.domain.flat;

import java.util.ArrayList;
import java.util.List;

import net.sf.jkniv.whinstone.PreCallBack;
import net.sf.jkniv.whinstone.CallbackScope;
import net.sf.jkniv.whinstone.PostCallBack;

public class AuthorFlat
{
    private Long         id;
    private String       author;
    private String       book;
    
    private List<String> callback;
    
    public AuthorFlat()
    {
        this.callback = new ArrayList<String>();
    }
    
    public AuthorFlat(String author, String book)
    {
        this();
        this.author = author;
        this.book = book;
    }
    
    public AuthorFlat(Long id, String author)
    {
        this();
        this.id = id;
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
    
    public String getAuthor()
    {
        return author;
    }
    
    public void setAuthor(String author)
    {
        this.author = author;
    }
    
    public String getBook()
    {
        return book;
    }
    
    public void setBook(String book)
    {
        this.book = book;
    }
    
    public List<String> getCallback()
    {
        return callback;
    }
    
    public void setCallback(List<String> callback)
    {
        this.callback = callback;
    }
    
    @PreCallBack(scope = CallbackScope.ADD)
    public void callMePreAdd()
    {
        this.callback.add("PRE-ADD");
    }
    
    @PreCallBack(scope = CallbackScope.REMOVE)
    public void callMePreRemove()
    {
        this.callback.add("PRE-REMOVE");
    }
    
    @PreCallBack(scope = CallbackScope.SELECT)
    public void callMePreSelect()
    {
        this.callback.add("PRE-SELECT");
    }
    
    @PreCallBack(scope = CallbackScope.UPDATE)
    public void callMePreUpdate()
    {
        this.callback.add("PRE-UPDATE");
    }
    
    @PostCallBack(scope = CallbackScope.ADD)
    public void callMePostAdd()
    {
        this.callback.add("POST-ADD");
    }
    
    @PostCallBack(scope = CallbackScope.REMOVE)
    public void callMePostRemove()
    {
        this.callback.add("POST-REMOVE");
    }
    
    @PostCallBack(scope = CallbackScope.SELECT)
    public void callMePostSelect()
    {
        this.callback.add("POST-SELECT");
    }
    
    @PostCallBack(scope = CallbackScope.UPDATE)
    public void callMePostUpdate()
    {
        this.callback.add("POST-UPDATE");
    }
    
}
