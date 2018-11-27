Title: Introduction


# What is whinstone


It's implementation from Repository pattern:

>by Edward Hieatt and Rob Mee

>Mediates between the domain and data mapping layers using a collection-like interface for accessing domain objects.

The project `jkniv-whinstone` define the repository contract between you service layer and the database through the interface `net.sf.jkniv.whinstone.Repository`. There are four repository flavors: 

- `jkniv-whinstone-jdbc` encapsulate the JDBC access with a simple repository contract.
- `jkniv-whinstone-jpa2` encapsulate JPA access with a simple repository contract.
- `jkniv-whinstone-cassandra` encapsulate Apache Cassandra protocol to access with a simple repository contract.
- `jkniv-whinstone-couchdb` encapsulate HTTP API from CouchDB to access with a simple repository contract.

The High-level design:

![whinstone design](images/whinstone-architecture.png)



## Oh nooo more one database framework!

The whinstone repository interface it's simple and very ease to use, it establish a contract for the Repository pattern, with the advantage that you can programmer using different databases (Oracle, PostgreSQL, etc) or APIs (JDBC, JPA) the same way. The learning curve to use `jkniv-whinstone` is very low because you don't need to learning another query language like Criteria, JOOQL etc, understand a simple API with automatic binding and coding faster.

The steps to start are:

1. Configure your datasource
2. write your SQL queries
3. run, forrest, run...

No annotations, no mapping (except if you want to use JPA), easily to test. The power of the database query language is yours and `whinstone` gives to you **the plain java objects with automatic bind to input parameters and result set output**, reducing a boilerplate of code to set JDBC parameters and ResultSet *getters*.


It's very seductive write a query and someone (framework) translate to my specific database like HQL, JPQL, jOOQ DSL, etc. But there is the trap from lowest common denominator that could hug that framework. It's probable that a lot of extra java code is necessary to do the same without the **native** query from your database.

The `whinstone` approach keep the power of database query language and your java code become more maintainable naturally.

These queries make the same for NoSQL Cassandra and a Relation Database SQL with PARTITION BY:

    // cassandra query (Partition key -> acct_id, event, evt_date)
    SELECT acct_id, event, evt_date, tag_code FROM TRACKABLE_DATA PER PARTITION LIMIT 1;


    // SQL with PARTITION BY
    SELECT * FROM 
     (SELECT acct_id, event, evt_date, tag_code, 
      row_number() OVER (PARTITION BY acct_id, event, evt_date ORDER BY acct_id, event, evt_date desc) rnum
      FROM TRACKABLE_DATA) tablerank  
    WHERE tablerank.rnum = 1


    // Java code, naturally simple
    Queryable query = QueryFactory.of("events");
    List<Event> events = repository.list(query);
    

You can use the best of your database without waste time to learn new framework query language, DSL or anything else.


    
### Requirements

`jkniv-whinstone-jdbc`, `jkniv-whinstone-jpa2`, `jkniv-whinstone-cassandra` and `jkniv-whinstone-couchdb` binaries requires JDK level 6.0 or above.


## The collection-like interface

The `Repository` interface have a simple set of methods to manipulate the data from/to database.

- `get`: retrieve only one row data from repository.
- `list`: retrieve a set of rows data from repository.
- `add`: add an object or collection of data to repository.
- `update`: update an object or collection of data to repository.
- `remove`: delete an object or collection of data from repository.
- `enrich`: use the `queryable` object to retrieve data from repository and append the result to object parameter from `queryable`. 
- `scalar`: retrieve a scalar value

The library is 100% protected against [SQL injection](https://www.owasp.org/index.php/SQL_Injection "OWASP SQL injection") because all statements are [PreparedStatement](https://docs.oracle.com/javase/6/docs/api/java/sql/PreparedStatement.html "PreparedStatement"), except to couchdb that access it's over HTTP protocol.


### Traceability for Whinstone Features

| Features            | whinstone-jpa2 | whinstone-jdbc | whinstone-cassandra | whinstone-couchdb |
| ------------------- | -------------- | -------------- |---------------------|-------------------|
| Auto Bind Parameters| ![close][chk]  | ![close][chk]  | ![close][chk]       | ![close][chk]     |
| Auto Bind Result    | ![close][chk]  | ![close][chk]  | ![close][chk]       | ![close][chk]     |
| One-to-One          | ![close][chk]  | ![close][chk]  | ![close][chk]        | ![open][clo]     |
| One-to-Many         | ![close][chk]  | ![close][chk]  | ![close][chk]        | ![open][clo]     |
| JSR Bean Validation | ![close][chk]  | ![close][chk]  | ![close][chk]       | ![close][chk]     |
| Transaction         | ![close][chk]  | ![close][chk]  | ![open][clo]        | ![open][clo]      |
| Paginate Query      | ![close][chk]  | ![close][chk]  | ![open][clo]        | ![open][clo]      |
| Retrieving auto-generated keys| ![close][chk]| ![close][chk]| ![open][clo]  | ![close][chk]     |
| Bulk Commands(batch)| ![open][clo]   | ![open][clo]   | ![open][clo]        | ![close][chk]     |
| Callback Methods    | ![open][clo]   | ![open][clo]   | ![open][clo]        | ![close][chk]     |
| Triggers Events     | ![open][clo]   | ![open][clo]   | ![open][clo]        | ![open][clo]      |
| Query Cache         | ![open][clo]   | ![open][clo]   | ![close][chk]       | ![close][chk]     |
| Query Statistics    | ![open][clo]   | ![open][clo]   | ![open][clo]        | ![open][clo]      |
| Stored Procedure    | ![open][clo]   | ![open][clo]   | ![open][clo]        | ![open][clo]      |
| Converter Annotation| ![open][clo]   | ![open][clo]   | ![open][clo]        | ![open][clo]      |
| Jdk8 Timers         | ![open][clo]   | ![open][clo]   | ![open][clo]        | ![close][chk]  (jackson)|



### Transaction supports

| Transaction API    | whinstone-jpa2 | whinstone-jdbc | whinstone-cassandra | whinstone-couchdb |
| ------------------ | -------------- | -------------- |---------------------|-------------------|
| JTA                |   yes (0.6.0)  | no             | no                  | no                |
| JPA                |   yes (0.6.0)  | no             | no                  | no                |
| JDBC               |   yes (0.6.0)  | yes  (0.6.0)   | no                  | no                |
| Spring transaction |   yes (0.6.0)  | yes  (0.6.0)   | no                  | no                |


[chk]: images/check.png "Supported"
[clo]: images/close.png "Not implemented yet"
