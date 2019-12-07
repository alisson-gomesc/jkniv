package net.sf.jkniv.whinstone.couchbase.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Airline
{
    @JsonProperty("callsign")
    private String              callsign;
    @JsonProperty("country")
    private String              country;
    @JsonProperty("iata")
    private String              iata;
    @JsonProperty("icao")
    private String              icao;
    @JsonProperty("id")
    private Integer             id;
    @JsonProperty("name")
    private String              name;
    @JsonProperty("type")
    private String              type;
    
    @JsonProperty("callsign")
    public String getCallsign()
    {
        return callsign;
    }
    
    @JsonProperty("callsign")
    public void setCallsign(String callsign)
    {
        this.callsign = callsign;
    }
    
    @JsonProperty("country")
    public String getCountry()
    {
        return country;
    }
    
    @JsonProperty("country")
    public void setCountry(String country)
    {
        this.country = country;
    }
    
    @JsonProperty("iata")
    public String getIata()
    {
        return iata;
    }
    
    @JsonProperty("iata")
    public void setIata(String iata)
    {
        this.iata = iata;
    }
    
    @JsonProperty("icao")
    public String getIcao()
    {
        return icao;
    }
    
    @JsonProperty("icao")
    public void setIcao(String icao)
    {
        this.icao = icao;
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
    
    @JsonProperty("name")
    public String getName()
    {
        return name;
    }
    
    @JsonProperty("name")
    public void setName(String name)
    {
        this.name = name;
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
