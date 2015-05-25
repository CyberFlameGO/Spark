/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.spark.internals.policy;

import codes.goblom.spark.internals.Validater;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author Goblom
 */
public final class NonDefinedPolicy<T> extends Policy<T> implements Iterable<Entry<String, Object>> {

    public Validater<NonDefinedPolicy> validater = null;
    private final Map<String, Object> values = Maps.newConcurrentMap();
    
    protected NonDefinedPolicy(Class<T> forClass) {
        super(forClass);
    }

    public NonDefinedPolicy<T> value(String key, Object... objects) {
        if (objects != null) {
            if (objects.length == 1) {
                values.put(key, objects[0]);
            } else {
                values.put(key, objects);
            }
        }
        
        return this;
    }
    
    public Object getNoCast(String key) {
        return values.get(key);
    }
    
    public <O> O get(String key) {
        return (O) values.get(key);
    }
    
    public <O> O[] getArray(String key) {
        return (O[]) values.get(key);
    }
    
    @Override
    public final boolean isValid() {
        return validater != null ? validater.validate(this) : true;
    }

    @Override
    public Iterator<Entry<String, Object>> iterator() {
        return values.entrySet().iterator();
    }
}
