Title: Configuration

Configuration
-------------


SQLegance provides an way to configure the database connection (data source or properties), but is complete unnecessary if you are using with another framework like Hibernate or spring-jdbc.

An analogue with JPA files is shown into the next table:

| File       | JPA                                        | SQLegance                                |
|------------|--------------------------------------------|------------------------------------------|
|config file | `/main/java/resources/META-INF/persistence.xml` | `/main/java/resources/repository-config.xml`  |
|query file  | `/main/java/resources/META-INF/orm.xml`        | any name, normally `repository-sql.xml`, `/main/java/resources/repository-sql.xml` |


### Binding SQL Statements with Repository Config

To bind a set of SQLs statements with a configuration just add attribute `context` at `<statements>` element, like this:


    <statements 
      context="ctx-users"
      xmlns="http://jkniv.sf.net/schema/sqlegance/statements"
      xsi:schemaLocation="http://jkniv.sf.net/schema/sqlegance/statements
      http://jkniv.sf.net/schema/sqlegance/sqlegance-stmt.xsd">


This create a link between `repository-sql.xml` file and `repository-config.xml`.


    <repository-config xmlns="http://jkniv.sf.net/schema/sqlegance/config" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xsi:schemaLocation="http://jkniv.sf.net/schema/sqlegance/config 
      http://jkniv.sf.net/schema/sqlegance/sqlegance-config.xsd">
    
      <repository name="ctx-users" transaction-type="JDBC">
        <description>My jdbc datasource config</description>
    ...
    
    
![Binding](images/bind-config-with-statement.png "Binding")


**Note:** `context` attributes from `<include>` files will not be considered!

When a context attribute isn't defined at `statements` element, the first `repository` element is used as default.

