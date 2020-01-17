Title: Bean validation

# Bean Validation


Automatic validation is achieved through Java API for bean validation using annotations such as `@NotNull`, `@Min`,`@Max`, `@Size` etc.


- Bean Validation 1.0 (JSR 303) `not tested`
- Bean Validation 1.1 (JSR 349) `supported`
- Bean Validation 2.0 (JSR 380) `no tested`

Example:

    import net.sf.jkniv.sqlegance.validation.AddValidate;
    import net.sf.jkniv.sqlegance.validation.UpdateValidate;
    
    public class User
    {
        @NotNull(groups = { UpdateValidate.class, RemoveValidate.class })
        private Long id;
    
        @Email(groups = { UpdateValidate.class, AddValidate.class })
        private String email;
        
        @NotNull(groups = { UpdateValidate.class })
        private Date lastUpdate;
    }


The `@NotNull`, `@Email` annotations are used to declare the constraints which should be applied to the fields of a User instance while `AddValidate`, `UpdateValidate` and `RemoveValidate` are used to say when apply the validation:


 - `id` must never be null for update or remove operations.
 - `email` must have a correct format for update and add operations.
 - `lastUpdate` must never be null for update operations.


Beyond the annotations for the field is necessary specify the SQL to be validate when execute it, example:

    <insert id="save-user" validation="ADD">
      insert into users (email, lastUpdate) values (:email, :lastUpdate)
    </insert>
    
    <update id="update-user" validation="UPDATE">
      update users set email = :email, lastUpdate = :lastUpdate where id = :id
    </update>
      

The validations are applied before execute the methods from `Repository`:  

 - `NONE` not apply validation, is default behavior.
 - `GET` before *get* method
 - `LIST` before *list* method
 - `SELECT` before *list* and *get* methods
 - `SCALAR` before *scalar* method
 - `ADD` before *add* method
 - `UPDATE` before *update* method
 - `ENRICH` before *enrich* method
 - `REMOVE` before *remove* method
 - `ALL` before any (*get*, *list*, *add*, *update*, *remove*, *enrich*, *scalar*) method
 