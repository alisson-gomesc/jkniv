package net.sf.jkniv.whinstone;

import java.util.List;

import net.sf.jkniv.whinstone.transaction.Transactional;

class RepositoryEmpty implements Repository
{
    @Override
    public <T> T get(Queryable queryable)
    {
        return null;
    }

    @Override
    public <T> T get(Queryable queryable, Class<T> returnType)
    {
        return null;
    }

    @Override
    public <T, R> T get(Queryable queryable, ResultRow<T, R> customResultRow)
    {
        return null;
    }

    @Override
    public <T> T get(T object)
    {
        return null;
    }

    @Override
    public <T> T get(Class<T> returnType, Object object)
    {
        return null;
    }

    @Override
    public <T> T scalar(Queryable queryable)
    {
        return null;
    }

    @Override
    public boolean enrich(Queryable queryable)
    {
        return false;
    }

    @Override
    public <T> List<T> list(Queryable queryable)
    {
        return null;
    }

    @Override
    public <T> List<T> list(Queryable queryable, Class<T> returnType)
    {
        return null;
    }

    @Override
    public <T, R> List<T> list(Queryable queryable, ResultRow<T, R> customResultRow)
    {
        return null;
    }

    @Override
    public int add(Queryable queryable)
    {
        return 0;
    }

    @Override
    public <T> int add(T entity)
    {
        return 0;
    }

    @Override
    public int update(Queryable queryable)
    {
        return 0;
    }

    @Override
    public <T> T update(T entity)
    {
        return null;
    }

    @Override
    public int remove(Queryable queryable)
    {
        return 0;
    }

    @Override
    public <T> int remove(T entity)
    {
        return 0;
    }

    @Override
    public void flush()
    {
    }

    @Override
    public Transactional getTransaction()
    {
        return null;
    }

    @Override
    public void close()
    {
    }

    @Override
    public boolean containsQuery(String name)
    {
        return false;
    }

    @Override
    public <T> T dsl()
    {
        return null;
    }
    
}
