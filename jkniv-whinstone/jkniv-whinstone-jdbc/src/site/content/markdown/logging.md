Title: Logging

Logging
-------------
       
`JDBC Repository` provides logging information through the use of Simple Logging Facade for Java or (SLF4J) serves as a simple facade or abstraction for various logging frameworks, e.g. java.util.logging, log4j and logback, allowing the end user to plug in the desired logging framework at deployment time. 

To active the logger configure a logger category `jkniv.whinstone.jdbc.SQL`.


    <logger name="jkniv.whinstone.jdbc.SQL" additivity="false">
      <level value="trace" />
      <appender-ref ref="console" />
    </logger>


| Category                         | Level     | Print |
| -------------------------------- | --------- |--------|
| `jkniv.whinstone.jdbc.SQL` |  `trace` | result set bind |
| `jkniv.whinstone.jdbc.SQL` |  `debug` | statement parameters bind |
| `jkniv.whinstone.jdbc.SQL` |  `info`  | statement sentence |


<a href="http://www.slf4j.org/">SLF4J guide you for more information.</a>
