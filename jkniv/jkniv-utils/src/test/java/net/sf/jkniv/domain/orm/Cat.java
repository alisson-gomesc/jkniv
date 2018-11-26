package net.sf.jkniv.domain.orm;

public class Cat extends Animal
{
    @Override
    public String sound()
    {
        return "miau miau";
    }
    

    @Deprecated
    public void run()
    {
        System.out.println("Cat is running");
    }
}
