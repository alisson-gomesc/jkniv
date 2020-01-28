Title: Download

# Download

The `jkniv-whinstone` is not deployed into public repository yet (like maven central), but you can configure the github as maven repository to resolve the dependencies.


### Configuring Github Packages as Maven Repository

Editing your ~/.m2/settings.xml file to include the token access. Create a new ~/.m2/settings.xml file if one doesn't exist.

    <settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">

    <servers>
      <server>
        <id>github</id>
        <username>alisson-gomesc</username>
        <password>01a5e0770c462db50143aca3fbc33e26516a65c4</password>
      </server>
    </servers>
    <profiles>
      <profile>
        <id>github</id>
        <repositories>
          <repository>
            <id>github</id>
            <name>GitHub Packages</name>
            <url>https://maven.pkg.github.com/alisson-gomesc/jkniv</url>
            <releases><enabled>true</enabled></releases>
            <snapshots><enabled>false</enabled></snapshots>
          </repository>
        </repositories>
      </profile>
    </profiles>
    <activeProfiles>
      <activeProfile>github</activeProfile>
    </activeProfiles>
    </settings>  
  
### package manually 

If you want a version enter (https://github.com/alisson-gomesc/jkniv) and download the source code over [LGPL 2.1]
 
    $ git clone https://github.com/alisson-gomesc/jkniv.git jkniv
    $ cd jkniv 
    $ mvn package -Dmaven.test.skip=true
    
    
[LGPL 2.1]: https://www.gnu.org/licenses/old-licenses/lgpl-2.1.en.html