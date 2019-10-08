package net.sf.jkniv.domain.orm;

import java.util.Date;

import org.junit.Before;

public class Animal
{
    private String id;
    private String name;
    private Date   updateAt;
    
    public String getId()
    {
        return id;
    }
    
    public void setId(String id)
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
    
    public Date getUpdateAt()
    {
        return updateAt;
    }
    
    @Before
    public void setUpdateAt(Date updateAt)
    {
        this.updateAt = updateAt;
    }
    
    public String sound()
    {
        return "argh argh";
    }
    
    @Override
    public String toString()
    {
        return "Employee [id=" + id + ", name=" + name + ", updateAt=" + updateAt + "]";
    }
    
}
