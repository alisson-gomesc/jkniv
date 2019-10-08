import java.util.List;
import java.util.Properties;

import net.sf.jkniv.sqlegance.QueryFactory;
import net.sf.jkniv.sqlegance.Queryable;
import net.sf.jkniv.sqlegance.Repository;
import net.sf.jkniv.sqlegance.RepositoryProperty;
import net.sf.jkniv.whinstone.jdbc.RepositoryJdbc;

public class BookService
{
    
    public static void main(String[] args)
    {
        Properties props = new Properties();
        props.put(RepositoryProperty.JDBC_URL.key(), "jdbc:derby:mydatabase;create=true");
        props.put(RepositoryProperty.JDBC_DRIVER.key(), "org.apache.derby.jdbc.EmbeddedDriver");
        //props.put(RepositoryProperty.JDBC_USER.key(), "");
        //props.put(RepositoryProperty.JDBC_PASSWORD.key(), "");
        Repository repository = new RepositoryJdbc(props);
    
        createTableIfNotExists(repository);

        insert(repository);
        
        List<Book> books = select(repository);
        for(Book b : books)
            System.out.println(b);
    }

    private static void insert(Repository repository)
    {
        Book book = new Book();
        book.setId(System.currentTimeMillis());
        book.setIsbn("978-1503250888");
        book.setName("Beyond Good and Evil");
        Queryable query = QueryFactory.newInstance("book", book);
        repository.add(query);
    }
    
    private static List<Book> select(Repository repository)
    {        
        Queryable query = QueryFactory.newInstance("allBooks");
        return repository.list(query);
    }
    
    private static void createTableIfNotExists(Repository repository)
    {
        try {
            
            repository.update(QueryFactory.newInstance("create-table"));
        } catch(Exception ignoreTableIfExists) {}
    }

}
