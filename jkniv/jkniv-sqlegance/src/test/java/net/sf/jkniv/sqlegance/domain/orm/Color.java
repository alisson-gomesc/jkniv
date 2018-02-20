package net.sf.jkniv.sqlegance.domain.orm;

import java.util.ArrayList;
import java.util.List;

public class Color
{
    private String       name;
    private int          code;
    private List<String> priorities;
    
    public Color()
    {
        this(null, 0);
    }
    
    public Color(String name, int code)
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