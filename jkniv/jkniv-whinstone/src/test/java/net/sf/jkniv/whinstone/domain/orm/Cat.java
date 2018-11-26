package net.sf.jkniv.whinstone.domain.orm;

import net.sf.jkniv.whinstone.PostCallBack;
import net.sf.jkniv.whinstone.PreCallBack;
import net.sf.jkniv.whinstone.CallbackScope;

public class Cat extends Animal
{
    
    @Override
    public String sound()
    {
        return "miau miau";
    }
    
    @PreCallBack(scope = CallbackScope.ADD)
    public void preAdd()
    {
        System.out.println("Cat pre-add");
    }
    
    @PostCallBack(scope = CallbackScope.ADD)
    public void postAdd()
    {
        System.out.println("Cat post-add");
    }
    
    @PreCallBack(scope = CallbackScope.REMOVE)
    public void preRemove()
    {
        System.out.println("Cat pre-remove");
    }
    
    @PostCallBack(scope = CallbackScope.REMOVE)
    public void postRemove()
    {
        System.out.println("Cat post-remove");
    }
    
    @PreCallBack(scope = CallbackScope.SELECT)
    public void preSelect()
    {
        System.out.println("Cat pre-select");
    }
    
    @PostCallBack(scope = CallbackScope.SELECT)
    public void postSelect()
    {
        System.out.println("Cat post-select");
    }
    
    @PreCallBack(scope = CallbackScope.UPDATE)
    public void preUpdate()
    {
        System.out.println("Cat pre-update");
    }
    
    @PostCallBack(scope = CallbackScope.UPDATE)
    public void postUpdate()
    {
        System.out.println("Cat post-update");
    }
    
    @PostCallBack(scope =
    { CallbackScope.SELECT, CallbackScope.ADD, CallbackScope.UPDATE, CallbackScope.REMOVE, CallbackScope.EXCEPTION })
    public void postException()
    {
        System.out.println("Cat post-exception");
    }
    
    @PostCallBack(scope =
    { CallbackScope.ADD, CallbackScope.UPDATE, CallbackScope.REMOVE, CallbackScope.COMMIT })
    public void postCommit()
    {
        System.out.println("Cat post-commit");
    }
    
}
