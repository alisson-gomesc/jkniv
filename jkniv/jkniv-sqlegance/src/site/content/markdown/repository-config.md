Title: Configuration

Configuration
-------------


Now the sqlegance version 0.6.0 can have configuration to auxiliar the data source connection.


### Binding Sql Statements with Repository Config

To bind a set of SQLs statements with a configuration just add attribute `context` at `<statements>` element, like this:


    <statements 
      context="custom-users"
      xmlns="http://jkniv.sf.net/schema/sqlegance/statements"
      xsi:schemaLocation="http://jkniv.sf.net/schema/sqlegance/statements
      http://jkniv.sf.net/schema/sqlegance/sqlegance-stmt.xsd">


This create link between `repository-sql.xml` (previous version to 0.6.0 is `SqlConfig.xml`) and `repository-config.xml`


    <repository-config xmlns="http://jkniv.sf.net/schema/sqlegance/config" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xsi:schemaLocation="http://jkniv.sf.net/schema/sqlegance/config 
      http://jkniv.sf.net/schema/sqlegance/sqlegance-config.xsd">
    
      <repository name="custom-users" transaction-type="JDBC">
        <description>My jdbc datasource config</description>
    ...
    
    
![Binding](images/bind-config-with-statement.png "Binding")


**Note:** `context` attributes from `<include>` XML files will not be considered!

When a context attribute isn't defined the first repository config name is default.
 
