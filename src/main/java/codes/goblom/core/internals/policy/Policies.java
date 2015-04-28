/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.core.internals.policy;

/**
 *
 * @author Goblom
 */
public class Policies {
    
    public static LoadPolicy newLoadPolicy() {
        return new LoadPolicy(null);
    }
    
    public static <T> LoadPolicy<T> newLoadPolicy(Class<T> clazz) {
        return new LoadPolicy<T>(clazz);
    }
    
    public static <T> NonDefinedPolicy<T> newNonDefinedPolicy(Class<T> clazz) {
        return new NonDefinedPolicy<T>(clazz);
    }
}
