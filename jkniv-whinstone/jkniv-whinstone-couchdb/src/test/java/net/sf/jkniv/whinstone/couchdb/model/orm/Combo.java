package net.sf.jkniv.whinstone.couchdb.model.orm;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Combo
{
    
    private String key;
    private Integer value;
    
    @JsonCreator
    public Combo(@JsonProperty("key") String key, @JsonProperty("value") Integer value)
    {
        super();
        this.key = key;
        this.value = value;
    }
    
    
    public String getKey()
    {
        return key;
    }
    
    public Integer getValue()
    {
        return value;
    }
}
