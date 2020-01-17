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
