Title: Query Parameters

# Query Parameters

##JPQL


#### JPA Native Query Restrictions

JPA have a series of restrictions when use native queries

>3.8.12 Positional Parameters

>Only positional parameter binding and positional (i.e., “?”, rather than “?1”) access to result items may be portably used for native
queries.
>The use of positional parameters is not supported for criteria queries.

However `whinstone-jpa2` accept named parameter for JPQL and Native Query.

#### EclipseLink, named parameters for Native query

>[EclipseLink Parameters to SQL queries](https://wiki.eclipse.org/EclipseLink/UserGuide/JPA/Basic_JPA_Development/Querying/Native) are delimited using the ? character. Only indexed parameters are supported, named parameters are not supported.

Using `whinstone-jpa2` you can use named parameter for all queries at EclipseLink (JPQL, Native, Named Native and Named), jkniv resolve then for you.  


