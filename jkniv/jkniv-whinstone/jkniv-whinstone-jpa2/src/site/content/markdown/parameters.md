Title: Parameters

# Query Parameters

##JPQL


##Native Query

**Note JPA specification:** assumed that for native queries the parameters themselves use the SQL syntax (i.e., “?”, rather than “?1”).
The use of positional parameters is not supported for criteria queries.


## EclipseLink, named parameters for Native query


>[EclipseLink Parameters to SQL queries](https://wiki.eclipse.org/EclipseLink/UserGuide/JPA/Basic_JPA_Development/Querying/Native) are delimited using the ? character. Only indexed parameters are supported, named parameters are not supported.

However using `whinstone-jpa2` you can use named parameter for all queries at EclipseLink (JPQL, Native, Named Native and Named).  


