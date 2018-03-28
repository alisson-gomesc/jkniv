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
package net.sf.jkniv.whinstone.couchdb;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.asserts.Assertable;
import net.sf.jkniv.asserts.AssertsFactory;
import net.sf.jkniv.exception.HandleableException;
import net.sf.jkniv.sqlegance.ConnectionAdapter;
import net.sf.jkniv.sqlegance.QueryNameStrategy;
import net.sf.jkniv.sqlegance.Queryable;
import net.sf.jkniv.sqlegance.Repository;
import net.sf.jkniv.sqlegance.ResultRow;
import net.sf.jkniv.sqlegance.Selectable;
import net.sf.jkniv.sqlegance.Sql;
import net.sf.jkniv.sqlegance.SqlContext;
import net.sf.jkniv.sqlegance.SqlType;
import net.sf.jkniv.sqlegance.builder.RepositoryConfig;
import net.sf.jkniv.sqlegance.builder.SqlContextFactory;
import net.sf.jkniv.sqlegance.statement.StatementAdapter;
import net.sf.jkniv.sqlegance.transaction.Transactional;

/**
 * Encapsulate the data access for Cassandra
 * 
 * @author Alisson Gomes
 *
 */
public class RepositoryCouchDb implements Repository
{
    private static final Logger LOG       = LoggerFactory.getLogger(RepositoryCouchDb.class);
    private static final Assertable                         notNull = AssertsFactory.getNotNull();
    private QueryNameStrategy   strategyQueryName;
    private HandleableException handlerException;
    private RepositoryConfig    repositoryConfig;
    private SqlContext          sqlContext;
    private ConnectionAdapter   factoryConnection;
    
    
    private boolean             isTraceEnabled;
    private boolean             isDebugEnabled;
    
    RepositoryCouchDb()
    {
        //openConnection();
        this.sqlContext = SqlContextFactory.newInstance("/repository-sql.xml");
        this.isDebugEnabled = LOG.isDebugEnabled();
        this.isTraceEnabled = LOG.isTraceEnabled();
        this.factoryConnection = new HttpConnectionFactory(new Properties()).open();
    }
    
    RepositoryCouchDb(Properties props)
    {
        this.sqlContext = SqlContextFactory.newInstance("/repository-sql.xml");
        this.isDebugEnabled = LOG.isDebugEnabled();
        this.isTraceEnabled = LOG.isTraceEnabled();
        this.factoryConnection = new HttpConnectionFactory(props).open();
    }

    RepositoryCouchDb(Properties props, SqlContext sqlContext)
    {
        this.sqlContext = sqlContext;
        this.isDebugEnabled = LOG.isDebugEnabled();
        this.isTraceEnabled = LOG.isTraceEnabled();
        this.factoryConnection = new HttpConnectionFactory(props).open();
    }
    
//    private void openConnection()
//    {
//        if (cluster != null)
//            return;
//        
//        cluster = Cluster.builder().addContactPoints(server_ip).build();
//        
//        final Metadata metadata = cluster.getMetadata();
//        String msg = String.format("Connected to cluster: %s", metadata.getClusterName());
//        System.out.println(msg);
//        System.out.println("List of hosts");
//        for (final Host host : metadata.getAllHosts())
//        {
//            msg = String.format("Datacenter: %s; Host: %s; Rack: %s", host.getDatacenter(), host.getAddress(),
//                    host.getRack());
//            System.out.println(msg);
//        }
//        session = cluster.connect(keyspace);
//    }
    
//    private PreparedStatement getPreparedStatement(String statement)
//    {
//        return session.prepare(statement);
//    }
    
    @Override
    public <T> T get(Queryable queryable)
    {
        throw new UnsupportedOperationException("RepositoryCassandra doesn't implement this method yet!");
    }
    
    @Override
    public <T> T get(Queryable queryable, Class<T> returnType)
    {
        throw new UnsupportedOperationException("RepositoryCassandra doesn't implement this method yet!");
    }
    
    @Override
    public <T, R> T get(Queryable queryable, ResultRow<T, R> resultRow)
    {
        throw new UnsupportedOperationException("RepositoryCassandra doesn't implement this method yet!");
    }
    
    @Override
    public <T> T get(T object)
    {
        throw new UnsupportedOperationException("RepositoryCassandra doesn't implement this method yet!");
    }
    
    @Override
    public <T> T get(Class<T> returnType, T object)
    {
        throw new UnsupportedOperationException("RepositoryCassandra doesn't implement this method yet!");
    }
    
    @Override
    public <T> T scalar(Queryable queryable)
    {
        throw new UnsupportedOperationException("RepositoryCassandra doesn't implement this method yet!");
    }
    
    @Override
    public boolean enrich(Queryable queryable)
    {
        throw new UnsupportedOperationException("RepositoryCassandra doesn't implement this method yet!");
    }
    
    @Override
    public <T> List<T> list(Queryable queryable)
    {
        return list(queryable, null, null);
    }
    
    @Override
    public <T> List<T> list(Queryable queryable, Class<T> returnType)
    {
        return list(queryable, returnType, null);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T, R> List<T> list(Queryable queryable, ResultRow<T, R> overloadResultRow)
    {
        return list(queryable, null, overloadResultRow);
    }
    
    @SuppressWarnings("unchecked")
    private <T, R> List<T> list(Queryable queryable, Class<T> overloadReturnType, ResultRow<T, R> overloadResultRow)
    {
        if (isTraceEnabled)
            LOG.trace("Executing [{}] as list command", queryable);
        
        List<T> list = Collections.emptyList();
        Class<T> returnType = (Class<T>) Map.class;
        
        Sql isql = sqlContext.getQuery(queryable.getName());
        checkSqlType(isql, SqlType.SELECT);
        Selectable select = isql.asSelectable();
        
        isql.getValidateType().assertValidate(queryable.getParams());
        
        if (!queryable.isBoundSql())
            queryable.bind(isql);

        if (overloadReturnType != null)
            returnType = overloadReturnType;
        else if (isql.getReturnTypeAsClass() != null)
            returnType = (Class<T>) isql.getReturnTypeAsClass();

        StatementAdapter<T, R> stmt = factoryConnection.newStatement(queryable);
        queryable.bind(stmt).on();
        
        stmt
        .returnType(returnType)
        .resultRow(overloadResultRow)
        .oneToManies(select.getOneToMany())
        .groupingBy(select.getGroupByAsList());
    
        if(queryable.isScalar())
            stmt.scalar();
        
        list = stmt.rows();

        if (isDebugEnabled)
            LOG.debug("Executed [{}] query, {} rows fetched", queryable.getName(), list.size());
        
        return list;
        
    }
    
    @Override
    public int add(Queryable queryable)
    {
        notNull.verify(queryable);
        //if (isTraceEnabled)
        //    LOG.trace("Executing [{}] as add command with dialect [{}]", queryable, this.repositoryConfig.getSqlDialect());
        
        Sql isql = sqlContext.getQuery(queryable.getName());
        checkSqlType(isql, SqlType.INSERT);
        if (!queryable.isBoundSql())
            queryable.bind(isql);
        
        isql.getValidateType().assertValidate(queryable.getParams());
        
        StatementAdapter<Number, String> adapterStmt = factoryConnection.newStatement(queryable);
        queryable.bind(adapterStmt).on();
        int affected = adapterStmt.execute();

        if (isDebugEnabled)
            LOG.debug("{} records was affected by add [{}] command", affected, queryable.getName());
        return affected;
    }
    
    @Override
    public <T> T add(T entity)
    {
        throw new UnsupportedOperationException("RepositoryCassandra doesn't implement this method yet!");
    }
    
    @Override
    public int update(Queryable queryable)
    {
        throw new UnsupportedOperationException("RepositoryCassandra doesn't implement this method yet!");
    }
    
    @Override
    public <T> T update(T entity)
    {
        throw new UnsupportedOperationException("RepositoryCassandra doesn't implement this method yet!");
    }
    
    @Override
    public int remove(Queryable queryable)
    {
        if (isTraceEnabled)
            LOG.trace("Executing [{}] as remove command", queryable);

        Sql isql = sqlContext.getQuery(queryable.getName());
        checkSqlType(isql, SqlType.DELETE);
        
        isql.getValidateType().assertValidate(queryable.getParams());
        
        int affected = 0;//currentWork().execute(queryable, isql);
        if (isDebugEnabled)
            LOG.debug("{} records was affected by remove [{}] command", affected, queryable.getName());
        return affected;

        //throw new UnsupportedOperationException("RepositoryCassandra doesn't implement this method yet!");
    }
    
    @Override
    public <T> int remove(T entity)
    {
        throw new UnsupportedOperationException("RepositoryCassandra doesn't implement this method yet!");
    }
    
    @Override
    public void flush()
    {
        throw new UnsupportedOperationException("RepositoryCassandra doesn't implement this method yet!");
    }
    
    @Override
    public Transactional getTransaction()
    {
        throw new UnsupportedOperationException("RepositoryCassandra doesn't implement this method yet!");
    }
    
    @Override
    public void close()
    {
        try
        {
            factoryConnection.close();
        }
        catch (SQLException e)
        {
            LOG.warn("Error to try close Cassandra session/cluster [{}]", factoryConnection, e);
        }
    }
    
    private void checkSqlType(Sql isql, SqlType expected)
    {
        if (isql.getSqlType() != expected)
            throw new IllegalArgumentException("Cannot execute sql [" + isql.getName() + "] as ["
                    + isql.getSqlType() + "], exptected is " + expected);
    }

//    /**
//     * Summarize the columns from SQL result in binary data or not.
//     * @param metadata  object that contains information about the types and properties of the columns in a <code>ResultSet</code> 
//     * @return Array of columns with name and index
//     */
//    @SuppressWarnings("unchecked")
//    private JdbcColumn<Row>[] getJdbcColumns(ColumnDefinitions metadata)
//    {
//        JdbcColumn<Row>[] columns = new JdbcColumn[metadata.size()];
//        
//        for (int i = 0; i < columns.length; i++)
//        {
//            //int columnNumber = i + 1;
//            
//            String columnName = metadata.getName(i);//getColumnName(metadata, columnNumber);
//            int columnType = metadata.getType(i).getName().ordinal(); //metadata.getColumnType(columnNumber);
//            //boolean binaryData = false;
//            //if (columnType == Types.CLOB || columnType == Types.BLOB)
//            //    binaryData = true;
//            columns[i] = new CouchDbColumn(i, columnName, columnType);
//        }
//        return columns;
//    }

}
