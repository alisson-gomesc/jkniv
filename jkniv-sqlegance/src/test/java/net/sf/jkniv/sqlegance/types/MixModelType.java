package net.sf.jkniv.sqlegance.types;

import net.sf.jkniv.sqlegance.types.Converter;

public class MixModelType
{
    private boolean active;
    
    @Converter(pattern = "1")
    public boolean getActive() 
    {
        return this.active;
    }
    
    public void setActive(boolean active)
    {
        this.active = active;
    }
}
