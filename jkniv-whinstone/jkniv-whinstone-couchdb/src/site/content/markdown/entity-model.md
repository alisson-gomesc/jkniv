Title: whinstone couchdb

jkniv whinstone couchdb
--------------------

### Introduction

This section show the details and constraints from `jkniv-whinstone-couchdb` how make the bind the values to POJO objects 



### Identify field `_id` and `_rev`

The fields `_id` and `_rev` are mandatory to save a document into CouchDB.

`whinstone-couchdb` use Jackson library to mapping a Java object to a CouchDB document,


Entity example with mandatories fields for CouchDB.

    import com.fasterxml.jackson.annotation.JsonProperty;
    public class Account {
      @JsonProperty("_id")
      private String id;
      @JsonProperty("_rev")
      private String rev;
      ... 
    }
    
The default access for `id` property is using the name `id` to the field, `getId` for getter and `setId` for setter.

Those values can be replaced by a property `jkniv.repository.accessId` defined into `repository-config.xml` file:

    <repository name="dialect-override" transaction-type="LOCAL">
     <description>Overriding access for identify field</description>
      <properties>
       <property name="jkniv.repository.accessId" value="id,getId,withId"/>
      </properties>
    </repository>
    
- `id`: field name
- `getId`: read method (getter)
- `withId`: writer method (setter)

    