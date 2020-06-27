Title: Whinstone Querying


# Querying database

Whinstone use `jkniv-sqlegance` to handle the query language for your database. The philosophy behind `jkniv-whinstone` it's keeping the java code simple, without SQL manipulation, all [SQL is handler by XML files][1].


# CouchDB

Into `jkniv-whinstone-couchdb` there are queries that are built-in, or else, you don't need write them:

- `_all_docs`
- `get`
- `add`
- `update`
- `remove`


## _find


## _views


Example to create CouchDb View that emit a list of values for nationalities:
 
    <package name="_design/stored">
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
    
Result of CouchDb view `http://localhost:5984/my-db/_design/stored/_view/natio-combo?group=true`

    {
      "rows": [
        {
          "key": "BR", "value": 1
        },
        {
          "key": "CZ", "value": 1
        },
        {
          "key": "DE", "value": 3
        },
        {
          "key": "DZ", "value": 1
        },
        {
          "key": "GB", "value": 1
        }
      ]
    }
    
Write a select query with type `STORED` to execute it:
    
    <select id="docs/_view/natio-combo" type="STORED"/>

Java code to retrieve the view data using the `group` attribute from CouchDb:

    Queryable q = QueryFactory.of("docs/_view/natio-combo", "group", "true");
    List<Map> list = repositoryDb.list(q);

or write a POJO with `key` and `value` attributes to retrieve a typed object.

    Queryable q = QueryFactory.of("docs/_view/natio-combo", Combo.class, "group", "true");
    List<Combo> list = repositoryDb.list(q);


#### Retrieve default JSON from CouchDB

If you want retrieve the default JSON data from CouchDb you can use the `CouchDbResult` interface as return of your query:

    <select id="authors-deutsch" returnType="net.sf.jkniv.whinstone.couchdb.CouchDbResult">
      <description>Select all books written by a german</description>
      {
       "selector": {"nationality": "DE" }
      }
    </select>


Java code get the result:

    Queryable query = QueryFactory.of("authors-deutsch");
    CouchDbResult answer = repositoryDb.get(query);

Interface that represents the CouchDb data, this interface was normalized to represents the data from
[_find](https://docs.couchdb.org/en/stable/api/database/find.html "_find") and [_view](https://docs.couchdb.org/en/stable/api/ddoc/views.html  "_view")

    public interface CouchDbResult {
      Long getTotalRows();
      Long getOffset();
      List<?> getRows();
      String getBookmark();
      String getWarning();
      ExecutionStats getExecutionStats();
    }


## _bulk



[1]: http://jkniv.sourceforge.net/jkniv-sqlegance/index.html "SQL is handler by XML files"