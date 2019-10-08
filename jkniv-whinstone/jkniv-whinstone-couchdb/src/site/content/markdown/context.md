Title: Context

Whinstone JDBC Context
-------------------------

The context represents that configuration about your database connection. If your project works just one data pool connection (data source) don't worry about that, but if you needs to handle more one data pool connection you have to specify the context at repository-sql.xml.


    <?xml version="1.0" encoding="UTF-8" standalone="no"?>
    <statements context="oracle"
      xmlns="http://jkniv.sf.net/schema/sqlegance/statements"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xmlns:xi="http://www.w3.org/2001/XInclude"
      xsi:schemaLocation="http://jkniv.sf.net/schema/sqlegance/statements
        http://jkniv.sf.net/schema/sqlegance/sqlegance-stmt.xsd">
      
      <include href="/repository-queries.xml"/>
    </statements>

The JDBC repository is thread-safe for each context and if you are manipulate more one jdbc data source the context is mandatory, otherwise your database connection will be mess up.