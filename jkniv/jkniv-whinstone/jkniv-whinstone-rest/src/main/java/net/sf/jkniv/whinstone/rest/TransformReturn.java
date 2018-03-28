package net.sf.jkniv.whinstone.rest;

import java.util.List;

public interface TransformReturn<T>
{
    T transform(Object o);
    
    List<T> transform(List<?> list);
}
