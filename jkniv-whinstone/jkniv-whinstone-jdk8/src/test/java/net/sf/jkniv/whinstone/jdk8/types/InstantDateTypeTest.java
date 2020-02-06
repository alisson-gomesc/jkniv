package net.sf.jkniv.whinstone.jdk8.types;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.junit.Test;

public class InstantDateTypeTest
{

    @Test
    public void whenToJdbcInstantToDateWorks() throws ParseException
    {
        InstantDateType converter = new InstantDateType();
        LocalDateTime localDateTime = LocalDateTime.of(2020, 2, 4, 20, 24, 30); 
        Instant instant = localDateTime.toInstant(ZoneOffset.UTC);
        
        assertThat(instant.toEpochMilli(), is(localDateTime.atZone(ZoneOffset.UTC).toInstant().toEpochMilli()));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S");
        String dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S").format(localDateTime);
                
        Date date = sdf.parse(dateTimeFormatter);
        
        assertThat(date.getYear(), is(2020-1900));
        assertThat(date.getMonth(), is(1));
        assertThat(date.getDate(), is(4));
        assertThat(date.getHours(), is(20));
        assertThat(date.getMinutes(), is(24));
        assertThat(date.getSeconds(), is(30));

        assertThat(converter.toJdbc(instant).getYear(), is(2020-1900));
        assertThat(converter.toJdbc(instant).getMonth(), is(1));
        assertThat(converter.toJdbc(instant).getDate(), is(4));
        assertThat(converter.toJdbc(instant).getHours(), is(20));
        assertThat(converter.toJdbc(instant).getMinutes(), is(24));
        assertThat(converter.toJdbc(instant).getSeconds(), is(30));
    }

    @Test
    public void whenToAttributeInstantToDateWorks() throws ParseException
    {
        InstantDateType converter = new InstantDateType();
        LocalDateTime localDateTime = LocalDateTime.of(2020, 2, 4, 20, 24, 30); 
        Instant instant = localDateTime.toInstant(ZoneOffset.UTC);
        
        assertThat(instant.toEpochMilli(), is(localDateTime.atZone(ZoneOffset.UTC).toInstant().toEpochMilli()));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S");
        String dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S").format(localDateTime);
                
        Date date = sdf.parse(dateTimeFormatter);
        
        assertThat(date.getYear(), is(2020-1900));
        assertThat(date.getMonth(), is(1));
        assertThat(date.getDate(), is(4));
        assertThat(date.getHours(), is(20));
        assertThat(date.getMinutes(), is(24));
        assertThat(date.getSeconds(), is(30));

        assertThat(converter.toAttribute(date).toEpochMilli(), is(date.getTime()));
    }
}
