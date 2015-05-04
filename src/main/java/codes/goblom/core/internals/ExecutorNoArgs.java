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
public interface ExecutorNoArgs<T, E extends Throwable> extends Executor<T, Object, E> {
    
    @Override
    default T execute(Object[] args) throws E {
        return execute();
    }
    
    T execute() throws E;
}
