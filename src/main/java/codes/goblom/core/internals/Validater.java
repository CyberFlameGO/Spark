/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.core.internals;

/**
 *
 * @author Goblom
 */
public interface Validater<T> {
    
    boolean validate(T obj);
}
