Title: Bulk Operations

Whinstone JDBC Bulk Operations
--------------------


`Repository` supports bulk operations when the parameters are collections over `add`, `update` or `remove` methods:

- Collections of array object (`Object[]`)
- Collections of Map (`java.util.Map`)
- Collections of POJOs
- Array of Map
- Array of POJOs

So when the parameters are a collection of data that needs to be executed by the same query one single call to `Repository.add`, `Repository.update` or `Repository.remove` is enough to execute the commands to internal `for each`.

    update book set name = :name, isbn = :isbn where id = :id
    
    
    Collection<Book> params = getBooks();
    Queryable queryable = QueryFactory.newInstance("Book#update", params);
    int rowsAffected = repositoryDerby.update(queryable);
    

`rowsAffected` receive the sum of records changed by `Book#update` for each Book in collection.
    
**Options to batch size, batch mode it's pending to implements!**
    