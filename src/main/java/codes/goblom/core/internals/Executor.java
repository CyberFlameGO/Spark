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
public interface Executor<T, E extends Throwable> {
    
    T execute(ExecutorArgs args) throws E;
}
