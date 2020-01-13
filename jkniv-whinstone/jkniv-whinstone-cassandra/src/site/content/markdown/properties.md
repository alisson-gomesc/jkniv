Title: Cassandra Properties

Registering Cassandra Codecs
-------------

Cassandra has “extras” drivers module to provide additional codec implementations.

The codecs supported are: 

| Type          | start with      | example                        | codecs |
| ------------- | --------------- | -------------------------------|--------|
| **arrays**    | *codec.arrays.* | `codec.arrays.LongArrayCodec`    | `DoubleArrayCodec`, `FloatArrayCodec`, `IntArrayCodec`, `LongArrayCodec` and `ObjectArrayCodec` | 
| **date**      | *codec.date.*   | `codec.date.SimpleTimestampCodec`| `SimpleDateCodec` and `SimpleTimestampCodec` |
| **enums**     | *codec.enums.*  | `codec.enums.EnumNameCodec`      | `EnumNameCodec` and `EnumOrdinalCodec` |
| **guava**     | *codec.guava.*  | `codec.guava.OptionalCodec`      | `OptionalCodec` |
| **jdk time**  | *codec.jdk8.*   | `codec.jdk8.InstantCodec`        | `InstantCodec`, `LocalDateCodec`, `LocalDateTimeCodec`, `LocalTimeCodec`, `OptinalCodec`, `ZonedDateTimeCodec` and `ZonaIdCodec` |
| **joda**      | *codec.joda.*   | `codec.joda.LocalTimeCodec`      | `DateTimeCodec`, `InstantCodec`, `LocalDateCodec` and `LocalTimeCodec` |
| **json**      | *codec.json.*   | `codec.json.JacksonJsonCodec`    | `JacksonJsonCodec` |


Example how to register a codec:

    <repository name="users">
      ...
      <properties>
        <property name="codec.jdk8.InstantCodec" value="true" />
      </properties>
    </repository>
    
    
jkniv.repository.protocol_version