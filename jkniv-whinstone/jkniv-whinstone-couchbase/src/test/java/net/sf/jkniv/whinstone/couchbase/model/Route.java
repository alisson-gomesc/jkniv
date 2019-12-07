package net.sf.jkniv.whinstone.couchbase.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(
{ "airline", "airlineid", "destinationairport", "distance", "equipment", "id", "schedule", "sourceairport", "stops",
        "type" })
public class Route
{
    @JsonProperty("airline")
    private String         airline;
    @JsonProperty("airlineid")
    private String         airlineid;
    @JsonProperty("destinationairport")
    private String         destinationairport;
    @JsonProperty("distance")
    private Double         distance;
    @JsonProperty("equipment")
    private String         equipment;
    @JsonProperty("id")
    private Integer        id;
    @JsonProperty("schedule")
    private List<Schedule> schedule = null;
    @JsonProperty("sourceairport")
    private String         sourceairport;
    @JsonProperty("stops")
    private Integer        stops;
    @JsonProperty("type")
    private String         type;
    
    @JsonProperty("airline")
    public String getAirline()
    {
        return airline;
    }
    
    @JsonProperty("airline")
    public void setAirline(String airline)
    {
        this.airline = airline;
    }
    
    @JsonProperty("airlineid")
    public String getAirlineid()
    {
        return airlineid;
    }
    
    @JsonProperty("airlineid")
    public void setAirlineid(String airlineid)
    {
        this.airlineid = airlineid;
    }
    
    @JsonProperty("destinationairport")
    public String getDestinationairport()
    {
        return destinationairport;
    }
    
    @JsonProperty("destinationairport")
    public void setDestinationairport(String destinationairport)
    {
        this.destinationairport = destinationairport;
    }
    
    @JsonProperty("distance")
    public Double getDistance()
    {
        return distance;
    }
    
    @JsonProperty("distance")
    public void setDistance(Double distance)
    {
        this.distance = distance;
    }
    
    @JsonProperty("equipment")
    public String getEquipment()
    {
        return equipment;
    }
    
    @JsonProperty("equipment")
    public void setEquipment(String equipment)
    {
        this.equipment = equipment;
    }
    
    @JsonProperty("id")
    public Integer getId()
    {
        return id;
    }
    
    @JsonProperty("id")
    public void setId(Integer id)
    {
        this.id = id;
    }
    
    @JsonProperty("schedule")
    public List<Schedule> getSchedule()
    {
        return schedule;
    }
    
    @JsonProperty("schedule")
    public void setSchedule(List<Schedule> schedule)
    {
        this.schedule = schedule;
    }
    
    @JsonProperty("sourceairport")
    public String getSourceairport()
    {
        return sourceairport;
    }
    
    @JsonProperty("sourceairport")
    public void setSourceairport(String sourceairport)
    {
        this.sourceairport = sourceairport;
    }
    
    @JsonProperty("stops")
    public Integer getStops()
    {
        return stops;
    }
    
    @JsonProperty("stops")
    public void setStops(Integer stops)
    {
        this.stops = stops;
    }
    
    @JsonProperty("type")
    public String getType()
    {
        return type;
    }
    
    @JsonProperty("type")
    public void setType(String type)
    {
        this.type = type;
    }
    
}
