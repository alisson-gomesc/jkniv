package net.sf.jkniv.whinstone.jdbc.dml;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import net.sf.jkniv.sqlegance.Sql;

public class DomainMock
{
    private ResultSetMetaData rsMetadata;
    private ResultSet rs;
    private Class<?> returnType;
    
    public DomainMock(ResultSetMetaData rsMetadata, ResultSet rs, Class<?> returnType)
    {
        this.rsMetadata = rsMetadata;
        this.rs = rs;
        this.returnType = returnType;
    }
    
    /**
     * Build the return type
     * @param sql instance of Sql
     * @return this builder instance
     */
    public DomainMock sql(Sql sql) 
    {
        given(sql.getReturnType()).willReturn(returnType.getName());
        doReturn(returnType).when(sql).getReturnTypeAsClass();
        return this;
    }
    
    /**
     * Build the JDBC ResultSetMetaData 
     * @param columns array of columns like {id, name, postal_code, street_name}
     * @return this builder instance
     * @throws SQLException 
     */
    public DomainMock columns(String[] columns) throws SQLException 
    {
        given(rsMetadata.getColumnCount()).willReturn(columns.length);
        for(int i=1; i<=columns.length; i++) 
        {
            given(rsMetadata.getColumnLabel(i)).willReturn(columns[i-1]);
            given(rsMetadata.getColumnName(i)).willReturn(columns[i-1]);
        }
        return this;
    }
    
    public void buildFifteenFlatBook() throws SQLException
    {
        given(rs.next()).willReturn(true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false);
        given(rs.getObject(1)).willReturn(1001L,1002L,1003L,1004L,1005L,1006L,1007L,1008L,1009L,1010L,1011L,1012L,1013L,1014L,1015L);
        given(rs.getObject(2)).willReturn("978-1503250888","978-0201895421","007-6092019909","978-0321826626","978-0321712943","978-0321984135","9788535920598","9788535921182","9788535926019","9788535922837","9788535921205","978-1557427663","978-1612931036","978-0679720201","978-0679733843");
        given(rs.getObject(3)).willReturn("Beyond Good and Evil","Analysis Patterns: Reusable Object Models","Patterns of Enterprise Application Architecture","NoSQL Distilled: A Brief Guide to the Emerging World of Polyglot Persistence","Domain-Specific Languages (Addison-Wesley Signature Series","Refactoring: Ruby Edition: Ruby Edition","Claro Enigma","Sentimento do Mundo","A Lição do Amigo","Alguma Poesia","José","The Metamorphosis","The Trial","The Stranger","The Rebel: An Essay on Man in Revolt");
        given(rs.getObject(4)).willReturn("Friedrich Nietzsche","Martin Fowler","Martin Fowler","Martin Fowler","Martin Fowler","Martin Fowler","Carlos Drummond","Carlos Drummond","Carlos Drummond","Carlos Drummond","Carlos Drummond","Franz Kafka","Franz Kafka","Albert Camus","Albert Camus");
        given(rs.getObject(5)).willReturn(1,2,2,2,2,2,3,3,3,3,3,4,4,5,5);
        //"ID", "ISBN", "NAME", "AUTHOR", "AUTHOR_ID"
    }
    
    public void buildThreeFlatAuthor() throws SQLException {
        //given(this.rsMetadata.getColumnCount()).willReturn(3);
        //given(this.rsMetadata.getColumnLabel(1)).willReturn("id");
        //given(this.rsMetadata.getColumnName(1)).willReturn("id");
        //given(this.rsMetadata.getColumnLabel(2)).willReturn("name");
        //given(this.rsMetadata.getColumnName(2)).willReturn("name");
        //given(this.rsMetadata.getColumnLabel(3)).willReturn("book");
        //given(this.rsMetadata.getColumnName(3)).willReturn("book");
        
        given(rs.next()).willReturn(true, true, false);
        given(rs.getObject(1)).willReturn(1L, 2L);
        given(rs.getObject(2)).willReturn("Author 1", "Author 2");
        given(rs.getObject(3)).willReturn(10L, 11L);

    }
}
