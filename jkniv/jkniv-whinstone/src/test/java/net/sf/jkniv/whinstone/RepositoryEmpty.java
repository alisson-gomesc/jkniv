package net.sf.jkniv.whinstone;

import java.util.List;

import net.sf.jkniv.whinstone.transaction.Transactional;

class RepositoryEmpty implements Repository
{
    @Override
    public <T> T get(Queryable queryable)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> T get(Queryable queryable, Class<T> returnType)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T, R> T get(Queryable queryable, ResultRow<T, R> customResultRow)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> T get(T object)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> T get(Class<T> returnType, Object object)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> T scalar(Queryable queryable)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean enrich(Queryable queryable)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public <T> List<T> list(Queryable queryable)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> List<T> list(Queryable queryable, Class<T> returnType)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T, R> List<T> list(Queryable queryable, ResultRow<T, R> customResultRow)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int add(Queryable queryable)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public <T> T add(T entity)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int update(Queryable queryable)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public <T> T update(T entity)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int remove(Queryable queryable)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public <T> int remove(T entity)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void flush()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Transactional getTransaction()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void close()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean containsQuery(String name)
    {
        // TODO Auto-generated method stub
        return false;
    }
    
}
