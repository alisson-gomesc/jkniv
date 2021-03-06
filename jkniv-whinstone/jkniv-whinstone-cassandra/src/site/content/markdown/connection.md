Title: Establishing a Connection Apache Cassandra


### Connecting to a cloud DataStax Astra database

When connect to cloud DataStax Astra is mandatory provide a zip file to establish a connection.

    <?xml version="1.0" encoding="UTF-8" standalone="no"?>
    <repository-config xmlns="http://jkniv.sf.net/schema/sqlegance/config"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xsi:schemaLocation="http://jkniv.sf.net/schema/sqlegance/config http://jkniv.sf.net/schema/sqlegance/sqlegance-config.xsd">

      <repository name="my-ds" type="CASSANDRA">
        <properties>
          <property name="jkniv.repository.key_file" value="file:////path/to/secure-connect-bundle.zip" />
          <property name="user" value="admin" />
          <property name="password" value="d0nut" />
          <property name="schema" value="my_keyspace" />
          <property name="jkniv.cassandra.local_datacenter" value="datacenter1" />
        </properties>
      </repository>
    </repository-config>


**Note:** use `file:///` prefix to specify a absolute path file or `/path/to/secure-connect-bundle.zip` for a resource as *classpath*.


### Connecting to Apache Cassandra or DataStax Enterprise (DSE)

To connect a Cassandra Cluster a set of IPs can be provide separated by commas into `url`.

    <repository name="my-ds" type="CASSANDRA">
      <properties>
        <property name="url" value="192.168.99.100,192.168.99.101" />
        <property name="user" value="admin" />
        <property name="password" value="d0nut" />
        <property name="schema" value="my_keyspace" />
        <property name="jkniv.cassandra.local_datacenter" value="datacenter1" />
      </properties>
    </repository>
    
    
### Connecting to Apache Cassandra with JNDI

Connection using JNDI:

    <repository name="my-ds" type="CASSANDRA">
      <jndi-data-source>java:comp/env/props/cassandra-conn</jndi-data-source>
    </repository>
    
<!--
issues

Codec not found for requested operation: [TIMESTAMP <-> java.util.Date]


IllegalStateException: Since you provided explicit contact points, the local DC must be explicitly set (see basic.load-balancing-policy.local-datacenter in the config, or set it programmatically with SessionBuilder.withLocalDatacenter). Current contact points are: Node(endPoint=/192.168.99.100:9042, hostId=46f9ffb0-b607-4fcc-99fc-21ca8fb9badf, hashCode=add248e)=datacenter1. Current DCs in this cluster are: datacenter1 
-->
