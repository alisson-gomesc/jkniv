Title: CouchDB Properties

Jackson API Config
-------------
       
Jackson provides many ways to work with JSON/POJO objects converting to/from them. 

`whinstone-couchdb` use Jackson API library to save the JSON format into couchdb database and convert the JSON values to POJO objects.

Sample of `src/main/resources/repository-config.xml` file:

    <repository name="users">
      <description>My couchdb database</description>
      <jndi-data-source>jdbc/dsUsers</jndi-data-source>
      <properties>
        <property name="jackson.WRITE_DATES_AS_TIMESTAMPS" value="true"/>
      </properties>
    </repository>
  
All properties started with `jackson.*` will be configured to work with singleton instance of `ObjectMapper`. 


### Jackson set Serialization Inclusion

| Jackson Serialization                    |
--------------------------------------------
|`jackson.JsonInclude.Include.ALWAYS`       |
|`jackson.JsonInclude.Include.NON_NULL`     |
|`jackson.JsonInclude.Include.NON_ABSENT`   |
|`jackson.JsonInclude.Include.NON_EMPTY`    |
|`jackson.JsonInclude.Include.NON_DEFAULT`  |
|`jackson.JsonInclude.Include.USE_DEFAULTS` |

Sample of `src/main/resources/repository-config.xml` file:

    <repository name="users">
      <description>My couchdb database</description>
      <jndi-data-source>jdbc/dsUsers</jndi-data-source>
      <properties>
        <property name="jackson.JsonInclude.Include.NON_NULL" value="true"/>
      </properties>
    </repository>

Note: the value different `true` not set up the serialization.

### Jackson register module

Sometimes jackson needs register a module to work properly. All property named start with `jackson.module` have intentions to do that. 

The modules supported are: 

 - ParameterNamesModule
 - JavaTimeModule
 - ParameterNamesModule
 - JSR310TimeModule
 - ThreeTenModule

Example how to register a Jackson module:

    <repository name="users">
      ...
      <properties>
        <property name="jackson.module.ParameterNamesModule" value="true"/>
        <property name="jackson.module.Jdk8Module" value="true"/>
        <property name="jackson.module.JavaTimeModule" value="true"/>
      </properties>
    </repository>
    
    
Design Documents
---------

## Views

>In CouchDB, design documents provide the main interface for building a CouchDB application. The design document defines the views used to extract information from CouchDB through one or more views.


Automatically `whinstone-couchdb` update the `views` to database when a new instance of `Repository` is created. That feature helps keep the view update accordingly the deployed version of application. Default behavior is `false` if you want keep the views updated define the property to `true`:

    <property name="jkniv.couchdb.update_views" value="true"/>
    
The viewer code is kept together with the SQL code into XML file under a specific prefix package name `_design`.  So this views code will be update into CouchDB.
    
    <package name="_design/docs">
      <select id="map#natio-combo">
        <description>generate unique values of nationality</description>
        function (doc) {
          if(doc.nationality) {
            emit(doc.nationality, 1);
          }
        }
      </select>
      <select id="reduce#natio-combo">
      <description>Count total of nationalities</description>
        function(keys, values) {
          return sum(values);
        }
      </select>
    </package>
    

There are 2 types of function on CouchDB to map it for `whinstone` use the prefixes `map#` and `reduce#`:

- For `map` function the `select` name must start with `map#`
- For `reduce` function the `select` name must start with `reduce#`


![design documents](images/design_docs.png)
   
    
## Indexes

>Mango is a declarative JSON querying language for CouchDB databases. Mango wraps several index types, starting with the Primary Index out-of-the-box. Mango indexes, with index type json, are built using MapReduce Views.


Automatically `whinstone-couchdb` **drop** and **create** the `indexes` to database when a new instance of `Repository` is created. That feature helps keep the indesx update accordingly the deployed version of application. Default behavior is `false` if you want keep the indexes updated define the property to `true`:

    <property name="jkniv.couchdb.update_indexes" value="true"/>
    
Example:

    <package name="_design/indexes">
      <select id="index#auto-create-foo-index">
      {
         "index": {
            "fields": [
               "foo"
            ]
         },
         "type": "json",
         "name": "auto-create-foo-index",
         "ddoc": "auto-create-foo-index"
      }
      </select>
    </package>
    
Start a package name with `_design` is mandatory to separate the indexes from Mango Queries and the **id** must start with `index#`


The **ddoc** value must have the same suffix name from `id` property (this example is `auto-create-foo-index` ). This way `whinstone-couchdb` can drop the actual index and create a new index.

### Properties for CouchDB
-------

|Property                          |default               | Description       |
| ----------------------------------|--------------------- | ----------------- |
|jkniv.repository.accessId          | `id`, `getId`, `setId` | Change default access for `id` property |
|jkniv.couchdb.update_views         | `false`               | Enable auto update view defined into XML |
|jkniv.couchdb.update_indexesviews  | `false`               | Enable auto drop/create indexes defined into XML|
|jackson.*                          |                      | Change the state of an on/off SerializationFeature for object mapper. |

