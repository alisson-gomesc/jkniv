package net.sf.jkniv.hibernate4.types;

import static java.lang.Byte.toUnsignedInt;
import static java.lang.Math.toIntExact;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Period;
import java.util.Objects;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.OracleResultSet;
import oracle.sql.INTERVALDS;
import oracle.sql.INTERVALYM;

/**
 * INTERVAL DAY TO SECOND
 * 
 * https://github.com/marschall/threeten-jpa
 *
 */
public class IntervalDsUserType implements UserType
{
    public static final IntervalDsUserType INSTANCE = new IntervalDsUserType();
    private static final Logger        log      = LoggerFactory.getLogger(IntervalDsUserType.class);
    private static final int HIGH_BIT_FLAG = 0x80000000;
    private static final int SIZE_INTERVALYM = 5;
    private static final int SIZE_INTERVALDS = 11;
    
    
    @Override
    public int[] sqlTypes()
    {
        return new int[]
        { oracle.jdbc.OracleTypes.INTERVALDS };
    }
    
    @Override
    public Class returnedClass()
    {
        return Duration.class;
    }
    
    @Override
    public boolean equals(Object x, Object y) throws HibernateException
    {
        return Objects.equals(x, y);
    }
    
    @Override
    public int hashCode(Object x) throws HibernateException
    {
        return Objects.hashCode(x);
    }
    
    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
            throws HibernateException, SQLException
    {
        
        OracleResultSet oracleResultSet = rs.unwrap(OracleResultSet.class);
        INTERVALDS intervalds = oracleResultSet.getINTERVALDS(names[0]);
        Duration duration = null;
        if(intervalds != null) 
            duration = intervaldsToDuration(intervalds);//Duration.ofMillis(intervalds.timeValue().getTime());
        
        log.debug("Result get column [{}] value is [{}]", names[0], duration);
        return duration;
    }
    
    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
            throws HibernateException, SQLException
    {
        OraclePreparedStatement oracleStmt = st.unwrap(OraclePreparedStatement.class);
        
        if (value == null)
        {
            log.debug("Binding null to parameter [{}] index", index);
            st.setNull(index, oracle.jdbc.OracleTypes.INTERVALDS);
        }
        else
        {
            //INTERVALDS intervalds = new INTERVALDS("INTERVAL '" + ((Duration) value).getSeconds() + "' SECONDS");
            INTERVALDS intervalds = durationToIntervalds((Duration) value);
            log.debug("Binding [{}] to parameter [{}] ", value, index);
            oracleStmt.setINTERVALDS(index, intervalds);
        }
    }
    
    @Override
    public Object deepCopy(Object value) throws HibernateException
    {
        return value;
    }
    
    @Override
    public boolean isMutable()
    {
        return false;
    }
    
    @Override
    public Serializable disassemble(Object value) throws HibernateException
    {
        return (Duration) deepCopy(value);
    }
    
    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException
    {
        return deepCopy(cached);
    }
    
    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException
    {
        return deepCopy(original);
    }
    
    private Duration intervaldsToDuration(INTERVALDS intervalds) {
        if (intervalds == null) {
          return null;
        }

        byte[] bytes = intervalds.toBytes();
        int day = toUnsignedInt(bytes[0]) << 24
                | toUnsignedInt(bytes[1]) << 16
                | toUnsignedInt(bytes[2]) << 8
                | toUnsignedInt(bytes[3]);
        day ^= HIGH_BIT_FLAG;
        int hour = toUnsignedInt(bytes[4]) - 60;
        int minute = toUnsignedInt(bytes[5]) - 60;
        int second = toUnsignedInt(bytes[6]) - 60;
        int nano = toUnsignedInt(bytes[7]) << 24
                | toUnsignedInt(bytes[8]) << 16
                | toUnsignedInt(bytes[9]) << 8
                | toUnsignedInt(bytes[10]);
        nano ^= HIGH_BIT_FLAG;
        
        return Duration.ofDays(day)
                .plusHours(hour)
                .plusMinutes(minute)
                .plusSeconds(second)
                .plusNanos(nano);
      }
    
    private INTERVALDS durationToIntervalds(Duration attribute) {
        if (attribute == null) {
          return null;
        }
        byte[] bytes = newIntervaldsBuffer();

        long totalSeconds = attribute.getSeconds();
        if (attribute.isNegative()) {
          //
          totalSeconds += 1L;
        }
        // computation happens in UTC
        // lead seconds are sort of an issue

        int day = toIntExact(totalSeconds / 24L / 60L / 60L);
        int hour = (int) ((totalSeconds / 60L / 60L) - (day * 24L));
        int minute = (int) ((totalSeconds / 60L) - (day * 24L * 60L) - (hour * 60L));
        int second = (int) (totalSeconds % 60);
        int nano;
        if (attribute.isNegative()) {
          // Java represents -10.1 seconds as
          //  -11 seconds
          //  +900000000 nanoseconds
          // Oracle wants -10.1 seconds as
          //  -10 seconds
          //  -100000000 nanoseconds
          nano = -(1_000_000_000 - attribute.getNano());
        } else {
          nano = attribute.getNano();
        }
        nano ^= HIGH_BIT_FLAG;
        day ^= HIGH_BIT_FLAG;

        hour += 60;
        minute += 60;
        second += 60;

        bytes[0] = (byte) (day >> 24);
        bytes[1] = (byte) (day >> 16 & 0xFF);
        bytes[2] = (byte) (day >> 8 & 0xFF);
        bytes[3] = (byte) (day & 0xFF);

        bytes[4] = (byte) (hour & 0xFF);
        bytes[5] = (byte) (minute & 0xFF);
        bytes[6] = (byte) (second & 0xFF);

        bytes[7] = (byte) (nano >> 24);
        bytes[8] = (byte) (nano >> 16 & 0xFF);
        bytes[9] = (byte) (nano >> 8 & 0xFF);
        bytes[10] = (byte) (nano & 0xFF);

        return new INTERVALDS(bytes);
    }


    
    private INTERVALYM periodToIntervalym(Period attribute) {
        if (attribute == null) {
          return null;
        }
        if (attribute.getDays() != 0) {
          throw new IllegalArgumentException("days are not supported");
        }
        byte[] bytes = newIntervalymBuffer();

        int year = attribute.getYears() ^ HIGH_BIT_FLAG;
        bytes[0] = (byte) (year >> 24);
        bytes[1] = (byte) (year >> 16 & 0xFF);
        bytes[2] = (byte) (year >> 8 & 0xFF);
        bytes[3] = (byte) (year & 0xFF);

        int month = attribute.getMonths() + 60;
        bytes[4] = (byte) (month & 0xFF);

        return new INTERVALYM(bytes);
    }

    private byte[] newIntervaldsBuffer() {
        return new byte[SIZE_INTERVALDS];
    }
    
    private byte[] newIntervalymBuffer() {
        return new byte[SIZE_INTERVALYM];
    }
}
