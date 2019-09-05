Title: Callback Methods

Callback Methods
-------------

Lifecycle callback methods must be define directly on parameters from `queryable` instance or superclass.       
       
The callback methods are annotated with annotations designating the callback events for which
they are invoked.


Callback methods defined on an parameters class or superclass have the following signature:

    public void <METHOD>()
       
**Note:** public method is mandatory, cannot be `static`.

These annotations design the lifecycle from callback methods: 
- `PreCallBack`: before save the record into database. Scopes are `SELECT`, `ADD`, `UPDATE`, `REMOVE` and `NONE`.
- `PostCallBack`: after execute the respective command at database. Scopes are `SELECT`, `ADD`, `UPDATE`, `REMOVE`, `LOAD` and `NONE`.
- `PostCallBack`: after save the record into database. Scores are `EXCEPTION` (pending), `COMMIT` (pending) and  (pending)


Example:

    public class Author {
      // ... declare attributes

      @PreCallBack(scope=CallbackScope.ADD)
      public void generateId() {
        this.id = UUID.randomUUID().toString();
      }
      
      @PreCallBack(scope=CallbackScope.UPDATE)
      public void updateAt() {
        this.updateAt = new Date();
      }
      
      @PreCallBack(scope=CallbackScope.ADD)
      public void addAt() {
        this.addAt = new Date();
      }
    }
    
One method can have many scopes.


### Exceptions from Callback methods

TODO

 

    