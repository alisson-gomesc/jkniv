package net.sf.jkniv.whinstone.couchbase.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(
{ "airportname", "city", "country", "faa", "geo", "icao", "id", "type", "tz" })
public class Airport
{
    
    @JsonProperty("airportname")
    private String  airportname;
    @JsonProperty("city")
    private String  city;
    @JsonProperty("country")
    private String  country;
    @JsonProperty("faa")
    private String  faa;
    @JsonProperty("geo")
    private Geo     geo;
    @JsonProperty("icao")
    private String  icao;
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("type")
    private String  type;
    @JsonProperty("tz")
    private String  tz;
    
    @JsonProperty("airportname")
    public String getAirportname()
    {
        return airportname;
    }
    
    @JsonProperty("airportname")
    public void setAirportname(String airportname)
    {
        this.airportname = airportname;
    }
    
    @JsonProperty("city")
    public String getCity()
    {
        return city;
    }
    
    @JsonProperty("city")
    public void setCity(String city)
    {
        this.city = city;
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
    
    @JsonProperty("faa")
    public String getFaa()
    {
        return faa;
    }
    
    @JsonProperty("faa")
    public void setFaa(String faa)
    {
        this.faa = faa;
    }
    
    @JsonProperty("geo")
    public Geo getGeo()
    {
        return geo;
    }
    
    @JsonProperty("geo")
    public void setGeo(Geo geo)
    {
        this.geo = geo;
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
    
    @JsonProperty("tz")
    public String getTz()
    {
        return tz;
    }
    
    @JsonProperty("tz")
    public void setTz(String tz)
    {
        this.tz = tz;
    }
}
