Title: Configuring a Persistence Context

# Configuring a Persistence Context

Differently from `whinstone-jdbc` that talk with `javax.sql.DataSource`, `whinstone-jpa2` talk with a `javax.persistence.EntityManager`, that to acquire an entity manager must be available via JNDI.


For `META-INF/persistence.xml` example:

    <persistence version="2.1" xmlns="http://xmlns.jcp.org/xml/ns/persistence" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
     xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence http://xmlns.jcp.org/xml/ns/persistence/persistence_2_1.xsd">
    
     <persistence-unit name="ctx-vendor" transaction-type="JTA">
       <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>
       ...
     </persistence-unit>
    </persistence>

Basically entity manager there are 3 use cases:

**Note:** In JEE environment the prefix `persistence` is a best practice, not required. But for `whinstone-jpa2` this prefix is mandatory.

## WAR Application


`WEB-INF/web.xml` file:

    <web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://xmlns.jcp.org/xml/ns/javaee"
     xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
     id="WebApp_ID" version="3.1" metadata-complete="true">

    <persistence-context-ref>
      <description>JNDI for lookup EntityManager</description>
      <persistence-context-ref-name>persistence/ctx-vendor</persistence-context-ref-name>
      <persistence-unit-name>ctx-vendor</persistence-unit-name>
      <persistence-context-type>Transaction</persistence-context-type>
    </persistence-context-ref>

## EAR Application


`META-INF/application.xml` file:

    <application xmlns="http://xmlns.jcp.org/xml/ns/javaee" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/application_7.xsd"
      version="7">
      <description>My Vendor System</description>
      <display-name>vendor-ear</display-name>
      <module>
        <web>
          <web-uri>vendor-rest.war</web-uri>
          <context-root>/vendor-rest</context-root>
        </web>
      </module>
      <module>
        <ejb>vendor-service.jar</ejb>
      </module>
      <library-directory>lib</library-directory>
      <persistence-context-ref>
        <description>JNDI for lookup EntityManager</description>
        <persistence-context-ref-name>persistence/ctx-vendor</persistence-context-ref-name>
        <persistence-unit-name>ctx-vendor</persistence-unit-name>
        <persistence-context-type>Transaction</persistence-context-type>
      </persistence-context-ref>
    </application>

Stateless Session Bean

    @PersistenceContext(name = "persistence/ctx-vendor", unitName = "ctx-vendor")
    public class BaseFacade
    { 
        private static SqlContext sqlContext = SqlContextFactory.newInstance("/repository-sql.xml");        
        private Repository        repository;
                
        @PostConstruct
        protected void initRepo() {
            this.repository = RepositoryService.getInstance().lookup(RepositoryType.JPA).newInstance(sqlContext);
        }
        
        protected Repository getRepository() {
            return repository;
        }
    }
    
    @Stateless
    @Local(CatalogFacade.class)
    public class CatalogFacadeImpl extends BaseFacade implements CatalogFacade
    {
    }
    
## Java SE Application

TODO documentation

### Configuring Repository Context for `whinstone`


The attribute `context` from `statements` element bind the SQL context with `persistence-unit` name.
 
 
`src/main/resources/repository-sql.xml` file:
 
    <statements context="ctx-vendor"
       
      xmlns="http://jkniv.sf.net/schema/sqlegance/statements" 
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xmlns:xi="http://www.w3.org/2001/XInclude"
      xsi:schemaLocation="http://jkniv.sf.net/schema/sqlegance/statements
           http://jkniv.sf.net/schema/sqlegance/sqlegance-stmt.xsd">
    
      <select id="myselect">
        SELECT id, name from users
      </select>
    
    </statements>


So the `Repository` can be instanced:

    SqlContext sqlContext = SqlContextFactory.newInstance("/repository-sql.xml");
    Repository repository = RepositoryService.getInstance().lookup(RepositoryType.JPA).newInstance(sqlContext);


###  Java EE *versus* Java SE environments

`whinstone-jpa2` always try to acquire EntityManager by JNDI reference, however when cannot lookup that reference the EntityManager is create using `Persistence.createEntityManagerFactory(unitName)` like Java SE environments. Pay attention because instance of EntityManager for JEE is supports transaction managed but Java SE don't. If you intend use JNDI check the logger if the lookup is made correctly. 


The log at INFO level told about that:

    [INFO ] JpaEmFactorySEenv.<init> - Java SE environments factory net.sf.jkniv.whinstone.jpa2.JpaEmFactorySEenv was started successfully for unitName [ctx-vendor]. No supports for transaction managed!

    