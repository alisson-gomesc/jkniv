package net.sf.jkniv.sqlegance.types;

public class MixModelType
{
    private boolean active;
    private int     createdAt;
    @Converter(pattern = "yyyyMMddHHmmss.MMM")
    private String timestamp;
    
    @Converter(pattern = "1", converter = TrueType.class)
    public boolean getActive()
    {
        return this.active;
    }
    
    public void setActive(boolean active)
    {
        this.active = active;
    }
    
    @Converter(pattern = "yyyyMMdd")
    public int getCreatedAt()
    {
        return createdAt;
    }
    
    public void setCreatedAt(int createdAt)
    {
        this.createdAt = createdAt;
    }
    
    public String getTimestamp()
    {
        return timestamp;
    }
    
    public void setTimestamp(String timestamp)
    {
        this.timestamp = timestamp;
    }
}