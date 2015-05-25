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
public interface Callback<T> {
    
    void onFinish(T object, Throwable error);
}
