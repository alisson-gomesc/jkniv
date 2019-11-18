package net.sf.jkniv.reflect.beans;

import java.lang.reflect.Field;

import net.sf.jkniv.exception.HandleableException;
import net.sf.jkniv.exception.HandlerException;
import net.sf.jkniv.reflect.ObjectNotFoundException;
import net.sf.jkniv.reflect.ReflectionException;

class FieldReflect
{
    private final HandleableException handler;
    
    public FieldReflect()
    {
        handler = new HandlerException(ReflectionException.class, "Error into reflect this class")
                .config(NoSuchFieldException.class, ObjectNotFoundException.class, "[NoSuchFieldException] Can not found the field [%s]");
    }

    public FieldReflect(HandleableException handler)
    {
        this.handler = handler;
    }

    public Field getField(String name, Class<?> classTarget)
    {
        Field nestedField = null;
        Class<?> nestedClass = classTarget;
        String[] nestedMethodsNames = name.split("\\.");
        
        if (nestedMethodsNames == null || nestedMethodsNames.length == 0)
            throw new ObjectNotFoundException("Can not found the field ["+classTarget.getName() + "." + name +"]");
        
        //int found = 0;
        for(int i=0; i<nestedMethodsNames.length; i++)
        {
            try
            {
                //example: Field nested= c.getDeclaredField("address").getType().getDeclaredField("description");
                nestedField = nestedClass.getDeclaredField(nestedMethodsNames[i]);
                nestedClass = nestedField.getType();
                //found++;
            }
            catch (Exception e)
            {
                handler.handle(e);
            }
        }
//        if(found != nestedMethodsNames.length)
//            handler.throwMessage("The field %s was found into %, but must be from %s", 
//                    nestedField.getName(), nestedField.getDeclaringClass().getName(), name);
        
        return nestedField;
    }
}
