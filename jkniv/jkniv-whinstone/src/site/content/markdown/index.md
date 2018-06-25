Title: Introduction


# What is whinstone


It's implementation from Repository pattern:

>by Edward Hieatt and Rob Mee

>Mediates between the domain and data mapping layers using a collection-like interface for accessing domain objects.

The project `jkniv-whinstone` define the repository contract between you service layer and the database with the interface `net.sf.jkniv.whinstone.Repository`. There are four repository flavors: 

- `jkniv-whinstone-jdbc` repository contract using directly jdbc access.
- `jkniv-whinstone-jpa2` encapsulate JPA access with a simple repository contract.
- `jkniv-whinstone-cassandra` encapsulate cassandra access with a simple repository contract.
- `jkniv-whinstone-couchdb` encapsulate http api to access with a simple repository contract.

The High-level design:

![whinstone design](images/whinstone-architecture.png)



## Oh nooo more one database framework!

The whinstone repository interface it's simple and very ease to use, it establish the contract for the Repository pattern, with the advantage that you can programmer for your database the same way. The learning curve to use jkniv-whinstone is very low because you just needs to know the query language for your database.

The steps to start are:

1. Configure your datasource
2. write your SQL queries
3. run forest run...

No annotations, no mapping (except if you want to use JPA), easily to test. The power of the database query language is yours and `whinstone` gives to you **the plain java objects with automatic bind to input parameters and result set output**, reducing a boilerplate of code to set JDBC parameters and resultset *getters*.

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



### Transaction supports

| Transaction API    | whinstone-jpa2 | whinstone-jdbc | whinstone-cassandra | whinstone-couchdb |
| ------------------ | -------------- | -------------- |---------------------|-------------------|
| JTA                |   yes (0.6.0)  | no             | no                  | no                |
| JPA                |   yes (0.6.0)  | no             | no                  | no                |
| JDBC               |   yes (0.6.0)  | yes  (0.6.0)   | no                  | no                |
| Spring transaction |   yes (0.6.0)  | yes  (0.6.0)   | no                  | no                |



