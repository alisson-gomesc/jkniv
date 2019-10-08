Title: Parameters

# SQL Dialect configuration



  <repository name="dialect-override" transaction-type="LOCAL">
    <description>My jdbc datasource config overriding dialect</description>
    <jndi-data-source>jdbc/dssample</jndi-data-source>
    <properties>
      <property name="jkniv.repository.unknow" value="true"/>
      <property name="jkniv.repository.limit" value="true"/>
      <property name="jkniv.repository.limit_off_set" value="false"/>
      <property name="jkniv.repository.rownum" value="true"/>
      <property name="jkniv.repository.conn_holdability" value="false"/>
      <property name="jkniv.repository.statement_holdability" value="true"/>
      <property name="jkniv.repository.jdbc.max_parameters" value="255"/>
    </properties>
  </repository>