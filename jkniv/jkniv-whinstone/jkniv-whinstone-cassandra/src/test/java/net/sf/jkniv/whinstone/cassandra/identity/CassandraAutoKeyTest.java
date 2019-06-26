package net.sf.jkniv.whinstone.cassandra.identity;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.sql.Statement;

import org.junit.Ignore;
import org.junit.Test;

import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.cassandra.BaseJdbc;
import net.sf.jkniv.whinstone.cassandra.model.Vehicle;


public class CassandraAutoKeyTest extends BaseJdbc
{
    @Test @Ignore("JDBC Error cannot execute SQL [null Invalid null value in condition for column plate")
    public void whenGenerateKeyWithCassandraSequence()
    {
        Repository repositoryCas = getRepository();
        Vehicle v = new Vehicle();
        v.setName("Dodge Challenger");
        Queryable queryable = QueryFactory.of("vechile-autokey-cassandra-case-sequence", v);
        int affected = repositoryCas.add(queryable);
        assertThat(affected, is(Statement.SUCCESS_NO_INFO));
        assertThat(v.getPlate(), notNullValue());
    }
}
