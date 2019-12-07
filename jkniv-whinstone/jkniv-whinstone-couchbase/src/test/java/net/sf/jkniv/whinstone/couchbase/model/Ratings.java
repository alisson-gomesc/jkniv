package net.sf.jkniv.whinstone.couchbase.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(
{ "Cleanliness", "Location", "Overall", "Rooms", "Service", "Sleep Quality", "Value" })
public class Ratings
{
    
    @JsonProperty("Cleanliness")
    private Integer             cleanliness;
    @JsonProperty("Location")
    private Integer             location;
    @JsonProperty("Overall")
    private Integer             overall;
    @JsonProperty("Rooms")
    private Integer             rooms;
    @JsonProperty("Service")
    private Integer             service;
    @JsonProperty("Sleep Quality")
    private Integer             sleepQuality;
    @JsonProperty("Value")
    private Integer             value;
    
    @JsonProperty("Cleanliness")
    public Integer getCleanliness()
    {
        return cleanliness;
    }
    
    @JsonProperty("Cleanliness")
    public void setCleanliness(Integer cleanliness)
    {
        this.cleanliness = cleanliness;
    }
    
    @JsonProperty("Location")
    public Integer getLocation()
    {
        return location;
    }
    
    @JsonProperty("Location")
    public void setLocation(Integer location)
    {
        this.location = location;
    }
    
    @JsonProperty("Overall")
    public Integer getOverall()
    {
        return overall;
    }
    
    @JsonProperty("Overall")
    public void setOverall(Integer overall)
    {
        this.overall = overall;
    }
    
    @JsonProperty("Rooms")
    public Integer getRooms()
    {
        return rooms;
    }
    
    @JsonProperty("Rooms")
    public void setRooms(Integer rooms)
    {
        this.rooms = rooms;
    }
    
    @JsonProperty("Service")
    public Integer getService()
    {
        return service;
    }
    
    @JsonProperty("Service")
    public void setService(Integer service)
    {
        this.service = service;
    }
    
    @JsonProperty("Sleep Quality")
    public Integer getSleepQuality()
    {
        return sleepQuality;
    }
    
    @JsonProperty("Sleep Quality")
    public void setSleepQuality(Integer sleepQuality)
    {
        this.sleepQuality = sleepQuality;
    }
    
    @JsonProperty("Value")
    public Integer getValue()
    {
        return value;
    }
    
    @JsonProperty("Value")
    public void setValue(Integer value)
    {
        this.value = value;
    }
    
    
}
