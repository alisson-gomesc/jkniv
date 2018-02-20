package net.sf.jkniv.whinstone.cassandra;

import java.sql.SQLException;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;

import net.sf.jkniv.sqlegance.ConnectionAdapter;
import net.sf.jkniv.sqlegance.Queryable;
import net.sf.jkniv.sqlegance.statement.StatementAdapter;
import net.sf.jkniv.whinstone.cassandra.statement.PreparedStatementAdapter;

public class CassandraConnectionAdapter implements ConnectionAdapter
{
    private Session session;
    private Cluster             cluster;
    public CassandraConnectionAdapter(Cluster cluster, Session session)
    {
        this.cluster = cluster;
        this.session = session;
    }
    @Override
    public void commit() throws SQLException
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void rollback() throws SQLException
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void close() throws SQLException
    {
        if (session != null && !session.isClosed())
        {
            session.close();
            
        }
        if (cluster != null && !cluster.isClosed())
        {
                cluster.close();
        }
        cluster = null;
        session = null;
    }
    
    @Override
    public boolean isClosed() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public boolean isAutoCommit() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public void autoCommitOn() throws SQLException
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void autoCommitOff() throws SQLException
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public Object getMetaData()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public <T, R> StatementAdapter<T, R> newStatement(Queryable queryable)
    {
        String sql = queryable.getSql().getSql(queryable.getParams());
        String positionalSql = queryable.getSql().getParamParser().replaceForQuestionMark(sql, queryable.getParams());
        PreparedStatement stmt = session.prepare(positionalSql);
        StatementAdapter<T, R> adapter = new PreparedStatementAdapter(session, stmt);
        return adapter;
    }
    
    @Override
    public <T, R> StatementAdapter<T, R> newStatement(String sql)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public Object unwrap()
    {
        return session;
    }
    @Override
    public String toString()
    {
        return "CassandraConnectionAdapter [session=" + session + ", cluster=" + cluster + "]";
    }

    
}
