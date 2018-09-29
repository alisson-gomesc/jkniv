Title: Whinstone Querying


# Querying database

Whinstone use `jkniv-sqlegance` to handle the query language for your database. The philosophy behind `jkniv-whinstone` it's keeping the java code simple, without SQL manipulation, all [SQL is handler by XML files][1].


## Overview

The `jkniv-whinstone` doesn't have section or ORM to mapping your objects, so there is no magic, to handler the automatic binding for result set the columns or the alias are used to build the POJO. For example:

    select b.id, b.isbn, b.name from Book b
    
Bind with POJO:

    public class Book {
      private Long   id;
      private String isbn;
      private String name;
      // getters and setters REQUIRED
    }

The attribute and columns name are the same, match! More important are the getters and the setters. 

**Note:** `jkniv-whinstone` **doesn't broken the objects encapsulation, always the bind is through the getter and setters methods**.


But, not always the column have the same attribute name, then columns alias must be used to bind with the attribute. That means that you use Snake Case (snake_case) to bind with Camel Case (camelCase).

    select b.ID, b.PUBLISHER as publisher_name from Book b
    
Bind with POJO:

    public class Book {
      private Long   id;
      private String publisherName;
      // getters and setters REQUIRED
    }

A `set` method matching name and type is required to bind the column and alias names, like `publisher_name`:   

    setPublisherName(String publisherName)


### One-to-One Relationship

The `jkniv-whinstone` supports bind nested attributes (One-to-One relationship), this example I have a Book with one Author and an Address.

    public class Book {
      private Long   id;
      private String name;
      private Author author;
      // getters and setters REQUIRED
    }
    
    public class Author {
      private Long       id;
      private String     name;
      private Address address
      // getters and setters REQUIRED
    }

    public class Address {
      private String     street;
      // getters and setters REQUIRED
    }

To populate the `Book` with relationship One-to-One the alias must be between quotes using dot notation to inject yours values. 

    select b.id, b.name
     ,a.ID      as "author.id"
     ,a.NAME    as "author.name"
     ,ad.STREET as "author.address.street"
    from book b
    inner join author a on a.id = b.author_id
    left  join address ad on ad.id = a.address_id


**Note:** all bind happen using JavaBeans Conventions `get`/`set`/`is` methods, encapsulation is safe. A default constructor is required too (the exception it's for JPA native queries where the bind **for native queries** happen into Constructor).

### One-to-Many Relationship

Defines a association with one-to-many multiplicity, `jkniv-whinstone` can populate this relationship using a special tag `<one-to-many>` in `select` element.

For example, consider when a `Author` having zero or more `Books`:

    public class Author {
      private Long       id;
      private String     name;
      private List<Book> books;
      // getters and setters REQUIRED
    }

The plain query result could be translated to one-to-many relationship using the attribute element `groupBy` and `<one-to-many>` tag element:

    <select id="authors" returnType="org.acme.model.Author" groupBy="id">
      <one-to-many property="books" typeOf="org.acme.model.Book"/>
      select 
        a.ID, a.NAME, 
        b.ID as "books.id", b.ISBN as "books.isbn", b.NAME as "books.name"
      from AUTHOR a 
      inner join BOOK b on b.AUTHOR_ID = a.ID
      order by a.name, b.name
    </select>

- `groupBy` a set of columns (one or more) to group the result set.
- `one-to-many` to be populate one-to-many where `property` represent the attribute name from main class (`Author`) and `typeOf` must have the fully qualified class name that represents the relationship (`Book`).
- quoted values (`"books.id"`) it's required to populate the object association.


### Many-to-Many Relationship

`jkniv-whinstone` doesn't supports the Many-to-Many relationship.


## Parameters from Statements

Bellow we have a copy of the code using [JDBC PreparedStatement][2]


    public void updateCoffeeSales(HashMap<String, Integer> salesForWeek)
        throws SQLException {
    
        PreparedStatement updateSales = null;
        PreparedStatement updateTotal = null;
    
        String updateString = "update COFFEES set SALES = ? where COF_NAME = ?";
        String updateStatement = "update COFFEES set TOTAL = TOTAL + ? where COF_NAME = ?";
    
        try {
            con.setAutoCommit(false);
            updateSales = con.prepareStatement(updateString);
            updateTotal = con.prepareStatement(updateStatement);
    
            for (Map.Entry<String, Integer> e : salesForWeek.entrySet()) {
                updateSales.setInt(1, e.getValue().intValue());
                updateSales.setString(2, e.getKey());
                updateSales.executeUpdate();
                updateTotal.setInt(1, e.getValue().intValue());
                updateTotal.setString(2, e.getKey());
                updateTotal.executeUpdate();
                con.commit();
            }
        } catch (SQLException e ) {
            JDBCTutorialUtilities.printSQLException(e);
            if (con != null) {
                try {
                    System.err.print("Transaction is being rolled back");
                    con.rollback();
                } catch(SQLException excep) {
                    JDBCTutorialUtilities.printSQLException(excep);
                }
            }
        } finally {
            if (updateSales != null) {
                updateSales.close();
            }
            if (updateTotal != null) {
                updateTotal.close();
            }
            con.setAutoCommit(true);
        }
    }

The same functionality could be wrote like this using `jkniv-whinstone-jdbc`:

XML queries:

    <!-- (1) -->
    <update id="updateSales">
      update COFFEES 
      <set>
        <if test="sales != null">
          SALES = :sales
        </if>
        <if test="total != null">
          TOTAL = TOTAL + :total
        </if>
      </set>
      where COF_NAME = :coffee
    </update>

Java code:

    public void updateCoffeeSales(Collection<Map<String, Integer>> salesForWeek) {
    
      Queryable salesQuery = QueryFactory.of("updateSales", salesForWeek); // (2)
        
      try {
        repository.getTransaction().begin();                               // (3)
        
        repository.update(salesQuery);                                     // (4) 
     
        repository.getTransaction().commit();                              // (5)
      } catch (RepositoryException e ) {                                   // (6)
        System.err.print("Transaction is being rolled back");
        repository.getTransaction().rollback();                            // (7)
      }
    }

- `(1)`: queries is kept in XML files, away from java code. `jkniv-sqlegance` supports dynamic queries accordingly input values
- `(2)`: the automatic bind in action
- `(3)`: starting a new transaction, because we have a collection of data to be processed by a statement
- `(4)`: the repository iterates through each element of the Collection argument and sets the appropriate named parameter in `updateSales`, using a PreparedStatement.
- `(5)`: if everything it's ok a commit is propagate to the database
- `(6)`: Catch a RepositoryException is optional because it's unchecked exception
- `(7)`: if a RepositoryException is threw a rollback is executed.

OK, java looks like more readable. Of course `Collection<Map>` could be replace to `Collection<Sale>` for a better design.

[1]: http://jkniv.sourceforge.net/jkniv-sqlegance/index.html "SQL is handler by XML files"
[2]: https://docs.oracle.com/javase/tutorial/jdbc/basics/prepared.html "Overview of Prepared Statements"