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
public interface Executor<T, A, E extends Throwable> {
    
    T execute(A[] args) throws E;
}
