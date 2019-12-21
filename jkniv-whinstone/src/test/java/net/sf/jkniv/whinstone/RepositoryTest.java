package net.sf.jkniv.whinstone;


import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import net.sf.jkniv.whinstone.domain.flat.Car;


public class RepositoryTest
{
    Repository repository;
    Queryable queryable = new QueryName("save");
    Car entity = new Car("147", "Fiat", 2);
    
    @Before
    public void setup() 
    {
        this.repository = mock(Repository.class);
        
        given(repository.add(queryable)).willReturn(1);
        given(repository.add(entity)).willReturn(1);

        given(repository.update(queryable)).willReturn(1);
        given(repository.update(entity)).willReturn(entity);

        given(repository.remove(queryable)).willReturn(1);
        given(repository.remove(entity)).willReturn(1);
        //doNothing().when(repository).remove(entity);

    }
    
    @Test
    public void whenContractMethodsFromRepositoryWorks() 
    {
        
        int rows = repository.add(queryable);
        int rows2 = repository.add(entity);
        assertThat(rows, is(1));
        assertThat(rows2, is(1));
        
        rows = repository.update(queryable);
        Car entityUpdated = repository.update(entity);
        assertThat(rows, is(1));
        assertThat(entityUpdated.getName(), is(entity.getName()));

        rows = repository.remove(queryable);
        repository.remove(entity);
        assertThat(rows, is(1));

        verify(repository).add(queryable);
        verify(repository).add(entity);
        verify(repository).update(queryable);
        verify(repository).update(entity);
        verify(repository).remove(queryable);
        verify(repository).remove(entity);
    }
    
    
}
