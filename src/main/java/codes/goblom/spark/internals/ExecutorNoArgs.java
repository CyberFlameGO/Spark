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
public interface ExecutorNoArgs<T, E extends Throwable> extends Executor<T, E> {

    @Override
    default T execute(ExecutorArgs args) throws E {
        return execute();
    }
    
    T execute() throws E;
}
