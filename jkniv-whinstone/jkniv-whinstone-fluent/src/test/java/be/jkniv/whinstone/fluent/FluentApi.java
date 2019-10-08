package be.jkniv.whinstone.fluent;

import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;

public class FluentApi
{
    Repository repository;
    public Repository get(Queryable queryable) { return repository; }
    public Repository list(Queryable queryable)  { return repository; }
    public Repository add(Queryable queryable)  { return repository; }
    public Repository update(Queryable queryable)  { return repository; }
    public Repository remove(Queryable queryable)  { return repository; }
    public Repository enrich(Queryable queryable)  { return repository; }
    public Repository flush()  { return repository; }
    public Repository scalar(Queryable queryable)  { return repository; }
    public Repository transation()  { return repository; }
    
    public void fetch() {}
}
