package net.sf.jkniv.reflect.beans;

class C
{
    private String name;
    private Boolean active;
    private boolean cancel;
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }

    public Boolean getActive()
    {
        return active;
    }
    
    public void setActive(Boolean active)
    {
        this.active = active;
    }

    public boolean isCancel()
    {
        return cancel;
    }

    public void setCancel(boolean cancel)
    {
        this.cancel = cancel;
    }
}
