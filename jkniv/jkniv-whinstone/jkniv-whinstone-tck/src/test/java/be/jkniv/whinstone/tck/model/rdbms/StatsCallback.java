package be.jkniv.whinstone.tck.model.rdbms;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import net.sf.jkniv.whinstone.CallbackScope;
import net.sf.jkniv.whinstone.PreCallBack;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatsCallback
{
    private Long id;
    private String rev;
    private String name;
    private int    totalAdd;
    private int    totalUpdate;
    private int    totalRemove;
    private int    totalSelect;
    
    public StatsCallback()
    {
        this.name = "default";
        this.totalAdd = 0;
        this.totalRemove = 0;
        this.totalSelect = 0;
        this.totalUpdate = 0;
    }
    
    public Long getId()
    {
        return id;
    }
    
    public void setId(Long id)
    {
        this.id = id;
    }
    
    public String getRev()
    {
        return rev;
    }
    
    public void setRev(String rev)
    {
        this.rev = rev;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getName()
    {
        return name;
    }
    
    @PreCallBack(scope = CallbackScope.ADD)
    @JsonIgnore
    public void incrementAdd()
    {
        this.totalAdd++;
    }
    
    @PreCallBack(scope = CallbackScope.SELECT)
    @JsonIgnore
    public void incrementSelect()
    {
        this.totalSelect++;
    }
    
    @PreCallBack(scope = CallbackScope.UPDATE)
    @JsonIgnore
    public void incrementUpdate()
    {
        this.totalUpdate++;
    }
    
    @PreCallBack(scope = CallbackScope.REMOVE)
    @JsonIgnore
    public void incrementRemove()
    {
        this.totalRemove++;
    }
    
    public int getTotalAdd()
    {
        return this.totalAdd;
    }
    
    public int getTotalUpdate()
    {
        return totalUpdate;
    }
    
    public int getTotalRemove()
    {
        return totalRemove;
    }
    
    public int getTotalSelect()
    {
        return totalSelect;
    }
    
    @Override
    public String toString()
    {
        return "StatsCallback [name=" + name + ", totalAdd=" + totalAdd + ", totalUpdate=" + totalUpdate
                + ", totalRemove=" + totalRemove + ", totalSelect=" + totalSelect + "]";
    }
    
}
