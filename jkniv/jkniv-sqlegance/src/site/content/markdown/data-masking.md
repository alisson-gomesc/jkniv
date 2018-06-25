Title: Data Masking

Data Masking
-------------
       
SQLegance provide an interface to setting which attributes with sensible data should be masked by log API. 

The default attributes masked are: `password`,`passwd`, `pwd`

New attributes can be added invoking the method `addAttrName(String)` from `SimpleDataMasking` class or a new implementation can  provider implementing the `DataMasking` interface, see <a href="repository-config.html">Repository Config</a> to know how to configure new implementations.



