package net.sf.jkniv.whinstone.cassandra.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.whinstone.Command;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.cassandra.statement.CassandraStatementAdapter;

public class SelectCommand implements Command
{
    private static final Logger LOG = LoggerFactory.getLogger(SelectCommand.class);
    //private String body;
    private CassandraStatementAdapter<?, String> stmt;
    private Queryable queryable;

    
    public SelectCommand(CassandraStatementAdapter<?, String> stmt, Queryable queryable)
    {
        super();
        this.queryable = queryable;
        this.stmt = stmt;
        stmt.rows();
        //this.body = stmt.getBody();
    }

    @Override
    public <T> T execute()
    {
        T list = (T) stmt.rows();
      return list;
    }
    
    @Override
    public String getBody()
    {
        return queryable.query();
    }
    
}
