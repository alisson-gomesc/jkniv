package net.sf.jkniv.whinstone.jdk8.types;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.junit.Test;

public class InstantTimestampTypeTest
{

    @Test
    public void whenToJdbcInstantToTimestampWorks() throws ParseException
    {
        InstantTimestampType converter = new InstantTimestampType();
        LocalDateTime localDateTime = LocalDateTime.of(2020, 2, 4, 20, 24, 30); 
        Instant instant = localDateTime.toInstant(ZoneOffset.UTC);
        
        assertThat(instant.toEpochMilli(), is(localDateTime.atZone(ZoneOffset.UTC).toInstant().toEpochMilli()));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S");
        String dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S").format(localDateTime);
                
        Timestamp timestamp = new Timestamp(sdf.parse(dateTimeFormatter).getTime());
        
        assertThat(timestamp.getYear(), is(2020-1900));
        assertThat(timestamp.getMonth(), is(1));
        assertThat(timestamp.getDate(), is(4));
        assertThat(timestamp.getHours(), is(20));
        assertThat(timestamp.getMinutes(), is(24));
        assertThat(timestamp.getSeconds(), is(30));

        assertThat(converter.toJdbc(instant).getYear(), is(2020-1900));
        assertThat(converter.toJdbc(instant).getMonth(), is(1));
        assertThat(converter.toJdbc(instant).getDate(), is(4));
        assertThat(converter.toJdbc(instant).getHours(), is(20));
        assertThat(converter.toJdbc(instant).getMinutes(), is(24));
        assertThat(converter.toJdbc(instant).getSeconds(), is(30));
    }
    
    @Test
    public void whenToAttributeTimestampToInstant() throws ParseException
    {
        InstantTimestampType converter = new InstantTimestampType();
        LocalDateTime localDateTime = LocalDateTime.of(2020, 2, 4, 20, 24, 30); 
        Instant instant = localDateTime.toInstant(ZoneOffset.UTC);
        
        assertThat(instant.toEpochMilli(), is(localDateTime.atZone(ZoneOffset.UTC).toInstant().toEpochMilli()));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S");
        String dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S").format(localDateTime);
                
        Timestamp timestamp = new Timestamp(sdf.parse(dateTimeFormatter).getTime());
        
        assertThat(timestamp.getYear(), is(2020-1900));
        assertThat(timestamp.getMonth(), is(1));
        assertThat(timestamp.getDate(), is(4));
        assertThat(timestamp.getHours(), is(20));
        assertThat(timestamp.getMinutes(), is(24));
        assertThat(timestamp.getSeconds(), is(30));

        assertThat(converter.toAttribute(timestamp).toEpochMilli(), is(timestamp.getTime()));
    }
}
