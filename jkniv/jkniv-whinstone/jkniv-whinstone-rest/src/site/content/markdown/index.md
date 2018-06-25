Title: Introduction

# Introduction

## What is whinstone-rest


`jkniv-whinston-rest` allow access the XML queries using HTTP verbs.


### Requirements dependencies

`jkniv-whinstone-rest`require JDK 1.7 or high.

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
     <param-value>/repository-sql-finance.xml, /repository-sql-humanresources.xml</param-value>
    </context-param>
    <listener>
     <listener-class>net.sf.jkniv.whinstone.rest.RegistryLoaderListener</listener-class>
    </listener>
 


## URL patterns

    http://[host]:[port]/[context]/<jersey-base-url>/<sqlegance-context>/<repository-method>/<queryname>/?

Example:

    http://localhost:8080/myapp/api/finance/list/user?





