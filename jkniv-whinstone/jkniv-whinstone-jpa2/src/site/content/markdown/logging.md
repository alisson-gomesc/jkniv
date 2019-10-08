Title: Logging

Logging
-------------
       
`JPA2 Repository` provides logging information through the use of Simple Logging Facade for Java or (SLF4J) serves as a simple facade or abstraction for various logging frameworks, e.g. java.util.logging, log4j and logback, allowing the end user to plug in the desired logging framework at deployment time. 

To active the logger configure a logger category `jkniv.whinstone.jpa2.SQL`.


    <logger name="jkniv.whinstone.jpa2.SQL" additivity="false">
      <level value="trace" />
      <appender-ref ref="console" />
    </logger>


| Category                         | Level     | Print |
| -------------------------------- | --------- |--------|
| `jkniv.whinstone.jpa2.SQL` |  `trace` | result set bind |
| `jkniv.whinstone.jpa2.SQL` |  `debug` | statement parameters bind |
| `jkniv.whinstone.jpa2.SQL` |  `info`  | statement sentence |


<a href="http://www.slf4j.org/">SLF4J guide you for more information.</a>
