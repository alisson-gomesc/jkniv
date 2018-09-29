Title: Cache

# Query Cache

When you have query that run frequently many times with the same parameters, the cache provides performance gains.

Sample the same query running 500 times:

    Chronometer [ Without Cache: 40564 ms, total=500]
    Chronometer [    With Cache:   237 ms, total=500]
    
The cache keep the result in memory and the repository don't hit the database again, the `whinstone` don't take any control over the cache data, so if some update or delete operation occurs over the data in database the cache will not be receive any notification or invalidation about that. The use of cache is highly dependent from design patterns of applications.


    
### Configure Cache

To enable the cache the first one is configure a cache manager:

    <statements 
      xmlns="http://jkniv.sf.net/schema/sqlegance/statements" 
      xmlns:cache="http://jkniv.sf.net/schema/sqlegance/cache"
      xsi:schemaLocation="http://jkniv.sf.net/schema/sqlegance/statements
           http://jkniv.sf.net/schema/sqlegance/sqlegance-stmt.xsd
           http://jkniv.sf.net/schema/sqlegance/cache
           http://jkniv.sf.net/schema/sqlegance/sqlegance-cache.xsd">
    
    <cache:cache-manager delay="600" period="600" ttl="300" tti="120">
      <cache:policy name="cache1" ttl="120" tti="60" />
    </cache:cache-manager>
    
A `cache-manager` contains one or many cache policies.

After that, associate the `select` tag to a cache policy, this example it's for CouchDB query:

    <select id="sessionWithCache"  cache="cache1">
      {
        "selector": {
         "type": {"$eq": "SESSION"},
         "protocol": {"$eq": "WS"},
         "email": {"$eq": :email}
        }
      }
    </select>
    
    
| Cache Manager Attributes                                    |
|------------------|------------------------------------------|    
|  Attribute Name  | Purpose                                  |
|------------------|------------------------------------------|
| delay            | the time from now to delay execution     |
| period           | the period between successive executions |
| ttl              | time to live the data, after that the data is removed from cache |
| tti              | time to idle the data, after that the data is removed from cache |
| size             | limit the quantity of elements can live for entire cache manager  |
| sizeof          |  limit the bytes of the data can live for entire cache manager    |


The `cache-manager` and yours policies contains the same attributes, except for `delay`, `period` to control the `cache-manager` execution. 
But the `ttl`, `tti`, `size` and `sizeof` are the same.
    
    
| Cache Policy Attributes                                    |
|-----------------|------------------------------------------|    
|  Property Name  | Purpose                                  |
|-----------------|------------------------------------------|
| ttl             | time to live the data, after that the data is removed from cache |
| tti             | time to idle the data, after that the data is removed from cache |
| size            | limit the quantity of elements can live for entire cache manager  |
| sizeof          | limit the bytes of the data can live for entire cache manager    |

    
    
**Note**: **size** and **sizeof** until version 0.6.0 it's pendant for implementation!


There isn't synchronization between the cache and the repository, so to help design the use of cache for applications you can instruct programmatically the `Queryable` object to no make use from cache, just call the method `Queryable.cacheIgnore()` the `whinstone` will hit the database again independently from cache. 
 
 
     Queryable query = QueryFactory.of("session", "email", "someone@nohome.com");
     query.cacheIgnore();
     List<Session> list = repositoryDb.list(query);
 