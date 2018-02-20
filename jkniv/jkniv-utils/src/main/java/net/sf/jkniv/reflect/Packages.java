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
package net.sf.jkniv.reflect;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.asserts.Assertable;
import net.sf.jkniv.asserts.AssertsFactory;

/**
 * Adapted from https://dzone.com/articles/get-all-classes-within-package
 *
 *@author Alisson Gomes
 */
public final class Packages
{
    private final static Logger     LOG     = LoggerFactory.getLogger(Packages.class);
    private final static Assertable notNull = AssertsFactory.getNotNull();
    private String[]                packagesNames;
    private boolean                 recursive;
    private boolean                 evictInterface;
    private boolean                 evictEnum;
    private boolean                 evictAnnotation;
    private boolean                 evictAnonymousClass;
    private boolean                 evictClass;
    private final Set<Class<?>>     classes;
    private final Set<String>       resourceNames;
    private final Set<String>       suffixResources;
    private boolean                 scanned;
    private final ClassLoader       classLoader;
    
    public Packages(String packageName)
    {
        this(packageName, false, DefaultClassLoader.getClassLoader());
    }
    
    public Packages(String packageName, ClassLoader classLoader)
    {
        this(packageName, false, classLoader);
    }
    
    public Packages(String packageName, boolean recursive)
    {
        this(packageName, recursive, DefaultClassLoader.getClassLoader());
    }
    
    public Packages(String packageName, boolean recursive, ClassLoader classLoader)
    {
        notNull.verify(packageName, classLoader);
        this.packagesNames = packageName.split(",");
        this.recursive = recursive;
        this.evictAnnotation = false;
        this.evictAnonymousClass = false;
        this.evictClass = false;
        this.evictEnum = false;
        this.evictInterface = false;
        this.scanned = false;
        this.classLoader = classLoader;
        this.resourceNames = new HashSet<String>();
        this.classes = new HashSet<Class<?>>();
        this.suffixResources = new HashSet<String>();
        this.suffixResources.add(".class");
    }
    
    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @return The classes
     * @throws IOException failed when read directory resources
     */
    public Iterator<Class<?>> scan() throws IOException
    {
        Set<File> dirs = new HashSet<File>();
        for (String pack : packagesNames)
        {
            Enumeration<URL> resources = classLoader.getResources(pack.replace('.', '/'));
            
            while (resources.hasMoreElements())
            {
                URL resource = resources.nextElement();
                dirs.add(new File(resource.getFile()));
            }
            
            for (File directory : dirs)
            {
                classes.addAll(findClasses(directory, pack));
            }
        }
        this.scanned = true;
        return classes.iterator(); //classes.toArray(new Class[classes.size()]);
    }
    
    /**
     * Scans the resource accessible from the context class loader which belong to the given package and subpackages.
     *
     * @return The classes
     * @throws IOException failed when read directory resources
     */
    public Iterator<String> scanResource() throws IOException
    {
        Set<File> dirs = new HashSet<File>();
        for (String pack : packagesNames)
        {
            Enumeration<URL> resources = classLoader.getResources(pack.replace('.', '/'));
            
            while (resources.hasMoreElements())
            {
                URL resource = resources.nextElement();
                dirs.add(new File(resource.getFile()));
            }
            
            for (File directory : dirs)
            {
                resourceNames.addAll(findResource(directory, pack));
            }
        }
        this.scanned = true;
        return resourceNames.iterator(); //classes.toArray(new Class[classes.size()]);
    }
    
    public Class<?>[] asArray()
    {
        if (!scanned)
            throw new ReflectionException("Package [" + packagesNames + "] doesn't scanned, call scan first");
        
        return classes.toArray(new Class[classes.size()]);
    }
    
    public String[] asResources()
    {
        if (!scanned)
            throw new ReflectionException("Package [" + packagesNames + "] doesn't scanned, call scan first");
        
        return resourceNames.toArray(new String[resourceNames.size()]);
    }
    
    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     */
    private Set<Class<?>> findClasses(File directory, String packageName)
    {
        Set<Class<?>> classes = new HashSet<Class<?>>();
        if (!directory.exists())
            return classes;
        
        File[] files = directory.listFiles();
        
        for (File file : files)
        {
            if (file.isDirectory() && recursive)
            {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            }
            else if (file.getName().endsWith(".class"))
            {
                String s = null;
                try
                {
                    s = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                    Class<?> c = Class.forName(s);
                    if (!evict(c))
                    {
                        classes.add(c);
                        resourceNames.add(s);
                    }
                }
                catch (ClassNotFoundException e)
                {
                    resourceNames.add(s);
                    LOG.warn("Cannot get [" + s + "] associated with the class or interface ");
                }
            }
        }
        return classes;
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     */
    private Set<String> findResource(File directory, String packageName)
    {
        Set<String> classes = new HashSet<String>();
        if (!directory.exists())
            return classes;
        
        File[] files = directory.listFiles();
        
        for (File file : files)
        {
            if (file.isDirectory() && recursive)
            {
                assert !file.getName().contains(".");
                classes.addAll(findResource(file, packageName + "." + file.getName()));
            }
            else if (hasSuffix(file.getName()))
            {
                String s = null;
                s = packageName + '.' + file.getName();
                classes.add(s);
            }
        }
        return classes;
    }

    public Packages evictInterfaces()
    {
        this.evictInterface = true;
        return this;
    }
    
    public Packages evictEnums()
    {
        this.evictEnum = true;
        return this;
    }
    
    public Packages evictAnnotations()
    {
        this.evictAnnotation = true;
        return this;
    }
    
    public Packages evictAnonymousClasses()
    {
        this.evictAnonymousClass = true;
        return this;
    }
    
    public Packages evictClasses()
    {
        this.evictClass = true;
        return this;
    }
    
    public void onlyInterfaces()
    {
        evictAnnotations().evictAnonymousClasses().evictEnums().evictClasses();
    }
    
    public void onlyEnums()
    {
        evictAnnotations().evictAnonymousClasses().evictClasses().evictInterfaces();
    }
    
    public void onlyAnnotations()
    {
        evictClasses().evictAnonymousClasses().evictEnums().evictInterfaces();
    }
    
    public void onlyAnonymousClasses()
    {
        evictAnnotations().evictClasses().evictEnums().evictInterfaces();
    }
    
    public void onlyClasses()
    {
        evictAnnotations().evictAnonymousClasses().evictEnums().evictInterfaces();
    }

    public void onlyResource(String suffix)
    {
        this.suffixResources.clear();
        this.suffixResources.add(suffix);
    }
    
    private boolean hasSuffix(String resourceName)
    {
        int index = resourceName.lastIndexOf(".");
        String suffix = resourceName.substring(index, resourceName.length());
        return this.suffixResources.contains(suffix);
    }
    private boolean evict(Class<?> c)
    {
        boolean answer = false;
        if (c.isInterface() && evictInterface)
            answer = true;
        else if (c.isEnum() && evictEnum)
            answer = true;
        else if (c.isAnnotation() && evictAnnotation)
            answer = true;
        else if (c.isAnonymousClass() && evictAnonymousClass)
            answer = true;
        else if (evictClass && !c.isInterface() && !c.isEnum() && !c.isAnnotation() && !c.isAnonymousClass())
            answer = true;
        
        return answer;
    }
    
}
