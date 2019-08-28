Title: Changelog


# Changelog

`Note: MILESTONES (.MXX) releases isn't public yet`


## Release notes: jkniv-whinstone - Version 0.6.0.M49
    
### New Feature

<ul>
 <li>[<a href='https://jkniv-io.atlassian.net/browse/JKNIVWHIN-90'>JKNIVWHIN-90</a>] -         [cassandra] foreach to execute multiple insert/update/delete
 </li>
 <li>[<a href='https://jkniv-io.atlassian.net/browse/JKNIVWHIN-93'>JKNIVWHIN-93</a>] -         [jdbc] Change behavior Diaclect by SQLFeatures configuration
 </li>
</ul>
    
### Improvement

<ul>
 <li>[<a href='https://jkniv-io.atlassian.net/browse/JKNIVWHIN-73'>JKNIVWHIN-73</a>] -         [couchdb] Paginate query using CouchDB
 </li>
</ul>

        
### Bug

<ul>
<li>[<a href='https://jkniv-io.atlassian.net/browse/JKNIVWHIN-85'>JKNIVWHIN-85</a>] -         [cassandra] Auto generate keys
</li>
<li>[<a href='https://jkniv-io.atlassian.net/browse/JKNIVWHIN-89'>JKNIVWHIN-89</a>] -         [jdbc] SQLFeatureNotSupportedException: Unsupported holdability value
</li>
<li>[<a href='https://jkniv-io.atlassian.net/browse/JKNIVWHIN-95'>JKNIVWHIN-95</a>] -         [jdbc] get(T entity) and  get(Class&lt;T&gt; returnType, Object entity) doesn&#39;t resolve queryname correctly
</li>
</ul>
            
            
## Release notes: jkniv-whinstone - Version 0.6.0.M48

    
### New Feature

 - [<a href='https://jkniv-io.atlassian.net/browse/JKNIVWHIN-40'>JKNIVWHIN-40</a>] - ![Update](images/update_icon.png "Update") [jdbc] foreach to execute multiple insert/update/delete
 - [<a href='https://jkniv-io.atlassian.net/browse/JKNIVWHIN-75'>JKNIVWHIN-75</a>] - ![Update](images/update_icon.png "Update") [jdbc] supports for cache selects result

            
### Bug

 - [<a href='https://jkniv-io.atlassian.net/browse/JKNIVWHIN-88'>JKNIVWHIN-88</a>] - ![BUG Fix](images/bug_icon.png "BUG Fix") [jdbc] connection must be release after transaction is commited or rollbacked

## Release notes - jkniv-whinstone - Version 0.6.0.M47

###Bug

 - [<a href='https://jkniv-io.atlassian.net/browse/JKNIVWHIN-86'>JKNIVWHIN-86</a>] - ![BUG Fix](images/bug_icon.png "BUG Fix") [jdbc] potential unclosed statement
 - [<a href='https://jkniv-io.atlassian.net/browse/JKNIVWHIN-87'>JKNIVWHIN-87</a>] - ![BUG Fix](images/bug_icon.png "BUG Fix") [jdbc] connection is hold when there isn&#39;t transaction
