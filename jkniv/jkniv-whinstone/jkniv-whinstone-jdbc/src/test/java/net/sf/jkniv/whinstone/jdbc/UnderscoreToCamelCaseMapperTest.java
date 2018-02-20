package net.sf.jkniv.whinstone.jdbc;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;


import org.junit.Test;

import net.sf.jkniv.sqlegance.UnderscoreToCamelCaseMapper;

public class UnderscoreToCamelCaseMapperTest
{
    
    @Test 
    public void whenMapperUnderscoreToCameCase()
    {
        UnderscoreToCamelCaseMapper mapper = new UnderscoreToCamelCaseMapper();
        
        assertThat(mapper.map("CUSTOMER_ID"), is("customerId"));
        assertThat(mapper.map("CODE_NAME"), is("codeName"));
        assertThat(mapper.map("CODE"), is("code"));
        assertThat(mapper.map("code"), is("code"));
        assertThat(mapper.map("row_number_id"), is("rowNumberId"));
        assertThat(mapper.map("ROW_NUMBER_ID"), is("rowNumberId"));
        assertThat(mapper.map("rowNumberId"), is("rownumberid"));
        assertThat(mapper.map("author.name"), is("author.name"));
        assertThat(mapper.map("author.phoneNumber"), is("author.phonenumber"));
        
    }
}
