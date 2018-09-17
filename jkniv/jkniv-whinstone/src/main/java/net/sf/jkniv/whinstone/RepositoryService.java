package net.sf.jkniv.whinstone;

import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.sqlegance.RepositoryConfigException;
import net.sf.jkniv.sqlegance.RepositoryType;
import net.sf.jkniv.whinstone.spi.RepositoryFactory;

public class RepositoryService
{
    private static final Logger LOG = LoggerFactory.getLogger(RepositoryService.class);
    private static RepositoryService         service;
    private ServiceLoader<RepositoryFactory> loader;
    
    private RepositoryService()
    {
        loader = ServiceLoader.load(RepositoryFactory.class);
    }
    
    public static synchronized RepositoryService getInstance()
    {
        if (service == null)
            service = new RepositoryService();
        return service;
    }

    public RepositoryFactory lookup(RepositoryType type)
    {
        return lookup(type.name());
    }
    
    public RepositoryFactory lookup(String type)
    {
        RepositoryFactory factory = null;
        try
        {
            Iterator<RepositoryFactory> factories = loader.iterator();
            while (factory == null && factories.hasNext())
            {
                RepositoryFactory f = factories.next();
                if (f != null && type.equals(f.getType().name()))
                    factory = f;
            }
        }
        catch (ServiceConfigurationError serviceError)
        {
            LOG.error("Unexpected error", serviceError);
            factory = null;
        }
        if (factory == null)
            throw new RepositoryConfigException("RepositoryFactory for ["+type+"] type cannot be found, verify if jar file from repository it's set in classpath");
        
        return factory;
    }
}
