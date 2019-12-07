package net.sf.jkniv.whinstone.couchbase.statement;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class QueryParam
{
    private String  name;
    private boolean quote;

    public QueryParam(String name)
    {
        this(name, false);
    }

    public QueryParam(String name, boolean quote)
    {
        this.name = name;
        this.quote = quote;
    }
    
    public String name()
    {
        return name;
    }
    
    public String getValue(Object v)
    {
        if (quote)
            return  encode("\"" +v.toString()+ "\"") ;
        
        return v.toString();
    }
    
    private String encode(String v)
    {
        try
        {
            return URLEncoder.encode(v,"UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    
}
