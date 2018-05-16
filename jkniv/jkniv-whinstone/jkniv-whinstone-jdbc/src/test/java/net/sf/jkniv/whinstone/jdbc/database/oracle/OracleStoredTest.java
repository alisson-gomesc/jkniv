package net.sf.jkniv.whinstone.jdbc.database.oracle;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.jdbc.BaseJdbc;

public class OracleStoredTest extends BaseJdbc
{
    // https://www.mkyong.com/oracle/oracle-stored-procedures-hello-world-examples/
    @Autowired
    Repository repositoryOra;
    
    @Test
    public void whenCallingSimpleStoredByUpdateNoParams()
    {
        Queryable q = QueryFactory.newInstance("helloStored");
        repositoryOra.update(QueryFactory.newInstance("enable_output"));
        repositoryOra.update(q);
    }
    
    @Test @Ignore("TODO test")
    public void whenCallingSimpleStoredByUpdateOneINParam()
    {
    }
    
    @Test @Ignore("TODO test")
    public void whenCallingSimpleStoredByUpdateOneOUTParam()
    {
    }
    
    @Test @Ignore("TODO test")
    public void whenCallingSimpleStoredByUpdateOneINOUTParam()
    {
    }

    @Test @Ignore("TODO test")
    public void whenCallingSimpleStoredByUpdateOneCursor()
    {
    }

}
