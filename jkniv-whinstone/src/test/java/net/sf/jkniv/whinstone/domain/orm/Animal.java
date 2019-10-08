package net.sf.jkniv.whinstone.domain.orm;

import java.util.Date;

import net.sf.jkniv.whinstone.CallbackScope;
import net.sf.jkniv.whinstone.PreCallBack;

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
    
    public void setUpdateAt(Date updateAt)
    {
        this.updateAt = updateAt;
    }

    @PreCallBack(scope=CallbackScope.UPDATE)
    public void setUpdateAt()
    {
        this.updateAt = new Date();
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
