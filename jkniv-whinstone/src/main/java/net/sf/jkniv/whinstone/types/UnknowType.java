package net.sf.jkniv.whinstone.types;

public class UnknowType implements ColumnType
{
    private final static UnknowType INSTANCE = new UnknowType();
    
    private UnknowType()
    {
    }

    public static ColumnType getInstance()
    {
        return INSTANCE;
    }
    
    @Override
    public String name()
    {
        return "UNKNOW";
    }
    
    @Override
    public int value()
    {
        return -1;
    }
    
    @Override
    public boolean isBinary()
    {
        return false;
    }
    
    @Override
    public boolean isBlob()
    {
        return false;
    }
    
    @Override
    public boolean isClob()
    {
        return false;
    }
    
    @Override
    public boolean isDate()
    {
        return false;
    }
    
    @Override
    public boolean isTimestamp()
    {
        return false;
    }
    
    @Override
    public boolean isTime()
    {
        return false;
    }

    @Override
    public String toString()
    {
        return "UnknowType [name=" + name() + ", value=" + value() + ", isBinary=" + isBinary() + ", isBlob="
                + isBlob() + ", isClob=" + isClob() + ", isDate=" + isDate() + ", isTimestamp=" + isTimestamp()
                + ", isTime=" + isTime() + "]";
    }

}
