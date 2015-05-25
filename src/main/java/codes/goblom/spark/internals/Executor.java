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
public interface Executor<T, E extends Throwable> {
    
    public static final ExecutorArgs EMPTY_ARGS = ExecutorArgs.Builder().build();
    
    T execute(ExecutorArgs args) throws E;
}
