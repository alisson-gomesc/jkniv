Title: Introduction

# Introduction

## What is whinstone-rest


Whinstone-jpa2 is a implementation of  <a href="http://martinfowler.com/eaaCatalog/repository.html">Repository pattern</a> using JPA2, Hibernate and SQLegance.

# Why Whinstone

Whinstone allow the developers worry just with implementation of view, controller and business layers. While Whinstone encapsulate all data access with the Repository pattern keeping the secure coding of SQL Injection, paginated queries and others good design practices.

The Whinstone it's a final implementation of repository pattern, where the interface is defined at <a href="jkniv.sourceforge.net/apidocs/net/sf/jkniv/sqlegance/IRepository.html"><![CDATA[net.sf.jkniv.sqlegance.IRepository<T>]]></a> (SQLegance project), using some other frameworks.

### Requirements dependencies

### Full configuration whinstone-rest (web.xml)

    <context-param>
     <description>Scan package for model types</description>
     <param-name>model.packages</param-name>
     <param-value>example.acme.model</param-value>
    </context-param>
    <context-param>
     <description>Scan package for jasper file reports</description>
     <param-name>report.packages</param-name>
     <param-value>example.acme.reports</param-value>
    </context-param>
    <context-param>
     <description>Scan package for regiters transform result classes</description>
     <param-name>transform.packages</param-name>
     <param-value>example.acme.transformers</param-value>
    </context-param>
    <context-param>
     <description>Sql context file</description>
     <param-name>fullname.sqlContext</param-name>
     <param-value>/repository-sql.xml</param-value>
    </context-param>
    <listener>  
     <listener-class>net.sf.jkniv.whinstone.rs.RegistryLoaderListener</listener-class>
    </listener>
 


## URL patterns

http://host:port/context/api/rs/[repository-method]/[queryname]/?


