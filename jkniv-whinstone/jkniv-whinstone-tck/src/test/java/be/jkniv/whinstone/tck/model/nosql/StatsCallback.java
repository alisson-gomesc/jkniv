package be.jkniv.whinstone.tck.model.nosql;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import net.sf.jkniv.whinstone.CallbackScope;
import net.sf.jkniv.whinstone.PreCallBack;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatsCallback
{
    @JsonProperty("_id")
    private String id;
    @JsonProperty("_rev")
    private String rev;
    @JsonProperty("name")
    private String name;
    @JsonProperty("totalAdd")
    private int    totalAdd;
    @JsonProperty("totalUpdate")
    private int    totalUpdate;
    @JsonProperty("totalRemove")
    private int    totalRemove;
    @JsonProperty("totalSelect")
    private int    totalSelect;

    public StatsCallback()
    {
        this.name = "default";
        this.totalAdd = 0;
        this.totalRemove = 0;
        this.totalSelect = 0;
        this.totalUpdate = 0;
    }
    
    public String getId()
    {
        return id;
    }
    
    public void setId(String id)
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
