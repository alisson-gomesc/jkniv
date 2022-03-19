JKNIV
=====

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.sf.jkniv/jkniv/badge.svg)](https://maven-badges.herokuapp.com/maven-central/net.sf.jkniv/jkniv)
[![License: LGPL v2.1](https://img.shields.io/badge/License-LGPL%20v2_1-blue.svg)](https://www.gnu.org/licenses/lgpl-2.1)
[![Twitter](https://img.shields.io/twitter/follow/BeJkniv.svg?label=Follow&style=social)](https://twitter.com/BeJkniv)

`jkniv` is set of Java libraries to help software development. A Switzerland penknife (kniv).


- [jkniv-sqlegance](http://jkniv.sourceforge.net/jkniv-sqlegance/index.html) A library for build static or dynamic SQLs from XML files, handling SQL external to java code.
- [jkniv-whinstone](http://jkniv.sourceforge.net/jkniv-whinstone/index.html) A repository implementation for database (JDBC, Cassandra, CouchDB, JPA).


## Version number

Set up new version number:

    mvn versions:set -DnewVersion=0.6.8
    
from 0.6.8 to 0.6.9
    
    mvn validate -DbumpPatch
    
from 0.6.8 to 0.7.0

    mvn validate -DbumpMinor
    
from 0.6.8 to 1.0.0
    
    mvn validate -DbumpMajor
    
    
## Deploy

Deploy on github artifacts:

    mvn deploy
    
        
Deploy on sonytype artifacts:

    mvn deploy -Drelease=true
    