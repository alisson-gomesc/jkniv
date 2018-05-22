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
package net.sf.jkniv.whinstone.cassandra;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.ColumnDefinitions;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

import net.sf.jkniv.asserts.Assertable;
import net.sf.jkniv.asserts.AssertsFactory;
import net.sf.jkniv.cache.Cacheable;
import net.sf.jkniv.exception.HandleableException;
import net.sf.jkniv.sqlegance.QueryNameStrategy;
import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.sqlegance.RepositoryProperty;
import net.sf.jkniv.sqlegance.Selectable;
import net.sf.jkniv.sqlegance.Sql;
import net.sf.jkniv.sqlegance.SqlContext;
import net.sf.jkniv.sqlegance.SqlType;
import net.sf.jkniv.sqlegance.builder.RepositoryConfig;
import net.sf.jkniv.sqlegance.builder.SqlContextFactory;
import net.sf.jkniv.whinstone.Command;
import net.sf.jkniv.whinstone.ConnectionAdapter;
import net.sf.jkniv.whinstone.JdbcColumn;
import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.ResultRow;
import net.sf.jkniv.whinstone.ResultSetParser;
import net.sf.jkniv.whinstone.cassandra.result.FlatObjectResultRow;
import net.sf.jkniv.whinstone.cassandra.result.MapResultRow;
import net.sf.jkniv.whinstone.cassandra.result.NumberResultRow;
import net.sf.jkniv.whinstone.cassandra.result.ObjectResultSetParser;
import net.sf.jkniv.whinstone.cassandra.result.PojoResultRow;
import net.sf.jkniv.whinstone.cassandra.result.ScalarResultRow;
import net.sf.jkniv.whinstone.cassandra.result.StringResultRow;
import net.sf.jkniv.whinstone.classification.Groupable;
import net.sf.jkniv.whinstone.classification.GroupingBy;
import net.sf.jkniv.whinstone.classification.NoGroupingBy;
import net.sf.jkniv.whinstone.classification.Transformable;
import net.sf.jkniv.whinstone.statement.StatementAdapter;
import net.sf.jkniv.whinstone.transaction.Transactional;

/**
 * Encapsulate the data access for Cassandra
 * 
 * @author Alisson Gomes
 *
 */
class RepositoryCassandra implements Repository
{
    private static final Logger LOG       = LoggerFactory.getLogger(RepositoryCassandra.class);
    private static final Assertable                         notNull = AssertsFactory.getNotNull();
    private QueryNameStrategy   strategyQueryName;
    private HandleableException handlerException;
    private RepositoryConfig    repositoryConfig;
    private SqlContext          sqlContext;
    private ConnectionAdapter   adapterConn;
    // configuration Application settings
    //private static String       server_ip = "127.0.0.1";
    //private static String       keyspace  = "dev_data_3t";
    
    
    private boolean             isTraceEnabled;
    private boolean             isDebugEnabled;
    
    RepositoryCassandra()
    {
        this(new Properties(), SqlContextFactory.newInstance("/repository-sql.xml"));
    }

    RepositoryCassandra(Properties props)
    {
        this(props, SqlContextFactory.newInstance("/repository-sql.xml"));
    }
    
    RepositoryCassandra(String sqlContext)
    {
        this(new Properties(), SqlContextFactory.newInstance(sqlContext));
    }

    RepositoryCassandra(SqlContext sqlContext)
    {
        this(new Properties(), sqlContext);
    }
    
    RepositoryCassandra(Properties props, SqlContext sqlContext)
    {
        notNull.verify(props, sqlContext);
        if (props.isEmpty() || !props.containsKey(RepositoryProperty.JDBC_URL.key()))
        {
            String jndiName = sqlContext.getRepositoryConfig().getJndiDataSource();
            if (jndiName != null && !"".equals(jndiName))
            {
                Properties propsJndi = lookup(jndiName);
                sqlContext.getRepositoryConfig().add(propsJndi);
            }
        }
        else
            sqlContext.getRepositoryConfig().add(props);

        this.sqlContext = sqlContext;
        this.isDebugEnabled = LOG.isDebugEnabled();
        this.isTraceEnabled = LOG.isTraceEnabled();
        this.adapterConn = new CassandraSessionFactory(sqlContext.getRepositoryConfig().getProperties()).open();
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
    public <T> T get(Class<T> returnType, Object object)
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
    private <T, R> List<T> list(Queryable q, Class<T> overloadReturnType, ResultRow<T, R> overloadResultRow)
    {
        if (isTraceEnabled)
            LOG.trace("Executing [{}] as list command", q);
        
        Queryable queryable = QueryFactory.clone(q, overloadReturnType);
        
        List<T> list = Collections.emptyList();
        Class<T> returnType = (Class<T>) Map.class;
        
        Selectable selectable = sqlContext.getQuery(queryable.getName()).asSelectable();
        //checkSqlType(isql, SqlType.SELECT);
        
        selectable.getValidateType().assertValidate(queryable.getParams());
        
        if (!queryable.isBoundSql())
            queryable.bind(selectable);

        Cacheable.Entry entry = selectable.getCache().getEntry(queryable);
        if (entry == null)
        {
            Command command = adapterConn.asSelectCommand(queryable, overloadResultRow);
            list = command.execute();
            if (selectable.hasCache() && !list.isEmpty())
                selectable.getCache().put(queryable, list);
        }
        else
        {
            if (LOG.isInfoEnabled())
                LOG.info("{} object(s) was returned from cache [{}] using query [{}] since {}", list.size(),
                        selectable.getCache().getName(), selectable.getName(), entry.getTimestamp());
            list = (List<T>) entry.getValue();
        }
//
//        if (overloadReturnType != null)
//            returnType = overloadReturnType;
//        else if (selectable.getReturnTypeAsClass() != null)
//            returnType = (Class<T>) selectable.getReturnTypeAsClass();
//
//        StatementAdapter<T, R> stmt = adapterConn.newStatement(queryable);
//        queryable.bind(stmt).on();
//        
//        stmt
//        .returnType(returnType)
//        .resultRow(overloadResultRow)
//        .oneToManies(selectable.getOneToMany())
//        .groupingBy(selectable.getGroupByAsList());
//    
//        if(queryable.isScalar())
//            stmt.scalar();
//        
//        list = stmt.rows();

        q.setTotal(queryable.getTotal());
        if (isDebugEnabled)
            LOG.debug("Executed [{}] query, {} rows fetched", queryable.getName(), list.size());
        
        return list;
    }
    
    @SuppressWarnings("unchecked")
    private <T, R> List<T> ___list___(Queryable queryable, Class<T> overloadReturnType, ResultRow<T, R> overloadResultRow)
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

        StatementAdapter<T, R> stmt = adapterConn.newStatement(queryable);
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

    //@Override
    //@SuppressWarnings("unchecked")
    public <T, R> List<T> __list__(Queryable queryable, ResultRow<T, R> overloadResultRow)
    {
        if (isTraceEnabled)
            LOG.trace("Executing [{}] as list command", queryable);
        
        Selectable isql = sqlContext.getQuery(queryable.getName()).asSelectable();
        checkSqlType(isql, SqlType.SELECT);
        
        isql.getValidateType().assertValidate(queryable.getParams());
        String sql = isql.getSql(queryable.getParams());
        String[] paramsNames = isql.getParamParser().find(sql);
        String positionalSql = isql.getParamParser().replaceForQuestionMark(sql, queryable.getParams());
        Object[] params = queryable.values(paramsNames);
        Session session = null;
        PreparedStatement stmt = session .prepare(positionalSql);
        BoundStatement bound = stmt.bind(params);
        ResultSetParser<T, R> rsParser = null;
        ResultRow<T, R> rsRowParser = null;
        Groupable<T, ?> grouping = new NoGroupingBy<T, T>();
        Transformable<?> transformable = null;
        ResultSet rs = session.execute(bound);
        JdbcColumn<Row>[] columns = getJdbcColumns(rs.getColumnDefinitions());
        Class<T> returnType = (Class<T>) Map.class;
        
        if (isql.getReturnTypeAsClass() != null)
            returnType = (Class<T>)isql.getReturnTypeAsClass();

        List<T> list = Collections.emptyList();
        if (overloadResultRow != null)
        {
            overloadResultRow.setColumns((JdbcColumn<R>[]) columns);
            rsRowParser = overloadResultRow;
        }
        else
        {
            if (queryable.isScalar())
            {
                rsRowParser = new ScalarResultRow(columns);
            }
            else if (Map.class.isAssignableFrom(returnType))
            {
                rsRowParser = new MapResultRow(returnType, columns);
                //transformable = new MapTransform();
            }
            else if (Number.class.isAssignableFrom(returnType)) // FIXME implements for date, calendar, boolean improve design
            {
                rsRowParser = new NumberResultRow(returnType, columns);
            }
            else if (String.class.isAssignableFrom(returnType))
            {
                rsRowParser = new StringResultRow(columns);
            }
            else if (isql.getOneToMany().isEmpty())
            {
                rsRowParser = new FlatObjectResultRow(returnType, columns);
            }
            else
            {
                rsRowParser = new PojoResultRow(returnType, columns, isql.getOneToMany());
            }
        }
        
        transformable = rsRowParser.getTransformable();
        List<String> groupingBy = isql.getGroupByAsList();
        if (!groupingBy.isEmpty())
        {
            grouping = new GroupingBy(groupingBy, returnType, transformable);
        }
        rsParser = new ObjectResultSetParser(rsRowParser, grouping);
        try
        {
            list = rsParser.parser((R) rs);
        }
        catch (SQLException e)
        {
            handlerException.handle(e);
        }
        
        //  List<T> list = currentWork().select(queryable, isql, returnType, resultRow);
        
        if (isDebugEnabled)
            LOG.debug("Executed [{}] query, {}/{} rows fetched", queryable.getName(), list.size(),
                    queryable.getTotal());
        
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
        
        StatementAdapter<Number, ResultSet> adapterStmt = adapterConn.newStatement(queryable);
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
    public boolean containsQuery(String name)
    {
        return sqlContext.containsQuery(name);
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
            adapterConn.close();
        }
        catch (SQLException e)
        {
            LOG.warn("Error to try close Cassandra session/cluster [{}]", adapterConn, e);
        }
    }
    
    private void checkSqlType(Sql isql, SqlType expected)
    {
        if (isql.getSqlType() != expected)
            throw new IllegalArgumentException("Cannot execute sql [" + isql.getName() + "] as ["
                    + isql.getSqlType() + "], exptected is " + expected);
    }

    /**
     * Summarize the columns from SQL result in binary data or not.
     * @param metadata  object that contains information about the types and properties of the columns in a <code>ResultSet</code> 
     * @return Array of columns with name and index
     */
    @SuppressWarnings("unchecked")
    private JdbcColumn<Row>[] getJdbcColumns(ColumnDefinitions metadata)
    {
        JdbcColumn<Row>[] columns = new JdbcColumn[metadata.size()];
        
        for (int i = 0; i < columns.length; i++)
        {
            //int columnNumber = i + 1;
            
            String columnName = metadata.getName(i);//getColumnName(metadata, columnNumber);
            int columnType = metadata.getType(i).getName().ordinal(); //metadata.getColumnType(columnNumber);
            //boolean binaryData = false;
            //if (columnType == Types.CLOB || columnType == Types.BLOB)
            //    binaryData = true;
            columns[i] = new CassandraColumn(i, columnName, columnType);
        }
        return columns;
    }

    private Properties lookup(String remaining)
    {
        Properties prop = null;
        Object resource = JndiResources.lookup(remaining);
        if (resource != null)
        {
            if (resource instanceof Properties)
                prop = (Properties) resource;
            else
                throw new RepositoryException("Resource with name [" + remaining
                        + "] must be an instance of java.util.Properties to connect with Cassandra");
          //TODO exception design, must have ConfigurationException?
        }
        return prop;
    }
}
