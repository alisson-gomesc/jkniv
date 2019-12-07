package net.sf.jkniv.whinstone.couchbase.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(
{ "address", "alias", "checkin", "checkout", "city", "country", "description", "directions", "email", "fax",
        "free_breakfast", "free_internet", "free_parking", "geo", "id", "name", "pets_ok", "phone", "price",
        "public_likes", "reviews", "state", "title", "tollfree", "type", "url", "vacancy" })
public class Hotel
{
    
    @JsonProperty("address")
    private String              address;
    @JsonProperty("alias")
    private Object              alias;
    @JsonProperty("checkin")
    private String              checkin;
    @JsonProperty("checkout")
    private String              checkout;
    @JsonProperty("city")
    private String              city;
    @JsonProperty("country")
    private String              country;
    @JsonProperty("description")
    private String              description;
    @JsonProperty("directions")
    private String              directions;
    @JsonProperty("email")
    private String email;
    @JsonProperty("fax")
    private String              fax;
    @JsonProperty("free_breakfast")
    private Boolean             freeBreakfast;
    @JsonProperty("free_internet")
    private Boolean             freeInternet;
    @JsonProperty("free_parking")
    private Boolean             freeParking;
    @JsonProperty("geo")
    private Geo                 geo;
    @JsonProperty("id")
    private Integer             id;
    @JsonProperty("name")
    private String              name;
    @JsonProperty("pets_ok")
    private Boolean             petsOk;
    @JsonProperty("phone")
    private Object              phone;
    @JsonProperty("price")
    private String              price;
    @JsonProperty("public_likes")
    private List<String>        publicLikes          = null;
    @JsonProperty("reviews")
    private List<Review>        reviews              = null;
    @JsonProperty("state")
    private String              state;
    @JsonProperty("title")
    private String              title;
    @JsonProperty("tollfree")
    private String tollfree;
    @JsonProperty("type")
    private String              type;
    @JsonProperty("url")
    private String              url;
    @JsonProperty("vacancy")
    private Boolean             vacancy;
    
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
    
    @JsonProperty("alias")
    public Object getAlias()
    {
        return alias;
    }
    
    @JsonProperty("alias")
    public void setAlias(Object alias)
    {
        this.alias = alias;
    }
    
    @JsonProperty("checkin")
    public String getCheckin()
    {
        return checkin;
    }
    
    @JsonProperty("checkin")
    public void setCheckin(String checkin)
    {
        this.checkin = checkin;
    }
    
    @JsonProperty("checkout")
    public String getCheckout()
    {
        return checkout;
    }
    
    @JsonProperty("checkout")
    public void setCheckout(String checkout)
    {
        this.checkout = checkout;
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
    
    @JsonProperty("description")
    public String getDescription()
    {
        return description;
    }
    
    @JsonProperty("description")
    public void setDescription(String description)
    {
        this.description = description;
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
    public String getEmail()
    {
        return email;
    }
    
    @JsonProperty("email")
    public void setEmail(String email)
    {
        this.email = email;
    }
    
    @JsonProperty("fax")
    public String getFax()
    {
        return fax;
    }
    
    @JsonProperty("fax")
    public void setFax(String fax)
    {
        this.fax = fax;
    }
    
    @JsonProperty("free_breakfast")
    public Boolean getFreeBreakfast()
    {
        return freeBreakfast;
    }
    
    @JsonProperty("free_breakfast")
    public void setFreeBreakfast(Boolean freeBreakfast)
    {
        this.freeBreakfast = freeBreakfast;
    }
    
    @JsonProperty("free_internet")
    public Boolean getFreeInternet()
    {
        return freeInternet;
    }
    
    @JsonProperty("free_internet")
    public void setFreeInternet(Boolean freeInternet)
    {
        this.freeInternet = freeInternet;
    }
    
    @JsonProperty("free_parking")
    public Boolean getFreeParking()
    {
        return freeParking;
    }
    
    @JsonProperty("free_parking")
    public void setFreeParking(Boolean freeParking)
    {
        this.freeParking = freeParking;
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
    
    @JsonProperty("pets_ok")
    public Boolean getPetsOk()
    {
        return petsOk;
    }
    
    @JsonProperty("pets_ok")
    public void setPetsOk(Boolean petsOk)
    {
        this.petsOk = petsOk;
    }
    
    @JsonProperty("phone")
    public Object getPhone()
    {
        return phone;
    }
    
    @JsonProperty("phone")
    public void setPhone(Object phone)
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
    
    @JsonProperty("public_likes")
    public List<String> getPublicLikes()
    {
        return publicLikes;
    }
    
    @JsonProperty("public_likes")
    public void setPublicLikes(List<String> publicLikes)
    {
        this.publicLikes = publicLikes;
    }
    
    @JsonProperty("reviews")
    public List<Review> getReviews()
    {
        return reviews;
    }
    
    @JsonProperty("reviews")
    public void setReviews(List<Review> reviews)
    {
        this.reviews = reviews;
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
    
    @JsonProperty("vacancy")
    public Boolean getVacancy()
    {
        return vacancy;
    }
    
    @JsonProperty("vacancy")
    public void setVacancy(Boolean vacancy)
    {
        this.vacancy = vacancy;
    }
}
