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
public interface Safe<T> {
    
    T unsafe();
    
    String getName();
    
//    R get(Object instance);
//    
//    boolean set(Object instance, R value);
//    
//    T transfer(Object from, Object... to);
    
    boolean isPublic();
    
    boolean isReadOnly();
    
//    void setReadOnly(boolean read);
    
    default boolean isOk() {
        return unsafe() != null;
    }
    
    boolean isStatic();
    
    public void setAccessible(boolean flag);
    
    @Override
    String toString();
}
