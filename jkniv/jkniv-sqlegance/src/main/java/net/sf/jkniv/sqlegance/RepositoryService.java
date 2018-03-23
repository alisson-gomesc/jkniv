package net.sf.jkniv.sqlegance;

import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import net.sf.jkniv.sqlegance.spi.RepositoryFactory;

public class RepositoryService
{
    private static RepositoryService         service;
    private ServiceLoader<RepositoryFactory> loader;
    
    private RepositoryService()
    {
        loader = ServiceLoader.load(RepositoryFactory.class);
    }
    
    public static synchronized RepositoryService getInstance()
    {
        if (service == null)
        {
            service = new RepositoryService();
        }
        return service;
    }

    public RepositoryFactory lookup(RepositoryType type)
    {
        return lookup(type.name());
    }
    
    public RepositoryFactory lookup(String type)
    {
        RepositoryType typed = RepositoryType.get(type);
        RepositoryFactory factory = null;
        
        try
        {
            Iterator<RepositoryFactory> factories = loader.iterator();
            while (factory == null && factories.hasNext())
            {
                RepositoryFactory f = factories.next();
                if (f != null && f.getType() == typed)
                    factory = f;
            }
        }
        catch (ServiceConfigurationError serviceError)
        {
            factory = null;
            serviceError.printStackTrace();
        }
        return factory;
    }
}
