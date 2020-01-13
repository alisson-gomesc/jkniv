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

>In CouchDB, design documents provide the main interface for building a CouchDB application. The design document defines the views used to extract information from CouchDB through one or more views.


`whinstone-couchdb` can keep yours code views updated automatically with the code deployed. When you do a new deploy the library update the view code into CouchDB. Default behavior is `false` if you want keep the views updated define the property to `true`:

    <property name="jkniv.repository.couchdb.update_views" value="true"/>
    
The viewer code is kept together with the SQL code into XML file under a specific prefix package name `_design`.  So this viewer code will be update into CouchDB.
    
    <package name="_design/docs">
      <select id="map#natio">
        function(doc) { 
          emit( doc.nationality, {
           name: doc.name,
           nationality: doc.nationality,
           books: doc.books
          })
        }
      </select>
    </package>
    

There are 2 types of function on CouchDB:

- For `map` function the select name must start with `map#`
- For `reduce` function the select name must start with `reduce#`


![design documents](images/design_docs.png)
   
    
    
Properties for CouchDB
-------

|Property                               |default               | Description       |
| --------------------------------------|--------------------- | ----------------- |
|jkniv.repository.accessId              | `id`, `getId`, `setId` | Change default access for `id` property |
|jkniv.repository.couchdb.update_views  | `false`               | Enable auto update view definitions from XML code |
|jackson.*                              |                      | Change the state of an on/off SerializationFeature for object mapper. |

