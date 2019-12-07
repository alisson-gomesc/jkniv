package net.sf.jkniv.whinstone.couchbase.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(
{ "accuracy", "alt", "lat", "lon" })
public class Geo
{
    @JsonProperty("accuracy")
    private String  accuracy;
    @JsonProperty("alt")
    private Integer alt;
    @JsonProperty("lat")
    private Double  lat;
    @JsonProperty("lon")
    private Double  lon;
    
    @JsonProperty("accuracy")
    public String getAccuracy()
    {
        return accuracy;
    }
    
    @JsonProperty("accuracy")
    public void setAccuracy(String accuracy)
    {
        this.accuracy = accuracy;
    }
    
    @JsonProperty("alt")
    public Integer getAlt()
    {
        return alt;
    }
    
    @JsonProperty("alt")
    public void setAlt(Integer alt)
    {
        this.alt = alt;
    }
    
    @JsonProperty("lat")
    public Double getLat()
    {
        return lat;
    }
    
    @JsonProperty("lat")
    public void setLat(Double lat)
    {
        this.lat = lat;
    }
    
    @JsonProperty("lon")
    public Double getLon()
    {
        return lon;
    }
    
    @JsonProperty("lon")
    public void setLon(Double lon)
    {
        this.lon = lon;
    }
}
