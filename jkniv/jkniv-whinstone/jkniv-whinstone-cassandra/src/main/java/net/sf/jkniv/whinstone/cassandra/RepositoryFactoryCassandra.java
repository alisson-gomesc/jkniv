package net.sf.jkniv.whinstone.cassandra;

import java.util.Properties;

import net.sf.jkniv.sqlegance.Repository;
import net.sf.jkniv.sqlegance.RepositoryType;
import net.sf.jkniv.sqlegance.SqlContext;
import net.sf.jkniv.sqlegance.spi.RepositoryFactory;

public class RepositoryFactoryCassandra implements RepositoryFactory
{
    
    @Override
    public Repository newInstance()
    {
        return new RepositoryCassandra();
    }
    
    @Override
    public Repository newInstance(Properties props)
    {
        return new RepositoryCassandra(props);
    }
    
    @Override
    public Repository newInstance(Properties props, SqlContext sqlContext)
    {
        return new RepositoryCassandra(props, sqlContext);
    }
    
    @Override
    public Repository newInstance(String sqlContext)
    {
        throw new UnsupportedOperationException("Cassandra Repository doens't supports (String) constructor yet!");
        //return new RepositoryCassandra(sqlContext);
    }
    
    @Override
    public Repository newInstance(SqlContext sqlContext)
    {
        throw new UnsupportedOperationException("Cassandra Repository doens't supports (SqlContext) constructor yet!");
        //return new RepositoryCassandra(sqlContext);
    }
    
    @Override
    public RepositoryType getType()
    {
        return RepositoryType.CASSANDRA;
    }
    
}
