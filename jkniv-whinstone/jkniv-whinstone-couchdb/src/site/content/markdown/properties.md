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
  
All properties started with `jackson.*` will be configured to work with instance of `ObjectMapper`. 


Design Documents
---------

>In CouchDB, design documents provide the main interface for building a CouchDB application. The design document defines the views used to extract information from CouchDB through one or more views.


`whinstone-couchdb` can keep yours code views updated automatically with the code deploy. When you did a new deploy the library update the view code into CouchDB. Default behavior is `false` if you want keep the views updated define the property to `true`:

    <property name="jkniv.repository.couchdb.update_views" value="true"/>
    

The viewer code is kept together with the SQL code into XML file under a specific prefix package name `_design`.      
    
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
    
    
So this viewer code will be update into CouchDB.

![design documents](images/design_docs.png)

    
    
    