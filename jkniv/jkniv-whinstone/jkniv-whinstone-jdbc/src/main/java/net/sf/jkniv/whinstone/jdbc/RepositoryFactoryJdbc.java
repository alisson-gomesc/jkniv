package net.sf.jkniv.whinstone.jdbc;

import java.util.Properties;

import net.sf.jkniv.sqlegance.RepositoryType;
import net.sf.jkniv.sqlegance.SqlContext;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.spi.RepositoryFactory;

public class RepositoryFactoryJdbc implements RepositoryFactory
{
    
    @Override
    public Repository newInstance()
    {
        return new RepositoryJdbc();
    }
    
    @Override
    public Repository newInstance(Properties props)
    {
        return new RepositoryJdbc(props);
    }
    
    @Override
    public Repository newInstance(Properties props, SqlContext sqlContext)
    {
        return new RepositoryJdbc(props, sqlContext);
    }
    
    @Override
    public Repository newInstance(String sqlContext)
    {
        return new RepositoryJdbc(sqlContext);
    }
    
    @Override
    public Repository newInstance(SqlContext sqlContext)
    {
        return new RepositoryJdbc(sqlContext);
    }
    
    @Override
    public RepositoryType getType()
    {
        return RepositoryType.JDBC;
    }
    
}
