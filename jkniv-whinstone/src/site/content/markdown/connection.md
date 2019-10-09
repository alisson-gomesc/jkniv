Title: Establishing a Connection

# Establishing a Connection

To establish a connection with the data source must be using one of three ways:

1. A `java.util.Properties` class
2. A XML configuration with property parameters
3. A JNDI.


### Connection with Properties class

    Properties props = new Properties();
    props.put(RepositoryProperty.JDBC_URL.key(), "jdbc:oracle:thin:@127.0.0.1:1521/XE");
    props.put(RepositoryProperty.JDBC_DRIVER.key(), "oracle.jdbc.driver.OracleDriver");
    props.put(RepositoryProperty.JDBC_USER.key(), "myuser");
    props.put(RepositoryProperty.JDBC_PASSWORD.key(), "secret");
    
    Repository repository = RepositoryService.getInstance().lookup(RepositoryType.JDBC).newInstance(props);

The code above configure the connection parameters: url, driver, user and password. After that get an instance of `RepositoryService` to lookup the Repository façade setting the properties.

### Connection with XML property

To configure the connection properties in XML, a file with name `repository-config.xml` is mandatory in root folder, the structure bellow is a sample in maven:

    src/main/resources/
                ├─ repository-config.xml


This XML file represents the same configuration when a Properties class was used.

    <?xml version="1.0" encoding="UTF-8" standalone="no"?>
    <repository-config 
      xmlns="http://jkniv.sf.net/schema/sqlegance/config" 
      xsi:schemaLocation="http://jkniv.sf.net/schema/sqlegance/config http://jkniv.sf.net/schema/sqlegance/sqlegance-config.xsd">
      
    <repository name="myconfig">
      <description>My properties connection fo Oracle XE</description>
      <properties>
        <property name="driver" value="oracle.jdbc.driver.OracleDriver" />
        <property name="url" value="jdbc:oracle:thin:@127.0.0.1:1521/XE" />
        <property name="user" value="myuser" />
        <property name="password" value="secret" />
      </properties>
    </repository>

So, the java code is used to lookup the Repository façade.

    Repository repository = RepositoryService.getInstance().lookup(RepositoryType.JDBC).newInstance();

### Connection with JNDI

Using JNDI, the Repository façade can access a JDBC connection pool by looking up the DataSource that configures it.

    <?xml version="1.0" encoding="UTF-8" standalone="no"?>
    <repository-config 
      xmlns="http://jkniv.sf.net/schema/sqlegance/config" 
      xsi:schemaLocation="http://jkniv.sf.net/schema/sqlegance/config http://jkniv.sf.net/schema/sqlegance/sqlegance-config.xsd">
      
    <repository name="myconfig">
      <description>My properties connection fo Oracle XE</description>
      <jndi-data-source>java:comp/env/jdbc/myOracle</jndi-data-source>
    </repository>

In the above example, `java:comp/env/jdbc/myOracle` is the name by which the pool is referenced in the container.

So, the same java code is used to lookup the Repository façade.

    Repository repository = RepositoryService.getInstance().lookup(RepositoryType.JDBC).newInstance();


### repository-config.xml file


A `repository-config.xml` is like a `persistence.xml` for JPA that define one or more persistence unit. The `repository-config.xml` file is located in root classpath. It may be used to specify the connection properties to `Repository` façade and other parameters.


|Property                                | Description       |
| -------------------------------------- | ----------------- |
| user                                   | User to connect in database.
| password                               | Password to connect in database.
| url                                    | JDBC URL to connect in database.
| driver                                 | JDBC Drive class.
| schema                                 | Database schema, used for Couchdb or Cassandra.
| jkniv.repository.query_namestrategy    | default is `net.sf.jkniv.sqlegance.HashQueryNameStrategy`.
| jkniv.repository.jdbc.dialect          | Database dialect for LIMIT statement, default is `net.sf.jkniv.sqlegance.dialect.AnsiDialect`.
| jkniv.repository.data_masking          | Mask sensible data in log, default is `net.sf.jkniv.sqlegance.logger.SimpleDataMasking`.
| jkniv.repository.short_name_enable     | allow find query name using simple name, example: `query.finance.balance` could be lookup as `balance`, default is `false`. 
| jkniv.repository.reloadable_xml_enable | for reloading query files based on timestamp changes, default is `false`. When enable `true` running for 3 hours, after that doesn't reload anymore. Used for development environment.
| jkniv.repository.jdbc_adapter_factory  | Adapter for Connection manager in `jkniv-whinstone-jdbc`, implementations: `net.sf.jkniv.whinstone.jdbc.DriverManagerAdapter`, `net.sf.jkniv.whinstone.jdbc.DataSourceAdapter` and `net.sf.jkniv.whinstone.jdbc.SpringDataSourceAdapter`, default is `DriverManagerAdapter`.
| jkniv.repository.show_config           | Print the DatabaseMetaData in log, default is `false`
|jkniv.repository.protocol_version       | Values of `ProtocolVersion`: `NEWEST_SUPPORTED`, `V1`, `V2`, `V3`, `V4`, default is `NEWEST_SUPPORTED`

    
### SQL Dialect
 
 To work correctly with a database a SQL dialect must be set.
  
  
|Database     | Dialect           |
| ----------- | ----------------- |
| CouchDB     | net.sf.jkniv.whinstone.couchdb.dialect.CouchDbDialect2o1 |
| Cassandra   | net.sf.jkniv.whinstone.cassandra.dialect.CassandraDialect |
| DB2         | net.sf.jkniv.whinstone.jdbc.dialect.DB2Dialect |
| DB2 compatibility MySQL | net.sf.jkniv.whinstone.jdbc.dialect.DB2EnableMYSDialect | 
| DB2 compatibility Oracle | net.sf.jkniv.whinstone.jdbc.dialect.DB2EnableORADialect |
| Derby 10.4  | net.sf.jkniv.whinstone.jdbc.dialect.Derby10o4Dialect |
| Derby 10.7  | net.sf.jkniv.whinstone.jdbc.dialect.Derby10o7Dialect |
| HSQL        | net.sf.jkniv.whinstone.jdbc.dialect.HsqldbDialect |
| Informix    | net.sf.jkniv.whinstone.jdbc.dialect.InformixDialect |
| Ingres      | net.sf.jkniv.whinstone.jdbc.dialect.IngresDialect |
| MySQL       | net.sf.jkniv.whinstone.jdbc.dialect.MySqlDialect |
| Oracle      | net.sf.jkniv.whinstone.jdbc.dialect.OracleDialect |
| Oracle 12   | net.sf.jkniv.whinstone.jdbc.dialect.Oracle12cDialect |
| PostgreSQL  | net.sf.jkniv.whinstone.jdbc.dialect.PostgreSqlDialect |
| SQLite      | net.sf.jkniv.whinstone.jdbc.dialect.SQLiteDialect |
| SQLServer   | net.sf.jkniv.whinstone.jdbc.dialect.SqlServerDialect |


### Override dialect properties

Some dialect properties can be override to adapt the jdbc driver characteristics: 

    <repository name="dialect-override" transaction-type="LOCAL">
      <description>My jdbc datasource config overriding dialect</description>
      <jndi-data-source>jdbc/dssample</jndi-data-source>
      <properties>
        <property name="jkniv.repository.feature.limit" value="true"/>
        <property name="jkniv.repository.feature.limit_off_set" value="false"/>
        <property name="jkniv.repository.feature.rownum" value="false"/>
        <property name="jkniv.repository.feature.conn_holdability" value="true"/>
        <property name="jkniv.repository.feature.stmt_holdability" value="false"/>
        <property name="jkniv.repository.feature.bookmark_query" value="false"/>
        <property name="jkniv.repository.feature.paging_roundtrip" value="false"/>
        <property name="jkniv.repository.feature.jdbc.max_parameters" value="1000"/>
      </properties>
    </repository>
    
    
|Property | Type   | Default | Description |
|---------|--------|-------|-------------|
|limit    | boolean|`false` | SQL vendor support some form of limiting query results?|
|limit_off_set | boolean| `false`|SQL vendor support some form of offset query results?| 
|rownum| boolean| `false` |SQL vendor support some form of enumerate the rows results? | 
|conn_holdability| boolean|`true`|supports holdability at the connection level |
|stmt_holdability| boolean|`false`|supports holdability at the statement level  |
|bookmark_query| boolean|`false`|a page selected to mark the reader's place |
|paging_roundtrip|boolean|`false`|check if database needs a new round trip to get total paging|
|max_parameters |int|2147483647|  maximum number of parameters, any int value greater than 0 |


