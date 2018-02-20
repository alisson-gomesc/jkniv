package net.sf.jkniv.experimental;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.asserts.Assertable;
import net.sf.jkniv.asserts.AssertsFactory;

class SimpleNameRegistry<T>
{
    private static final Logger     LOG        = LoggerFactory.getLogger(SimpleNameRegistry.class);
    private static final Assertable notNull    = AssertsFactory.getNotNull();
    private Map<String, T>     rawTypes   = new HashMap<String, T>();
    private boolean shortNameEnable;
    
    public SimpleNameRegistry()
    {
        this(true);
    }
    
    public SimpleNameRegistry(boolean shortNameEnable)
    {
        this.rawTypes = new HashMap<String, T>();
        this.shortNameEnable = shortNameEnable;
    }

    /**
     * Provider a way to reference a object type with a short name
     * @param o object to registry
     */
    public void registry(String shortName, T o)
    {
        notNull.verify(shortName, o);
        put(shortName, o);
    }

    /**
     * Provider a way to reference a object type with a short name
     * @param o object to registry
     */
    public void registry(T o)
    {
        notNull.verify(o);
        put(getSimpleName(o.toString()), o);
    }
    
    public T getType(String simpleName)
    {
        return rawTypes.get(simpleName);
    }

    public Class<?> getTypeAsClass(String simpleName)
    {
        return forName(rawTypes.get(simpleName).toString());
    }

    private void put(String shortName, T o)
    {
        rawTypes.put(shortName, o);
    }
/*    
    private void add(String key, T o)
    {
        if (key.contains(".") && shortNameEnable)
        {
            String shortName = getSimpleName(key);
            if (rawTypes.get(shortName) == null)
                rawTypes.put(shortName, o);
            else
            {
                LOG.warn("There is duplicate short name [{}] for statement [{}], use fully name to recover it", shortName, key);
                rawTypes.put(shortName, new Duplicate(o.getName(), LanguageType.NATIVE));
            }
        }
        ISql old = rawTypes.put(key, o);
        if (old != null)
            LOG.warn("The object [{}] from [{}] was replaced from resource [{}]", key, old.getResourceName(), o.getResourceName());
    }
*/
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
    
    private String getSimpleName(String key)
    {
        int i = key.lastIndexOf('.');
        if ( i < 0)
             return key;

        return key.substring(i, key.length());
    }
}
