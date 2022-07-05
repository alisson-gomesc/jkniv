package net.sf.jkniv.whinstone.couchdb;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JacksonFeature
{
    public static SerializationFeature serializationOf(String featureName)
    {
        SerializationFeature feature = null;
        try 
        {
            feature = SerializationFeature.valueOf(featureName);
        }
        catch(Exception e)
        {
            
        }
        return feature;
        
    }
    
    public static DeserializationFeature deserializationOf(String featureName)
    {
        DeserializationFeature feature = null;
        try 
        {
            feature = DeserializationFeature.valueOf(featureName);
        }
        catch(Exception e)
        {
            
        }
        return feature;        
    }
}
