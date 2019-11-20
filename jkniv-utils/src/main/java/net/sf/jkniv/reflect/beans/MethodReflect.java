package net.sf.jkniv.reflect.beans;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.sf.jkniv.exception.HandleableException;
import net.sf.jkniv.exception.HandlerException;
import net.sf.jkniv.reflect.ObjectNotFoundException;
import net.sf.jkniv.reflect.ReflectionException;
import net.sf.jkniv.reflect.beans.Capitalize.PropertyType;

/**
 * Resolve {@link Field} attribute from specific class target with supports for nested field.
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
class MethodReflect
{
    private final HandleableException handler;
    private final static Capitalize CAPITAL_IS = CapitalNameFactory.getInstanceOfIs();
    private final static Capitalize CAPITAL_GET = CapitalNameFactory.getInstanceOfGetter();
    
    public MethodReflect()
    {
        handler = new HandlerException(ReflectionException.class, "Error into reflect this class")
                .config(NoSuchMethodException.class, ObjectNotFoundException.class,
                "[NoSuchMethodException] Can not found the method [%s]");
    }
    
    public MethodReflect(HandleableException handler)
    {
        this.handler = handler;
    }
    
    public Method getMethod(String name, Class<?> classTarget)
    {
        return getMethod(name, classTarget, CapitalNameFactory.getInstanceOfGetter());
    }
    
    public Method getMethod(String name, Class<?> classTarget, Capitalize capitalize)
    {
        Method nestedMethod = null;
        Class<?> nestedClass = classTarget;
        String[] nestedMethodNames = name.split("\\.");
        
        if (nestedMethodNames == null || nestedMethodNames.length == 0)
            throw new ObjectNotFoundException("Can not found the method [" + classTarget.getName() + "." + name + "]");
        
        int lastIndex = nestedMethodNames.length-1;
        for (int i = 0; i <= lastIndex; i++)
        {
            try
            {
                //example: Field nested= c.getDeclaredField("address").getType().getDeclaredField("description");
                if (i < lastIndex)
                {
                    nestedMethod = nestedClass.getDeclaredMethod(CAPITAL_GET.does(nestedMethodNames[i]));
                    nestedClass = nestedMethod.getReturnType();  
                }
                else
                {
                    ObjectProxy<?> proxy = ObjectProxyFactory.of(nestedClass);
                    nestedMethod = proxy.getDeclaredMethod(capitalize.does(nestedMethodNames[i]));
                    if (nestedMethod != null)
                        nestedClass = nestedMethod.getReturnType();
                    else if (capitalize.getPropertyType() == PropertyType.GET)
                    {
                        nestedMethod = nestedClass.getDeclaredMethod(CAPITAL_IS.does(nestedMethodNames[i]));                        
                    }
                }
            }
            catch (Exception e)
            {
                if (e instanceof NoSuchMethodException && capitalize.getPropertyType() == PropertyType.GET)
                {
                    try
                    {
                        nestedMethod = nestedClass.getDeclaredMethod(CAPITAL_IS.does(nestedMethodNames[i]));
                    }
                    catch (Exception ignore)
                    {
                        handler.handle(e);                        
                    }
                }
                else
                    handler.handle(e);
            }
        }
        return nestedMethod;
    }
}
