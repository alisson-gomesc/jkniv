package com.acme.domain.flat;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

//@Entity
//@Table(name = "JPA_COLOR")
public class Color
{
    //@Id
    //@GeneratedValue
    private Long       id;
    private String       name;
    private int          code;
    //private String       priority;
    //@Transient
    private List<String> priorities;
    
    public Color()
    {
        this(null, 0, null);
    }
    
    public Color(String name, Number code, String priority)
    {
        this.name = name;
        this.code = code.intValue();
        this.priorities = new ArrayList<String>();
        this.priorities.add(priority);
    }
    
    public void setId(Long id)
    {
        this.id = id;
    }
    
    public Long getId()
    {
        return id;
    }

    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public int getCode()
    {
        return code;
    }
    
    public void setCode(int code)
    {
        this.code = code;
    }
    
    public List<String> getPriorities()
    {
        return priorities;
    }
        
//    
//    public String getPriority()
//    {
//        return priority;
//    }
//    
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
