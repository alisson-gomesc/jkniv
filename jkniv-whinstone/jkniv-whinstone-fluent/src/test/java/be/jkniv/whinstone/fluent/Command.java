package be.jkniv.whinstone.fluent;

public class Command
{
    private Object[] args;
    
    public Object[] getRawArguments()
    {
        return this.args;
    }
    
    public boolean hasArgs() {
        return this.args != null;
    }
}
