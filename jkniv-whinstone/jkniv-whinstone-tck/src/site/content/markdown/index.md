Title: Introduction

# Introduction

## What is Whinstone-jpa2


Whinstone-jpa2 is a implementation of  <a href="http://martinfowler.com/eaaCatalog/repository.html">Repository pattern</a> using JPA2, Hibernate and SQLegance.

# Why Whinstone

Whinstone allow the developers worry just with implementation of view, controller and business layers. While Whinstone encapsulate all data access with the Repository pattern keeping the secure coding of SQL Injection, paginated queries and others good design practices.

The Whinstone it's a final implementation of repository pattern, where the interface is defined at <a href="jkniv.sourceforge.net/apidocs/net/sf/jkniv/sqlegance/Repository.html">net.sf.jkniv.sqlegance.Repository</a> (SQLegance project), using some other frameworks.

## Simple Sample

    package com.acme.whinstone.test;
    
    import net.sf.jkniv.whinstone.jpa2.AbstractRepository;
    import net.sf.jkniv.whinstone.jpa2.test.domain.Book;

    public class BookRepository extends AbstractRepository<Book>
    {
      public BookRepository()
      {
      }
    }

    package com.acme.business;

    import net.sf.jkniv.whinstone.jpa2.test.domain.Book;
    
    import net.sf.jkniv.sqlegance.IQuery;
    import net.sf.jkniv.sqlegance.Query;
    import net.sf.jkniv.sqlegance.IRepository;
    
    public class BookBusiness
    {
       private IRepository<Parameter> repositoryBook;
    
       public BookBusiness(BookRepository repositoryBook)
       {
          this.repositoryBook = repositoryBook;
       }
        
       public Book get(String isbn)
       {
          Book b = new Book();
          b.setIsbn(isbn);
          IQuery q = new Query("jpql.getBook", b);
          return repositoryBook.get(q);
       }
    }


### Config web.xml For a Persistence Context via JNDI

RepositoryJpa isn't a container managed and to acquire an entity manager via JNDI is mandatory configure the web.xml with context reference name, like this:

    <persistence-context-ref>
     <description>Persistence context for my database container-managed</description>
     <persistence-context-ref-name>persistence/clsiv-repo</persistence-context-ref-name>
     <persistence-unit-name>myUnitName</persistence-unit-name>
     <persistence-context-type>Transaction</persistence-context-type>
    </persistence-context-ref>

So RepositoryJpa can be instanced:

    new RepositoryJpa("myUnitName");


## Road map

- Add support to Stored Procedures
- Add support more one datasources
- Add implementation from other frameworks and technologies like JDBC, XML files, etc.
- Add support to read values of java embedded objects, like: `book.author.name`. 
- Now just support one object, like: `author.name`




