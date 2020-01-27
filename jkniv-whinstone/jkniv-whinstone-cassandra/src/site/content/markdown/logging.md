Title: Logging

Logging
-------------
       
`Cassandra Repository` provides logging information through the use of Simple Logging Facade for Java or (SLF4J) serves as a simple facade or abstraction for various logging frameworks, e.g. java.util.logging, log4j and logback, allowing the end user to plug in the desired logging framework at deployment time. 

To active the logger configure a logger category `jkniv.whinstone.cassandra.SQL`.


| Category                           | Level     | Print |
| ---------------------------------- | --------- |--------|
| `jkniv.whinstone.cassandra.SQL` |  `trace` | result set bind |
| `jkniv.whinstone.cassandra.SQL` |  `debug` | statement parameters bind |
| `jkniv.whinstone.cassandra.SQL` |  `info`  | statement sentence |


### Log4j example

    <?xml version="1.0" encoding="UTF-8" ?>
    <!DOCTYPE log4j:configuration SYSTEM "log4j.dtd">
    <log4j:configuration debug="true"
      xmlns:log4j='http://jakarta.apache.org/log4j/'>
      
      <appender name="console" class="org.apache.log4j.ConsoleAppender">
        <layout class="org.apache.log4j.PatternLayout">
          <param name="ConversionPattern" value="%d{YYYY-MM-dd HH:mm:ss.SSS} [%t] [%-5p] %C.%M - %m%n" />
        </layout>
      </appender>
      
      <logger name="jkniv.whinstone.cassandra.SQL" additivity="false">
        <level value="debug"/>
        <appender-ref ref="console" />
      </logger>
      <root>
        <level value="warn" />
        <appender-ref ref="console" />
      </root>    
    </log4j:configuration>


### Logback example

    <configuration debug="true">
    
      <appender name="console" class="ch.qos.logback.core.ConsoleAppender">
        <withJansi>false</withJansi>
        <encoder>
          <pattern>%d{YYYY-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
      </appender>
    
      <logger name="jkniv.whinstone.cassandra.SQL" level="DEBUG"/>
    
      <root level="info">
        <appender-ref ref="console" />
      </root>
    </configuration>
    
    
<a href="http://www.slf4j.org/">SLF4J guide you for more information.</a>
