/* 
 * JKNIV, utils - Helper utilities for jdk code.
 * 
 * Copyright (C) 2017, the original author or authors.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software Foundation, Inc., 
 * 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package net.sf.jkniv.reflect.beans;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.asserts.Assertable;
import net.sf.jkniv.asserts.AssertsFactory;
import net.sf.jkniv.exception.HandleableException;
import net.sf.jkniv.exception.HandlerException;
import net.sf.jkniv.reflect.BasicType;
import net.sf.jkniv.reflect.ReflectionException;

/*
 * TODO list Reflection ObjectProxy
 * Convert number type to near method BigDecimal->Double->Float->Long->Integer->Short
 * 
 * @param <T> Type of proxy class
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
@SuppressWarnings("unchecked")
class DefaultObjectProxy<T> implements ObjectProxy<T>
{
    private static final Logger       LOG        = LoggerFactory.getLogger(DefaultObjectProxy.class);
    private static final Assertable   NOT_NULL    = AssertsFactory.getNotNull();
    private static final Assertable   IS_NULL     = AssertsFactory.getIsNull();
    //private static final Capitalize   GETTER     = MethodNameFactory.getInstanceGetter();
    private static final Capitalize   CAPITAL_SETTER     = CapitalNameFactory.getInstanceOfSetter();
    private static final BasicType    BASIC_TYPE = BasicType.getInstance();
    /** {@code getClass} */
    private static final List<String> SKIP_NAMES = Arrays.asList("getClass");
    //private final Map<String, MethodInfo> cacheMethods;
    private T                         instance;
    private Class<T>                  targetClass;
    private Object[]                  constructorArgs;
    private Class<?>[]                constructorTypes;
    private Constructor<T>[]          constructors;
    private boolean                   isWrapperType;
    private HandleableException       handleException;
    private Invokable                 basicInvoke;
    private Invokable                 pojoInvoke;
    private Invokable                 nestedInvoke;
    private Invokable                 mapInvoke;
    private Invokable                 collectionInvoke;
    
    public DefaultObjectProxy(String className)
    {
        this(null, null, className);
    }
    
    public DefaultObjectProxy(T target)
    {
        this(target, null, null);
    }
    
    public DefaultObjectProxy(Class<T> targetClazz)
    {
        this(null, targetClazz, null);
    }
    
    private DefaultObjectProxy(T target, Class<T> targetClazz, String className)
    {
        this.handleException = new HandlerException(ReflectionException.class, "");// TODO exception handler design
        //this.cacheMethods = new HashMap<String, MethodInfo>(3);
        if (target != null)
        {
            this.instance = target;
            this.targetClass = (Class<T>) target.getClass();
        }
        else if (targetClazz != null)
        {
            this.targetClass = targetClazz;
        }
        else if (className != null)
        {
            this.targetClass = forName(className);
        }
        else
            this.handleException.throwMessage(
                    "Cannot create proxy object least one argument must be not null target [%s], targetClass [%s], className [%s]",
                    target, targetClazz, className);
        isWrapperType = BASIC_TYPE.isBasicType(targetClass);
        init();
        this.basicInvoke = new BasicInvoker(this.handleException);
        this.pojoInvoke = new PojoInvoker((BasicInvoker) basicInvoke, this.handleException);
        this.nestedInvoke = new NestedInvoker((PojoInvoker) pojoInvoke, this.handleException);
        this.collectionInvoke = new CollectionInvoker(this.handleException);
        this.mapInvoke = new MapInvoker(this.handleException);
    }
    
    @Override
    public ObjectProxy<T> mute(Class<? extends Exception> ex)
    {
        this.handleException.mute(ex);
        return this;
    }
    
    private void init()
    {
        this.constructors = (Constructor<T>[]) this.targetClass.getConstructors();
        this.constructorArgs = new Object[0];
        this.constructorTypes = new Class<?>[0];
        
        this.handleException
                .config(IllegalAccessException.class,
                        "[IllegalAccessException] -> Cannot create, get or invoke class method %s")
                .config(IllegalArgumentException.class, "[IllegalArgumentException] -> cannot invoke %s")
                .config(InvocationTargetException.class, "[InvocationTargetException] -> Cannot invoke %s")
                .config(ClassNotFoundException.class,
                        "[ClassNotFoundException] -> no definition for the class with the specified name could be found %s")
                .config(InstantiationException.class,
                        "[InstantiationException] -> Cannot create new instance of class %s")
                .config(NoSuchMethodException.class, "[NoSuchMethodException] -> Cannot invoke or get the method %s")
                .config(NoSuchFieldException.class, "[NoSuchFieldException] -> Cannot get the field %s")
                .config(SecurityException.class, "[SecurityException] -> Cannot invoke or get method %s");
        
        /* newInstance
          # IllegalAccessException - if the class or its nullary constructor is not accessible.
          # InstantiationException - if this Class represents an abstract class, an interface, an array class, a primitive type, or void; or if the class has no nullary constructor; or if the instantiation fails for some other reason.
          # ExceptionInInitializerError - if the initialization provoked by this method fails.
          # SecurityException - If a security manager, s, is present and any of the following conditions is met: • invocation of s.checkMemberAccess(this, Member.PUBLIC) denies creation of new instances of this class 
         */
    }
    
    /* (non-Javadoc)
     * @see net.sf.jkniv.reflect.beans.ObjectProxy#setConstructorArgs(java.lang.Object)
     */
    @Override
    public void setConstructorArgs(Object... constructorArgs)
    {
        NOT_NULL.verifyArray(constructorArgs);
        this.constructorArgs = constructorArgs;
        if (this.constructorTypes.length == 0)
        {
            this.constructorTypes = getTypes(constructorArgs);
        }
    }
    
    // match exactly the same types, fail when have null values
    private Constructor<T> getMatchStrictContructor() // TODO test me
    {
        Constructor<T> constructor = null;
        
        for (Constructor<T> c : this.constructors)
        {
            Class<?>[] cargs = c.getParameterTypes();
            if (cargs != null && cargs.length == this.constructorArgs.length)
            {
                int match = 0;
                for (int i = 0; i < this.constructorArgs.length; i++)
                {
                    if (this.constructorArgs[i] != null
                            && cargs[i].isAssignableFrom(this.constructorArgs[i].getClass()))
                    {
                        match++;
                    }
                }
                if (match == countNotNull(this.constructorArgs))
                {
                    //constructor = c;
                    return c;
                }
            }
        }
        constructor = getProbablyContructor();
        return constructor;
    }
    
    private Constructor<T> getProbablyContructor() // TODO test me
    {
        Constructor<T> constructorFound = null;
        double biggerProbably = 0D;
        
        for (Constructor<T> classConstructor : this.constructors)
        {
            Class<?>[] cargs = classConstructor.getParameterTypes();
            if (cargs != null && cargs.length == this.constructorArgs.length)
            {
                double probably = 0D;
                int match = 0;
                for (int i = 0; i < this.constructorArgs.length; i++)
                {
                    if (this.constructorArgs[i] != null)
                    {
                        if (cargs[i].isAssignableFrom(this.constructorArgs[i].getClass()))
                            match++;
                        else
                            LOG.warn("The [{}] parameter from type [{}] isn't assignable to [{}]", i,
                                    cargs[i].getName(), this.constructorArgs[i].getClass());
                    }
                }
                probably = ((double) match / (double) this.constructorArgs.length);
                if (probably > biggerProbably)
                {
                    biggerProbably = probably;
                    constructorFound = classConstructor;
                }
            }
        }
        return constructorFound;
    }
    
    private int countNotNull(Object[] args)
    {
        int count = 0;
        for (int i = 0; i < args.length; i++)
        {
            if (args[i] != null)
                count++;
        }
        return count;
    }
    
    /* (non-Javadoc)
     * @see net.sf.jkniv.reflect.beans.ObjectProxy#setConstructorTypes(java.lang.Class)
     */
    @Override
    public void setConstructorTypes(Class<?>... constructorTypes)
    {
        NOT_NULL.verifyArray(constructorTypes);
        this.constructorTypes = constructorTypes;
    }
    
    /* (non-Javadoc)
     * @see net.sf.jkniv.reflect.beans.ObjectProxy#newInstance()
     */
    @Override
    public T newInstance()
    {
        IS_NULL.verify(new ReflectionException("Bean already have an instance cannot create another one"),
                this.instance);
        
        try
        {
            Constructor<T> constructor = getMatchStrictContructor();
            if (constructor == null && this.constructorTypes.length > 0)
            {
                constructor = this.targetClass.getConstructor(this.constructorTypes);
                this.instance = constructor.newInstance(this.constructorArgs);
            }
            else if (constructor != null)
            {
                this.instance = constructor.newInstance(constructorArgs);
            }
            else
            {
                this.instance = targetClass.newInstance();
            }
            //            }
        }
        //TODO design exception handler InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
        catch (Exception e)
        {
            handleException.handle(e);
        }
        return this.instance;
    }
    
    /* (non-Javadoc)
     * @see net.sf.jkniv.reflect.beans.ObjectProxy#hasInstance()
     */
    @Override
    public boolean hasInstance()
    {
        return (this.instance != null);
    }
    
    /* (non-Javadoc)
     * @see net.sf.jkniv.reflect.beans.ObjectProxy#isInstanceof(java.lang.Class)
     */
    @Override
    public boolean isInstanceof(Class<?> clazz)
    {
        NOT_NULL.verify(clazz);
        return (clazz.isInstance(this.instance));
    }
    
    /* (non-Javadoc)
     * @see net.sf.jkniv.reflect.beans.ObjectProxy#getInstance()
     */
    @Override
    public T getInstance()
    {
        //notNull.verify(new NullPointerException("Instance of Bean [" + this.targetClass.getName() + "] is null"), this.instance);
        return this.instance;
    }
    
    /* (non-Javadoc)
     * @see net.sf.jkniv.reflect.beans.ObjectProxy#getTargetClass()
     */
    @Override
    public Class<T> getTargetClass()
    {
        return this.targetClass;
    }

    @Override
    public Object invoke(Method method, Object... args) // TODO write unit test
    {
        return invokeDirect(method, this.instance, args);
    }
    
    private Object invokeDirect(Method method, Object theInstance, Object... values)
    {
        Object ret = null;
        try
        {
            //
            if (method.getParameterTypes().length > 0)
                ret = method.invoke(theInstance, values);
            else
                ret = method.invoke(theInstance);
        }
        //IllegalAccessException, IllegalArgumentException, InvocationTargetException
        catch (Exception e)
        {
            this.handleException.handle(e, e.getMessage() + " method [" + method + "] type of(" + StringUtil.arrayToClass(values)
                    + ") values of (" + StringUtil.arrayToString(values) + ")");
        }
        return ret;
    }

    
    /* (non-Javadoc)
     * @see net.sf.jkniv.reflect.beans.ObjectProxy#invoke(java.lang.String, java.lang.Object)
     */
    @Override
    public Object invoke(String methodName, Object... args) // TODO write unit test
    {
        Object ret = null;
        if (isWrapperType)
        {
            this.instance = (T) basicInvoke.invoke(targetClass, args);
        }
        else
        {
            if (!hasInstance())
                newInstance();
            
            if (this.instance instanceof Map)
                ret = mapInvoke.invoke(methodName, instance, args);
            else if (this.instance instanceof Collection)
                ret = collectionInvoke.invoke(methodName, instance, args);
            else if (isNestedMethod(methodName))
                ret = nestedInvoke.invoke(methodName, instance, args);
            else
                ret = pojoInvoke.invoke(methodName, instance, args);
        }
        return ret;
    }
    
    @Override
    public T from(Object object)
    {
        NOT_NULL.verify(object);
        if (object instanceof Map)// TODO test me merge Object <- Map
        {
            Map<String, Object> map = (Map) object;
            for (Entry<String, Object> entry : map.entrySet())
            {
                invoke(CAPITAL_SETTER.does(entry.getKey()), entry.getValue());
            }
        }
        else
        {
            Method[] methods = object.getClass().getMethods();
            for (Method method : methods)
            {
                if (SKIP_NAMES.contains(method.getName()))
                    continue;
                
                if (method.getName().startsWith("get"))
                {
                    Object v = pojoInvoke.invoke(method, object);
                    invoke(CAPITAL_SETTER.does(method.getName().substring(3)), v);
                }
                else if (method.getName().startsWith("is"))
                {
                    Object v = pojoInvoke.invoke(method, object);
                    invoke(CAPITAL_SETTER.does(method.getName().substring(2)), v);
                }
            }
        }
        return this.instance;
    }
    
    @Override
    public T merge(Object object)
    {
        NOT_NULL.verify(object);
        if (object instanceof Map)// TODO test me merge Object <- Map
        {
            Map<String, Object> map = (Map) object;
            for (Entry<String, Object> entry : map.entrySet())
            {
                invoke(CAPITAL_SETTER.does(entry.getKey()), entry.getValue());
            }
        }
        else
        {
            Method[] methods = object.getClass().getMethods();
            for (Method method : methods)
            {
                if (SKIP_NAMES.contains(method.getName()))
                    continue;
                
                if (method.getName().startsWith("get"))
                {
                    Object v = pojoInvoke.invoke(method, object);
                    if (v instanceof Collection)//FIXME implements merge for (+)Collection, (-)Map, (-)Arrays
                    {
                        Collection<Object> collectionOrigin = (Collection<Object>) v;
                        Collection<Object> collectionDest = (Collection<Object>) pojoInvoke.invoke(method.getName(),
                                instance);
                        for (Object o : collectionOrigin)
                        {
                            collectionDest.add(o);
                        }
                    }
                    else
                    {
                        invoke(CAPITAL_SETTER.does(method.getName().substring(3)), v);
                    }
                }
                else if (method.getName().startsWith("is"))
                {
                    Object v = pojoInvoke.invoke(method, object);
                    invoke(CAPITAL_SETTER.does(method.getName().substring(2)), v);
                }
            }
        }
        return this.instance;
    }
    
    @Override
    public boolean hasMethod(String methodName)
    {
        return pojoInvoke.hasMethod(methodName, this.targetClass);
    }

    @Override
    public boolean hasAnnotation(String annotation)
    {
        return hasAnnotation((Class<? extends Annotation>) forName(annotation));
    }

    @Override
    public boolean hasAnnotation(Class<? extends Annotation> annotation)
    {
        if (this.targetClass != null && annotation != null)
            return  this.targetClass.isAnnotationPresent(annotation);
        
        return false;
    }

    @Override
    public List<Method> getMethodsAnnotatedWith(final Class<? extends Annotation> annotation)
    {
        final List<Method> methods = new ArrayList<Method>();
        final List<Method> allMethods = new ArrayList<Method>(Arrays.asList(this.targetClass.getMethods()));
        for (final Method method : allMethods)
        {
            if (method.isAnnotationPresent(annotation))
            {
                methods.add(method);
            }
        }
        return methods;
    }

    @Override
    public <G extends Annotation> G getAnnotationMethod(Class<? extends Annotation> annotation, String methodName, Class<?>... paramTypes)
    {
        Method method = null;
        try
        {
            if (paramTypes.length == 0)
            {
                method = pojoInvoke.getMethodByName(methodName, this.targetClass);
                if (method == null)
                    this.handleException.throwMessage("[NoSuchMethodException] -> Cannot invoke or get the method "+this.targetClass.getName() + "." + methodName + "()");
            }
            else
                method = this.targetClass.getMethod(methodName, paramTypes);
        }
        catch (Exception e) // SecurityException, NoSuchMethodException, NullPointerException 
        {
            this.handleException.handle(e);
        }
        return method == null ? null : (G) method.getAnnotation(annotation);
    }

    @Override
    public <G extends Annotation> G getAnnotationField(Class<? extends Annotation> annotation, String fieldName)
    {
        Field field = null;
        field = new FieldReflect(this.handleException).getField(fieldName, this.targetClass);
        return field == null ? null : (G) field.getAnnotation(annotation);
    }
    
    @Override
    public Field getDeclaredField(String name)
    {
        return new FieldReflect(this.handleException).getField(name, this.targetClass);
    }
    
    @Override
    public Method getDeclaredMethod(String name)
    {
        return pojoInvoke.getMethodByName(name, this.targetClass);
    }
    
    @Override
    public ObjectProxy<T> with(HandleableException handle)
    {
        this.handleException = handle;
        return this;
    }
    
    private Class<?>[] getTypes(Object[] args)
    {
        if (args == null)
            return this.constructorTypes;
        
        Class<?>[] types = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++)
        {
            if (args[i] != null)
                types[i] = args[i].getClass();
        }
        return types;
    }
    
    private boolean isNestedMethod(String methodName)
    {
        return (methodName.indexOf(".") > 0);
    }

    @SuppressWarnings("unchecked")
    private Class<T> forName(String className)
    {
        Class<T> clazz = null;
        try
        {
            clazz = (Class<T>) Class.forName(className);
        }
        catch (ClassNotFoundException e)
        {
            handleException.handle(e, className);
        }
        return clazz;
    }
    
    
    /*
    // FIXME needs supports jdk types like Duration, LocalTime, LocalDateTime, etc
    private boolean isDateType(Class<?> type)// TODO test me Calendar and Gregoria Calendar case
    {
        return (Date.class.getCanonicalName().equals(type.getCanonicalName()));
    }
    
    private boolean isCalendarType(Class<?> type)// TODO test me Calendar and Gregoria Calendar case
    {
        return (Calendar.class.getCanonicalName().equals(type.getCanonicalName())
                || GregorianCalendar.class.getCanonicalName().equals(type.getCanonicalName()));
    }
    */


}
