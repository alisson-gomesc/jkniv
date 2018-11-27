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

- `PreCallBack`, scopes: `SELECT`, `ADD`, `UPDATE`, `REMOVE`, `NONE`
- `PostCallBack`, scopes: `SELECT`, `ADD`, `UPDATE`, `REMOVE`, `NONE`, `EXCEPTION` (pending), `COMMIT` (pending) and `LOAD` (pending)


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

 

    