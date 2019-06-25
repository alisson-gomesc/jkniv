Title: Apache Cassandra Paging

Apache Cassandra Paging
-------------

It is a good practices [paginate][2] the result of a query. [Apache Cassandra][1] has some constraints about that:

>Offset queries: it doesn’t allow random jumps (like “go directly to page 10”), because you can’t fetch a page unless you have the paging state of the previous one.

>Note that setting a fetch size doesn’t mean that Cassandra will always return the exact number of rows, it is possible that it returns slightly more or less results.

>PagingState instances are not portable across native protocol versions. This could become a problem in the following scenario:
>- you’re using the driver 2.0.x and Cassandra 2.0.x, and therefore native protocol v2;
>- a user bookmarks a link to your web service that contains a serialized paging state;
>- you upgrade your server stack to use the driver 2.1.x and Cassandra 2.1.x, so you’re now using protocol v3;
>- the user tries to reload their bookmark, but the paging state was serialized with protocol v2, so trying to reuse it will fail.


But Apache Cassandra supports go to the next page using a bookmark resource saving [PagingState][3] even are not portable.

`whinstone-cassandra` work with paginate using `Queryable` interface and `PagingState`.

    Queryable q = QueryFactory.of("customers", 0, 25);// query name, offset, max rows

In this case `offset` is ignored and the max result is set to 25. The `PagingState` is returned into `Queryable` as `bookmark` property.

A example to show that:

    Queryable q = QueryFactory.of("customers", 0, 25);
    List<Customer> customers = repository.list(q);
    assertThat(q.getTotal(), is(java.sql.Statement.SUCCESS_NO_INFO));
    assertThat(q.getBookmark(), notNullValue());

Next request to get next page:

    Queryable q = QueryFactory.of("customers", 0, 25);
    q.setBookmark(previousBookmark);
    List<Customer> customers = repository.list(q);


[1]: https://docs.datastax.com/en/developer/java-driver/3.2/manual/paging/ "Paging Java Driver for Apache Cassandra"
[2]: https://en.wikipedia.org/wiki/Pagination "Paginate"
[3]: https://docs.datastax.com/en/drivers/java/2.0/com/datastax/driver/core/PagingState.html "PagingState"