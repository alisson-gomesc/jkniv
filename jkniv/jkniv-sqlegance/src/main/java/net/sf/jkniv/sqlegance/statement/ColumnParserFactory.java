package net.sf.jkniv.sqlegance.statement;

public class ColumnParserFactory
{
    private static final ColumnParser INSTANCE = new SimpleColumnParser();
    
    public static ColumnParser getInstance()
    {
        return INSTANCE;
    }
}
