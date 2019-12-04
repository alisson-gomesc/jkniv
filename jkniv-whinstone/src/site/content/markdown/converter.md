Title: Converter

# Converters

The `Convert` annotation is applied directly to an field or method of a class.


    enum DayOfWeek {
       SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY;
    }


    public class Boo
    {
        @Converter(converter = TrueType.class, pattern = "T|F")
        private Boolean        active;
        @Converter(converter = DayOfWeek.class, isEnum = EnumType.ORDINAL)
        private DayOfWeek      deliveryDay;
        @Converter(converter = DayOfWeek.class)
        private DayOfWeek      salesDay;
        // ...
    }


| Converter                    | Example Pattern | Description        |
| ---------------------------- | --------------- | -------------------|
| TrueType                     | `Y|N` or `T|F`   | save the boolean type as `String` into database column |
| CalendarAsIntType            | `yyyyMMdd`       | save the `Calendar` type as `int` into database column |
| CalendarAsSqlTimestampType   | `yyyyMMdd`       | save the `Calendar` type as `Timestamp` into database column. Default converter for Calendar type. |
| DateAsIntType                | `yyyyMMdd`       | save the `Date` type as `int` into database column |
| DateAsSqlTimestampType       |                 | save the `Date` type as `timestamp` into database column. Default converter for Date type.


