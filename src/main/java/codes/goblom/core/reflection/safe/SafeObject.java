/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.core.reflection.safe;

/**
 *
 * @author Goblom
 */
public class SafeObject implements Safe<Object> {
    
    private final Object unsafe;

    private SafeClass clazz;
    
    public SafeObject(Object o) {
        this.unsafe = o;
    }
    
    @Override
    public Object unsafe() {
        return unsafe;
    }
    
    public SafeClass getSafeClass() {
        if (this.clazz == null) {
            this.clazz = new SafeClass(unsafe().getClass());
        }
        
        return this.clazz;
    }
    
    public <T> T as(Class<T> type) {
        return (T) unsafe();
    }
    
    @Override
    @Deprecated
    public String getName() {
        return unsafe().toString();
    }

    @Override
    @Deprecated
    public boolean isPublic() {
        return true;
    }

    @Override
    @Deprecated
    public boolean isReadOnly() {
        return true;
    }

    @Override
    @Deprecated
    public boolean isStatic() {
        return false;
    }

    @Override
    @Deprecated
    public void setAccessible(boolean flag) { }
    
    @Override
    public String toString() {
        return String.format("SafeObject{%s}", unsafe().toString());
    }
}
