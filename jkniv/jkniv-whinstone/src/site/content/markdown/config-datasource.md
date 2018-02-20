Title: Datasource config

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

