/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.spark.internals;

/**
 *
 * @author Goblom
 */
public interface Validater<T> {
    
    //TODO: ??? Maybe have it throw a Throwable
    boolean validate(T obj);
}
