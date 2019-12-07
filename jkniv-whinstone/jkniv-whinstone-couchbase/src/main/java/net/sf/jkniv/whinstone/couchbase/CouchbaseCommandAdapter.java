/* 
 * JKNIV, whinstone one contract to access your database.
 * 
 * Copyright (C) 2017, the original author or authors.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software Foundation, Inc., 
 * 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package net.sf.jkniv.whinstone.couchbase;

import org.slf4j.Logger;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;

import net.sf.jkniv.asserts.Assertable;
import net.sf.jkniv.asserts.AssertsFactory;
import net.sf.jkniv.exception.HandleableException;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.ResultRow;
import net.sf.jkniv.whinstone.commands.Command;
import net.sf.jkniv.whinstone.commands.CommandAdapter;
import net.sf.jkniv.whinstone.couchbase.commands.DefaultQuery;
import net.sf.jkniv.whinstone.couchbase.statement.CouchbaseStatementAdapter;
import net.sf.jkniv.whinstone.statement.StatementAdapter;

/**
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
class CouchbaseCommandAdapter implements CommandAdapter
{
    private static final Logger  LOG = LoggerFactory.getLogger();
    private static final Logger SQLLOG = net.sf.jkniv.whinstone.couchbase.LoggerFactory.getLogger();
    private static final Assertable NOT_NULL = AssertsFactory.getNotNull();
    private Cluster cluster;
    private Bucket bucket;
    //private StatementCache stmtCache;
    private final String contextName;
    private final HandleableException handlerException;
    
    public CouchbaseCommandAdapter(Cluster cluster, Bucket bucket, String contextName, HandleableException handlerException)
    {
        NOT_NULL.verify(cluster, contextName);
        this.cluster = cluster;
        this.bucket = bucket;
        //this.stmtCache = new StatementCache(session);
        this.contextName = contextName;
        this.handlerException = handlerException;
    }
    
    @Override
    public String getContextName()
    {
        return this.contextName;
    }

    
    @Override
    public void close() //throws SQLException
    {
        // Couchbase session is built in multi-thread
        
    }

    //@Override
    public void shutdown() //throws SQLException
    {
        if (bucket != null)
        {
            LOG.info("Closing Couchbase Bucket for {} context", this.contextName);
            bucket.close();
        }   
        if (cluster != null )
        {
            LOG.info("Closing Couchbase Cluster connection for {} context", this.contextName);
            cluster.disconnect();
        }
        cluster = null;
        bucket = null;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public <T, R> Command asSelectCommand(Queryable queryable, ResultRow<T, R> overloadResultRow)
    {
        DefaultQuery command = null;
        String sql = queryable.query();
        if(SQLLOG.isInfoEnabled())
            SQLLOG.info("Bind Native SQL\n{}", sql);

        StatementAdapter<T, R> stmt = null;
        stmt = new CouchbaseStatementAdapter(this.bucket, queryable);
        if (queryable.isPaging())
            stmt.setFetchSize(queryable.getMax());

        queryable.bind(stmt).on();
        stmt.with(overloadResultRow);
        command = new DefaultQuery(stmt, queryable);
        return command;
    }
    
    @Override
    public <T, R> Command asUpdateCommand(Queryable queryable)
    {
        return buildCommand(queryable);
    }
    
    @Override
    public <T, R> Command asRemoveCommand(Queryable queryable)//, ResultRow<T, R> overloadResultRow)
    {
        return buildCommand(queryable);
    }
    
    @Override
    public <T, R> Command asAddCommand(Queryable queryable)//, ResultRow<T, R> overloadResultRow)
    {
        return buildCommand(queryable);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private <T,R> Command buildCommand(Queryable queryable)
    {
        Command command = null;
        /*
        String sql = queryable.query();
        AutoKey auto = null;
        if(SQLLOG.isInfoEnabled())
            SQLLOG.info("Bind Native SQL\n{}",sql);
        
        PreparedStatement stmtPrep = this.stmtCache.prepare(sql);
        CassandraPreparedStatementAdapter<Number, Row> stmt = new CassandraPreparedStatementAdapter<Number, Row>(this.session, stmtPrep, queryable);
        if (queryable.isTypeOfBulk())
            command = new BulkCommand((CassandraPreparedStatementAdapter) stmt, queryable);        
        else if (queryable.getDynamicSql().isInsertable() && 
                 queryable.getDynamicSql().asInsertable().isAutoGenerateKey())
        {
            Insertable isql = queryable.getDynamicSql().asInsertable();
            // isSequenceStrategy and isAutoStrategy are the the same in cassandra uuid value is generated
            // if(isql.getAutoGeneratedKey().isAutoStrategy())
            auto = new CassandraSequenceGeneratedKey(isql, this.session, handlerException);
            command = new AddSequenceKeyJdbcCommand(stmt, queryable);
        }
        else
            command = new DefaultCommand((CassandraPreparedStatementAdapter) stmt, queryable);

        stmt.with(auto);
        */
        return command;        
    }

    @Override
    public String toString()
    {
        return "CouchbaseCommandAdapter [session=" + bucket.name() + ", cluster=" + cluster + "]";
    }

}
