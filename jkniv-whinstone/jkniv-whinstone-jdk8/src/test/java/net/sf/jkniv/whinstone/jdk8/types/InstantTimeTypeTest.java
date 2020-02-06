package net.sf.jkniv.whinstone.jdk8.types;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import org.junit.Test;

public class InstantTimeTypeTest
{

    @Test
    public void whenConvertInstantToTime() throws ParseException
    {
        InstantTimeType converter = new InstantTimeType();
        LocalDateTime localDateTime = LocalDateTime.of(2020, 2, 4, 20, 24, 30); 
        Instant instant = localDateTime.toInstant(ZoneOffset.UTC);
        
        assertThat(instant.toEpochMilli(), is(localDateTime.atZone(ZoneOffset.UTC).toInstant().toEpochMilli()));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S");
        String dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S").format(localDateTime);
                
        Time time = new Time(sdf.parse(dateTimeFormatter).getTime());
        
        assertThat(time.getHours(), is(20));
        assertThat(time.getMinutes(), is(24));
        assertThat(time.getSeconds(), is(30));

        assertThat(converter.toJdbc(instant).getHours(), is(20));
        assertThat(converter.toJdbc(instant).getMinutes(), is(24));
        assertThat(converter.toJdbc(instant).getSeconds(), is(30));
    }
    
    
    @Test
    public void whenToAttributeTimestampToInstant() throws ParseException
    {
        InstantTimeType converter = new InstantTimeType();
        LocalDateTime localDateTime = LocalDateTime.of(2020, 2, 4, 20, 24, 30); 
        Instant instant = localDateTime.toInstant(ZoneOffset.UTC);
        
        assertThat(instant.toEpochMilli(), is(localDateTime.atZone(ZoneOffset.UTC).toInstant().toEpochMilli()));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S");
        String dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S").format(localDateTime);
                
        Time time = new Time(sdf.parse(dateTimeFormatter).getTime());
            
        assertThat(time.getHours(), is(20));
        assertThat(time.getMinutes(), is(24));
        assertThat(time.getSeconds(), is(30));

        assertThat(converter.toAttribute(time).toEpochMilli(), is(time.getTime()));
    }
}
