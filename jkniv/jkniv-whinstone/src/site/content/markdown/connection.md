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

### Connection with JNDI
