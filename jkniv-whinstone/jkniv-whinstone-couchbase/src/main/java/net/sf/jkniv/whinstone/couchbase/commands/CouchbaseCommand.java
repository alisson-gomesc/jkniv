package net.sf.jkniv.whinstone.couchbase.commands;

public enum CouchbaseCommand
{
    COUNTER("counter")
    ,GET("get")
    ,GET_AND_LOCK("getAndLock"), GET_AND_TOUCH("getAndTouch"), GET_FROM_REPLICA("getFromReplica")
    ,TOUCH("touch"), APPEND("append"), PREPEND("prepend")
    ,UNLOCK("unlock")
    ,EXISTS("exists")
    ,INSERT("insert"), UPSERT("upsert"), REPLACE("replace"), REMOVE("remove")
    ;
    
    private String commandName;
    
    private CouchbaseCommand(String commandName)
    {
        this.commandName = commandName;
    }
    
    public String getCommandName()
    {
        return commandName;
    }
}
