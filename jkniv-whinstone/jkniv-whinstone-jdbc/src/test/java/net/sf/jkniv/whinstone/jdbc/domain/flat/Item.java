package net.sf.jkniv.whinstone.jdbc.domain.flat;

public class Item
{
    private Long    id;
    private String  name;
    private Integer code;
    private Float   price;
    
    public Long getId()
    {
        return id;
    }
    
    public void setId(Long id)
    {
        this.id = id;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public Integer getCode()
    {
        return code;
    }
    
    public void setCode(Integer code)
    {
        this.code = code;
    }
    
    public Float getPrice()
    {
        return price;
    }
    
    public void setPrice(Float price)
    {
        this.price = price;
    }
    
    @Override
    public String toString()
    {
        return "Item [id=" + id + ", name=" + name + ", code=" + code + ", price=" + price + "]";
    }
    
}
