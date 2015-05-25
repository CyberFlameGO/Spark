/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.spark.internals.policy;

/**
 *
 * @author Goblom
 */
public final class LoadPolicy<T> extends Policy<T> {

    public Class[] constructor;
    public Object[] values;

    protected LoadPolicy(Class<T> forClass) {
        super(forClass);
    }
    
    public LoadPolicy constructor(Class... constructor) {
        this.constructor = constructor;
        
        return this;
    }
    
    public LoadPolicy values(Object... values) {
        this.values = values;
        
        return this;
    }
    
    @Override
    public boolean isValid() {
        if (constructor != null && values != null) {
            return constructor.length == values.length;
        }
        
        return false;
    }
}
