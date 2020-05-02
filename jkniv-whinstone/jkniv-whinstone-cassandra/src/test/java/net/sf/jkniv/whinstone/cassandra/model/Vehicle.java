package net.sf.jkniv.whinstone.cassandra.model;

import java.util.List;

public class Vehicle
{
    private String      name;
    private String      plate;
    private String      color;
    private List<String> alarms;
    private Frequency    frequency;
    
    public Vehicle()
    {
    }

    public Vehicle(String plate)
    {
        this();
        this.plate = plate;
    }

    public Vehicle(String plate, String name)
    {
        this();
        this.plate = plate;
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getPlate()
    {
        return plate;
    }
    
    public void setPlate(String plate)
    {
        this.plate = plate;
    }
    
    public String getColor()
    {
        return color;
    }
    
    public void setColor(String color)
    {
        this.color = color;
    }
    
    public List<String> getAlarms()
    {
        return alarms;
    }
    
    public void setAlarms(List<String> alarms)
    {
        this.alarms = alarms;
    }
    
    public Frequency getFrequency()
    {
        return frequency;
    }
    
    public void setFrequency(Frequency frequency)
    {
        this.frequency = frequency;
    }
    
    @Override
    public String toString()
    {
        return "Vehicle [name=" + name + ", plate=" + plate + ", color=" + color + ", alarms=" + alarms + "]";
    }
    
}
