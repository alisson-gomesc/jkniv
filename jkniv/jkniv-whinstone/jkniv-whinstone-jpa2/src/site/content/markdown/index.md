Title: Introduction

# Introduction

`jkniv-whinstone-jpa2` is a Java API for communicating with JPA. 

Using easily interface <a href="jkniv.sourceforge.net/apidocs/net/sf/jkniv/whinstone/Repository.html">net.sf.jkniv.whinstone.Repository</a>
for reduce the boilerplate code for data access object.


## What is whinstone-jpa2


`jkniv-whinstone-jpa2` is a implementation of  <a href="http://martinfowler.com/eaaCatalog/repository.html">Repository pattern</a> using JPA2, Hibernate and SQLegance.

# Why Whinstone

Whinstone allow the developers worry just with implementation of view, controller and business layers. While Whinstone encapsulate all data access with the Repository pattern keeping the secure coding of SQL Injection, paginated queries and others good design practices.

The Whinstone it's a final implementation of repository pattern, where the interface is defined at <a href="jkniv.sourceforge.net/apidocs/net/sf/jkniv/whinstone/Repository.html">net.sf.jkniv.whinstone.Repository</a>, using some other frameworks.


### Requirements dependencies

`jkniv-whinstone-jpa2`require JDK 1.6 or high.


### Config web.xml For a Persistence Context via JNDI

RepositoryJpa isn't a container managed and to acquire an entity manager via JNDI is mandatory configure the web.xml with context reference name, like this:

    <persistence-context-ref>
     <description>Persistence context for my database container-managed</description>
     <persistence-context-ref-name>persistence/my-repo</persistence-context-ref-name>
     <persistence-unit-name>myUnitName</persistence-unit-name>
     <persistence-context-type>Transaction</persistence-context-type>
    </persistence-context-ref>

So RepositoryJpa can be instanced:

    new RepositoryJpa("myUnitName");


