package net.sf.jkniv.whinstone.rs;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.asserts.Assertable;
import net.sf.jkniv.asserts.AssertsFactory;
import net.sf.jkniv.sqlegance.DefaultClassLoader;

class SimpleNameRegistry
{
    private static final Logger     LOG        = LoggerFactory.getLogger(SimpleNameRegistry.class);
    private static final Assertable notNull    = AssertsFactory.getNotNull();
    private Map<String, String>     rawTypes   = new HashMap<String, String>();
    private Map<String, Class<?>>   classTypes = new HashMap<String, Class<?>>();
    private boolean                 canRegistryString;
    
    public SimpleNameRegistry()
    {
        this.rawTypes = new HashMap<String, String>();
        this.classTypes = new HashMap<String, Class<?>>();
        this.canRegistryString = true;
    }
    
    /**
     * Provider a way to reference a class type with a short name
     * @param fqn fully qualified name for class
     */
    public boolean registry(String fqn)
    {
        notNull.verify(fqn);
        boolean registered = true;
        if (canRegistryString)
            rawTypes.put(getSimpleName(fqn), fqn);
        else
        {
            Class<?> classType = forName(fqn);
            if (classType != null)
                classTypes.put(classType.getSimpleName(), classType);
            else registered = false;
        }
        return registered;
    }
    
    public Class<?> getType(String simpleName)
    {
        if (classTypes.isEmpty())
            loadTypes();
        
        return classTypes.get(simpleName);
    }

    public InputStream getResourceAsStream(String simpleName)
    {
        String resource = rawTypes.get(simpleName);
        InputStream is = DefaultClassLoader.getResourceAsStream("/br/com/rwit/clsiv/web/reports/TempoMedioRotas.jasper");
        return is;
    }

    public URL getResource(String simpleName)
    {
        String resource = rawTypes.get(simpleName);
        URL url = DefaultClassLoader.getResource(resource);
        return url;
    }

    public void cleanup(){
        rawTypes.clear();
        classTypes.clear();

    }
    /**
     * Transform the String type at Class types
     */
    private void loadTypes()
    {
        for (Entry<String, String> entry : rawTypes.entrySet())
        {
            classTypes.put(entry.getKey(), forName(entry.getValue()));
        }
        canRegistryString = false;
    }
    
    /**
     * Returns the Class object associated with the class with the given string name
     * @param fdn fully qualified name for class
     * @return the Class object for the class with the specified name.
     */
    private Class<?> forName(String fdn)
    {
        try
        {
            return Class.forName(fdn);
        }
        catch (ClassNotFoundException e)
        {
            LOG.error("Cannot registry simple name for class [{}]", fdn);
        }
        return null;
    }
    
    private String getSimpleName(String fqn)
    {
        int i = fqn.lastIndexOf('.');
        if ( i < 0)
             return fqn;

        return fqn.substring(i+1, fqn.length());
    }
}
