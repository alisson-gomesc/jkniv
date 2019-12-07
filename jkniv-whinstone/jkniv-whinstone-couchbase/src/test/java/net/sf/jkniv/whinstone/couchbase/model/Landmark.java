package net.sf.jkniv.whinstone.couchbase.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(
{ "activity", "address", "alt", "city", "content", "country", "directions", "email", "geo", "hours", "id", "image",
        "name", "phone", "price", "state", "title", "tollfree", "type", "url" })
public class Landmark
{
    
    @JsonProperty("activity")
    private String              activity;
    @JsonProperty("address")
    private String              address;
    @JsonProperty("alt")
    private Double              alt;
    @JsonProperty("city")
    private String              city;
    @JsonProperty("content")
    private String              content;
    @JsonProperty("country")
    private String              country;
    @JsonProperty("directions")
    private String              directions;
    @JsonProperty("email")
    private Object              email;
    @JsonProperty("geo")
    private Geo                 geo;
    @JsonProperty("hours")
    private String              hours;
    @JsonProperty("id")
    private Integer             id;
    @JsonProperty("image")
    private Object              image;
    @JsonProperty("name")
    private String              name;
    @JsonProperty("phone")
    private String              phone;
    @JsonProperty("price")
    private String              price;
    @JsonProperty("state")
    private String              state;
    @JsonProperty("title")
    private String              title;
    @JsonProperty("tollfree")
    private String              tollfree;
    @JsonProperty("type")
    private String              type;
    @JsonProperty("url")
    private String              url;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    
    @JsonProperty("activity")
    public String getActivity()
    {
        return activity;
    }
    
    @JsonProperty("activity")
    public void setActivity(String activity)
    {
        this.activity = activity;
    }
    
    @JsonProperty("address")
    public String getAddress()
    {
        return address;
    }
    
    @JsonProperty("address")
    public void setAddress(String address)
    {
        this.address = address;
    }
    
    @JsonProperty("alt")
    public Double getAlt()
    {
        return alt;
    }
    
    @JsonProperty("alt")
    public void setAlt(Double alt)
    {
        this.alt = alt;
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
    
    @JsonProperty("content")
    public String getContent()
    {
        return content;
    }
    
    @JsonProperty("content")
    public void setContent(String content)
    {
        this.content = content;
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
    
    @JsonProperty("directions")
    public String getDirections()
    {
        return directions;
    }
    
    @JsonProperty("directions")
    public void setDirections(String directions)
    {
        this.directions = directions;
    }
    
    @JsonProperty("email")
    public Object getEmail()
    {
        return email;
    }
    
    @JsonProperty("email")
    public void setEmail(Object email)
    {
        this.email = email;
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
    
    @JsonProperty("hours")
    public String getHours()
    {
        return hours;
    }
    
    @JsonProperty("hours")
    public void setHours(String hours)
    {
        this.hours = hours;
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
    
    @JsonProperty("image")
    public Object getImage()
    {
        return image;
    }
    
    @JsonProperty("image")
    public void setImage(Object image)
    {
        this.image = image;
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
    
    @JsonProperty("phone")
    public String getPhone()
    {
        return phone;
    }
    
    @JsonProperty("phone")
    public void setPhone(String phone)
    {
        this.phone = phone;
    }
    
    @JsonProperty("price")
    public String getPrice()
    {
        return price;
    }
    
    @JsonProperty("price")
    public void setPrice(String price)
    {
        this.price = price;
    }
    
    @JsonProperty("state")
    public String getState()
    {
        return state;
    }
    
    @JsonProperty("state")
    public void setState(String state)
    {
        this.state = state;
    }
    
    @JsonProperty("title")
    public String getTitle()
    {
        return title;
    }
    
    @JsonProperty("title")
    public void setTitle(String title)
    {
        this.title = title;
    }
    
    @JsonProperty("tollfree")
    public String getTollfree()
    {
        return tollfree;
    }
    
    @JsonProperty("tollfree")
    public void setTollfree(String tollfree)
    {
        this.tollfree = tollfree;
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
    
    @JsonProperty("url")
    public String getUrl()
    {
        return url;
    }
    
    @JsonProperty("url")
    public void setUrl(String url)
    {
        this.url = url;
    }
}
