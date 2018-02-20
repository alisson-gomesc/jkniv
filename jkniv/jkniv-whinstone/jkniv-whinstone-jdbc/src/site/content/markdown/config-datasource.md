Title: Configure JNDI Datasource

How-to Configure JNDI Datasource
--------------------

This manual takes maven structure project as reference.

1. Edit `WEB-INF/web.xml` file.
2. Edit the resources file from your server, like `WEB-INF/jetty-env.xml` for jetty.
3. Create a file named `repository-config.xml` in `src/main/resources`, after compile or package the file go to `WEB-INF/classes/repository-config.xml` from web project, if your project isn't web must be root from classpath.


# Jetty Server

1. Contents `web.xml` file.


    <resource-ref>
      <description>Database DB2/AS400 Connection</description>
      <res-ref-name>jdbc/as400</res-ref-name>
      <res-type>javax.sql.DataSource</res-type>
      <res-auth>Container</res-auth> 
      <mapped-name>jdbc/as400</mapped-name>
    </resource-ref>

2. Contents `WEB-INF/jetty-env.xml` file.


    <!DOCTYPE Configure PUBLIC "-//Mort Bay Consulting//DTD Configure//EN" "http://www.eclipse.org/jetty/configure.dtd">
    <Configure class="org.eclipse.jetty.webapp.WebAppContext">
      <New id="dsAS400" class="org.eclipse.jetty.plus.jndi.Resource">
         <Arg>jdbc/as400</Arg>
         <Arg>
           <New class="com.jolbox.bonecp.BoneCPDataSource">
             <Set name="driverClass">com.ibm.as400.access.AS400JDBCDriver</Set>
             <Set name="jdbcUrl">jdbc:as400:192.168.0.10;</Set>
             <Set name="username">my_user</Set>
             <Set name="password">my_passwd</Set>
             <Set name="minConnectionsPerPartition">4</Set>
             <Set name="maxConnectionsPerPartition">10</Set>
             <Set name="acquireIncrement">2</Set>
             <Set name="idleConnectionTestPeriod">30</Set>
          </New>
        </Arg>
      </New>
    </Configure>

3. Contents `repository-config.xml` file.


    <repository-config 
     xmlns="http://jkniv.sf.net/schema/sqlegance/config" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
     xsi:schemaLocation="http://jkniv.sf.net/schema/sqlegance/config 
     http://jkniv.sf.net/schema/sqlegance/sqlegance-config.xsd">

    <repository name="commav" type="JDBC">
      <jndi-data-source>jdbc/as400</jndi-data-source>
        <properties>
          <property name="jkniv.repository.jdbc.dialect" value="net.sf.jkniv.whinstone.jdbc.dialect.DB2Dialect" />
        </properties>
      </repository>
    </repository-config>

*Note:* See more details for [jetty-ds] and [jetty-jndi]

# Tomcat Server (tested at 9 version)

*Notes:*
Tomcat JNDI resource configuration changed somewhat between Tomcat 7.x and Tomcat 8.x as they are using different versions of Apache Commons DBCP library, see [tomcat-change][] and [tomcat-migration][] for details 


1. Contents `web.xml` file.


    <resource-ref>
      <description>Database DB2/AS400 Connection</description>
      <res-ref-name>jdbc/as400</res-ref-name>
      <res-type>javax.sql.DataSource</res-type>
      <res-auth>Container</res-auth> 
      <mapped-name>jdbc/as400</mapped-name>
    </resource-ref>

2. Contents `META-INF/context.xml` file.


    <Context path="/commav-rest" reloadable="true">    
      <Resource name="jdbc/as400" auth="Container" type="javax.sql.DataSource"
       maxActive="10" maxIdle="30" maxWaitMillis="10000" driverClassName="com.ibm.as400.access.AS400JDBCDriver"
       username="my_user" password="my_passwd" 
       url="jdbc:as400:192.168.0.10;"/>  
      <ResourceLink name="jdbc/as400" global="jdbc/as400" type="javax.sql.DataSource" />
    </Context>

3. Contents `repository-config.xml` file.

*Note*: The difference from jetty that tomcat needs the prefix `java:comp/env/` for jndi name in `repository-config.xml` file.


    <repository-config 
     xmlns="http://jkniv.sf.net/schema/sqlegance/config" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
     xsi:schemaLocation="http://jkniv.sf.net/schema/sqlegance/config 
     http://jkniv.sf.net/schema/sqlegance/sqlegance-config.xsd">
     
      <repository name="commav" type="JDBC">
        <jndi-data-source>java:comp/env/jdbc/as400</jndi-data-source>
        <properties>
          <property name="jkniv.repository.jdbc.dialect" value="net.sf.jkniv.whinstone.jdbc.dialect.DB2Dialect" />
        </properties>
      </repository>
    </repository-config>


# Glassfish Server



# JBoss or Widlfy Server
 No tested.
 
# Weblogic Server
 No tested.
 
# WAS Server
 No tested.
 
 
 
[jetty-jndi]: https://wiki.eclipse.org/Jetty/Feature/JNDI       "Enabling Jetty JNDI"
[jetty-ds]: https://wiki.eclipse.org/Jetty/Howto/Configure_JNDI_Datasource "Configuring Datasoutce at Jetty"
[tomcat-migration]: http://tomcat.apache.org/migration.html     "See Tomcat Migration Guide for details"
[tomcat-change]: https://tomcat.apache.org/tomcat-9.0-doc/jndi-datasource-examples-howto.html "Tomcat datasource sample"
 
 
 