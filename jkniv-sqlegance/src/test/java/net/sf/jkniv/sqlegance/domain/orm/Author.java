/*
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

import javax.validation.constraints.NotNull;

import net.sf.jkniv.sqlegance.validation.AddValidate;
import net.sf.jkniv.sqlegance.validation.UpdateValidate;

public class Author
{
    private String  name;
    @NotNull(groups = { UpdateValidate.class })
    private String  username;
    private String  password;
    private String  email;

    private String  bio;
    private Address address;
    
    public void setAddress(Address address)
    {
        this.address = address;
    }
    
    public Address getAddress()
    {
        return address;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getName()
    {
        
        return name;
    }
    
    public String getUsername()
    {
        return username;
    }
    
    public void setUsername(String username)
    {
        this.username = username;
    }
    
    public String getPassword()
    {
        return password;
    }
    
    public void setPassword(String password)
    {
        this.password = password;
    }
    
    public String getEmail()
    {
        return email;
    }
    
    public void setEmail(String email)
    {
        this.email = email;
    }
    
    public String getBio()
    {
        return bio;
    }
    
    public void setBio(String bio)
    {
        this.bio = bio;
    }
}
