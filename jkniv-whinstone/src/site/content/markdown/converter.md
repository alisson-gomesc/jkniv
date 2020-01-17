Title: Mapping Data Types

# Mapping Data Types

There are two ways to mapping data from java to database:

- **Translate**: is an implicit conversion, applied over the type retrieved from database and translate to java type when the types mismatch. For example: the column is `timestamp` and java type is a `java.util.Calendar`. Another common scenario is when the column is `Decimal` and java type is `java.lang.Double`.
- **Converter**: is an explicit conversion, applied before set data to POJO value from java to database and vice-versa, always work the two ways. The **Converter** has preference over **Translate**.


## Converter

The `Convert` annotation is applied directly to an field or method of a class.

    enum DayOfWeek {
       SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY;
    }


    public class Boo
    {
        @Converter(converter = BooleanStringType.class, pattern = "T|F")
        private Boolean        active;
        @Converter(converter = DayOfWeek.class, isEnum = EnumType.ORDINAL)
        private DayOfWeek      deliveryDay;
        @Converter(converter = DayOfWeek.class)
        private DayOfWeek      salesDay;
        // ...
    }


| Converter                    | Example Pattern | Description        |
| ---------------------------- | --------------- | -------------------|
| BooleanStringType     | `Y|N` or `T|F`   | save the boolean type as `String` into database column |
| CalendarIntType       | `yyyyMMdd`       | save the `Calendar` type as `int` into database column |
| DateIntType           | `yyyyMMdd`       | save the `Date` type as `int` into database column |
| DateTimestampType     |                 | save the `Date` type as `timestamp` into database column. Default converter for Date type.
| CalendarTimestampType | `yyyyMMdd`       | save the `Calendar` type as `Timestamp` into database column. Default converter for Calendar type. |


