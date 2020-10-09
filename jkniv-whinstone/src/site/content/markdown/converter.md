Title: Mapping Data Types

# Mapping Data Types

The converters are used to transform the data types from Java to JDBC and vice-versa. They can be used implicit or explicit way.

When are registered in `repository-config.xml` file work implicitly transforming the types and when are annotated at Field or Methods work explicitly.

- implicit converter
- explicit converter on Field (overload implicit)
- explicit converter on Method (overload Field)

Converter
--------------------

The `Convert` annotation is applied directly to an field or method of a class.

    enum DayOfWeek {
       SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY;
    }
    
    
    import net.sf.jkniv.whinstone.types.BooleanCharType;
    
    public class Boo
    {
        @Converter(converter = BooleanCharType.class, pattern = "T|F")
        private Boolean        active;
        @Converter(converter = DayOfWeek.class, isEnum = EnumType.ORDINAL)
        private DayOfWeek      deliveryDay;
        @Converter(converter = DayOfWeek.class)
        private DayOfWeek      salesDay;
        // ...
    }


| Converter             | Example Pattern | Description        | Implicit Built-in configured|
| --------------------- | --------------- | -------------------|-------|
| BooleanBitType        | `1|0`            | save `boolean` as `Integer` | no |
| BooleanCharType       | `Y|N` or `T|F`   | save `boolean` as `String`| no |
| BooleanIntType        | `1|0`            | save `boolean` as `Integer`   | no |
| BooleanVarcharType    |  `true|false`    | save `boolean` as `String` | no |
| CalendarIntType       | `yyyyMMdd`       | save `Calendar` as `Integer` | no |
| CalendarTimestampType |                 | save `Calendar` as `java.sql.Timestamp` | `yes` |
| DateIntType           | `yyyyMMdd`       | save `java.util.Date` as `int` | no |
| DateTimestampType     |                 | save `java.util.Date` as `java.sql.Timestamp` | `yes` |
| DateTimeType          |                 | save `java.util.Date` as `java.sql.Time` | no |
| DateType              |                 | save `java.util.Date` as `java.sql.Date` | no |
| DoubleBigDecimalType  |                 | save `double` as `BigDecimal` | `yes` |
| EnumNameType          |                 | save `enum` as `enum.name()` | `yes` |
| EnumOrdinalType       |                 | save `enum` as `enum.ordinal()` | no |
| IntLongType           |                 | save `Integer` as `Long` | no |
| LongBigDecimalType    |                 | save `Long` as `BigDecimal` | `yes` |
| LongNumericType       |                 | save `Long` as `BigDecimal` | `yes` |
| ShortIntType          |                 | save `Short` as `Integer`   | `yes` |


Registering Convertible
--------------------

To register a built-in converter or custom Convertible use the file `repository-config.xml` to do that:

    <repository name="user">
     <description>database for users login</description>
      <properties>
       <property name="jkniv.repository.type.InstantDateType" value="net.sf.jkniv.whinstone.jdk8.types.InstantDateType" />
      </properties>
    </repository>

1. The property name MUST start with `jkniv.repository.type`
2. The property value is a fully qualified class name that implements `Convertible` interface.

Writing a Converter
--------------------

This example we have a Java Date and want to stored into integer column (year month day).

    import net.sf.jkniv.whinstone.types.Convertible;
    import net.sf.jkniv.whinstone.types.ColumnType;
    import net.sf.jkniv.whinstone.types.JdbcType;
    
    public class DateIntType implements Convertible<Date, Integer> {
      private String pattern;
      
      public DateIntType() {
        this("yyyyMMdd");
      }
    
      public DateIntType(String pattern) {
        this.pattern = pattern;
      }
    
      @Override
      public Integer toJdbc(Date attribute) {
        if (attribute == null)
            return null;
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return Integer.valueOf(sdf.format(attribute));
      }

      @Override
      public Date toAttribute(Integer jdbc) {
        if (jdbc == null)
            return null;
        
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            return sdf.parse(String.valueOf(jdbc));
        }
        catch (ParseException e) {
            throw new ConverterException(e);
        }
      }

      @Override
      public Class<Date> getType() {
        return Date.class;
      }
    
      @Override
      public ColumnType getColumnType() {
        return JdbcType.INTEGER;
      }
    }
    
### JDK 8 

The JDK 8 is not in the core module because the compatibility with older JDK (>= 1.6) is a good feature to support the improvement of legacy projects. 

So to add native support for Java 8 types, such as the ones from the java.time API. The module `jkniv-whinstone-jdk8` is published as a separate Maven artifact:

    <dependency>
      <groupId>net.sf.jkniv</groupId>
      <artifactId>jkniv-whinstone-jdk8</artifactId>
      <version>${current-version}</version>
    </dependency>
    
These types can be registered into `repository-config.xml` file with the prefix `jkniv.repository.type` and correspondent class value:

    <property name="jkniv.repository.type.DurationStringType" value="net.sf.jkniv.whinstone.jdk8.types.DurationStringType" />
    <property name="jkniv.repository.type.InstantDateType" value="net.sf.jkniv.whinstone.jdk8.types.InstantDateType" />
    <property name="jkniv.repository.type.InstantTimestampType" value="net.sf.jkniv.whinstone.jdk8.types.InstantTimestampType" />
    
    