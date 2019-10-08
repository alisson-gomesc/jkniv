package net.sf.jkniv.whinstone.cassandra.model;

public class ColorFlat
{
    private String name;
    private Short  code;
    private String priority;
    
    public ColorFlat()
    {
        this(null, null);
    }
    
    public ColorFlat(String name, String priority)
    {
        this.name = name;
        this.priority = priority;
        this.code = 255;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public Short getCode()
    {
        return code;
    }
    
    public void setCode(Short code)
    {
        this.code = code;
    }
    
    public String getPriority()
    {
        return priority;
    }
    
    public void setPriority(String priority)
    {
        this.priority = priority;
    }
    
    @Override
    public String toString()
    {
        return "Color [name=" + name + ", code=" + code + ", priority=" + priority + "]";
    }
    
}
