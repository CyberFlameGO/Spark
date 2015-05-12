/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.core.internals;

import com.google.common.collect.Maps;
import java.util.Map;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author Goblom
 */
@RequiredArgsConstructor( access = AccessLevel.PROTECTED )
public class ExecutorArgs {

    private final Object[] objects;

    public Object get() {
        return get(0);
    }

    public Object get(int i) {
        return objects[i];
    }

    public <T> T getAs(Class<T> clazz) {
        return getAs();
    }
    
    public <T> T getAs(int i, Class<T> clazz) {
        return getAs(i);
    }
    
    public <T> T getAs() {
        return (T) get();
    }

    public <T> T getAs(int i) {
        return (T) get(i);
    }
    
    public static Builder Builder() {
        return new Builder();
    }
    
    public static class Builder {
        private final Map<Integer, Object> map = Maps.newConcurrentMap();
        private int slot = 0;
        
        private Builder() { }
        
        public Builder put(Object o) {
            map.put(slot++, o);
            
            return this;
        }
        
        public ExecutorArgs build() {
            if (slot == 0) {
                return new ExecutorArgs(new Object[] { });
            }
            
            Object[] args = new Object[slot - 1];
            
            map.entrySet().forEach((entry) -> {
                args[entry.getKey()] = entry.getValue();
            });
            
            return new ExecutorArgs(args);
        }
    }
}
