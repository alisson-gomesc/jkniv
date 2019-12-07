Title: Bulk Commands

Bulk Command
-------------
       
From CouchDB Documentation: 

>>The bulk document API allows you to create and update multiple documents at the same time within a single request. The basic operation is similar to creating or updating a single document, except that you batch the document structure and information. 
       
`whinstone-couchdb` supports bulk API when the parameters from `Queryable` is a collection: 
 - Collection of POJO
 - Collection of Map
 - Collection of array
 - Array of POJO
 - Array of Map

    Repository repository = getRepository();
    List<Author> authors = new ArrayList<Author>();
        
    Author a1 = new Author();
    a1.setName("Bulk One"); a1.setNationality("BR");
    Author a2 = new Author();
    a2.setName("Bulk Two"); a2.setNationality("PT");
    Author a3 = new Author(); 
    a3.setName("Bulk Three"); a3.setNationality("AU");

    authors.add(a1); authors.add(a2); authors.add(a3);
    Queryable q = QueryFactory.of("add", authors); // query must be named with 'add' or 'update'
        
    int rows = repository.add(q);
    assertThat(rows, is(3));

**whinstone-couchdb** there are built-in queries: `add`, `remove`, `update`, `get` and `_all_docs`, isn't necessary wrote in XML file. So, to use bulk API you must be invoke repository method using one from these built-in queries. 

