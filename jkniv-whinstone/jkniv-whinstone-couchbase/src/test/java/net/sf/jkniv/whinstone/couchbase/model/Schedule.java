package net.sf.jkniv.whinstone.couchbase.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(
{ "day", "flight", "utc" })
public class Schedule
{
    
    @JsonProperty("day")
    private Integer             day;
    @JsonProperty("flight")
    private String              flight;
    @JsonProperty("utc")
    private String              utc;
    
    @JsonProperty("day")
    public Integer getDay()
    {
        return day;
    }
    
    @JsonProperty("day")
    public void setDay(Integer day)
    {
        this.day = day;
    }
    
    @JsonProperty("flight")
    public String getFlight()
    {
        return flight;
    }
    
    @JsonProperty("flight")
    public void setFlight(String flight)
    {
        this.flight = flight;
    }
    
    @JsonProperty("utc")
    public String getUtc()
    {
        return utc;
    }
    
    @JsonProperty("utc")
    public void setUtc(String utc)
    {
        this.utc = utc;
    }
    
}
