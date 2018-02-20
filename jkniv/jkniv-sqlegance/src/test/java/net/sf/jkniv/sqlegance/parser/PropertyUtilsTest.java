package net.sf.jkniv.sqlegance.parser;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Test;

import net.sf.jkniv.sqlegance.domain.flat.Car;
import net.sf.jkniv.sqlegance.domain.orm.Address;
import net.sf.jkniv.sqlegance.domain.orm.Author;

public class PropertyUtilsTest
{

    @Test
    public void whenUseMap() throws Exception
    {
        Map<String, Object> map = new HashMap<String,Object>();
        map.put("color", "blue");
        map.put("name", "andry");
         
        map.put("car", new Car("Ford","Mustang",4));
        assertThat((String)PropertyUtils.getProperty(map, "color"),is("blue"));
        assertThat((String)PropertyUtils.getProperty(map, "name"),is("andry"));

        Car car = (Car)PropertyUtils.getProperty(map,"car");
        assertThat(car, notNullValue());
        assertThat(car.getName(), is("Ford"));
        assertThat(car.getModel(), is("Mustang"));
        assertThat(car.getDoors(), is(4));
        
        assertThat((String)PropertyUtils.getProperty(map, "car.name"),is("Ford"));
    }
    
    @Test
    public void whenReaderPropertyFromMethod() throws Exception 
    {
        Author author = new Author();
        author.setName("Bob");
        Address address = new Address("avenue may", 1005);
        author.setAddress(address);
        
        String name = (String)PropertyUtils.getProperty(author,"name");
        String addressName = (String)PropertyUtils.getProperty(author,"address.name");
        String complete = (String)PropertyUtils.getProperty(author,"address.complete");
        assertThat(name, is("Bob"));
        assertThat(addressName, is("avenue may"));
        assertThat(complete, is("avenue may, 1005"));
    }
}
