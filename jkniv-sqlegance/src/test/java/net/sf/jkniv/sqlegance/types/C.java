package net.sf.jkniv.sqlegance.types;

public class C
{
    private String name;
    
    @Converter(pattern = "1|0")
    private Boolean makeMeTrue;
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setMakeMeTrue(Boolean makeMeTrue)
    {
        this.makeMeTrue = makeMeTrue;
    }
    
    public Boolean getMakeMeTrue()
    {
        return makeMeTrue;
    }
}
