package net.sf.jkniv.whinstone.domain.flat;

public class Car
{
    private String name;
    private String model;
    private int doors;
    
    public Car(String name, String model, int doors)
    {
        this.name = name;
        this.model = model;
        this.doors = doors;
    }
    
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public String getModel()
    {
        return model;
    }
    public void setModel(String model)
    {
        this.model = model;
    }
    public int getDoors()
    {
        return doors;
    }
    public void setDoors(int doors)
    {
        this.doors = doors;
    }
    
    
}
