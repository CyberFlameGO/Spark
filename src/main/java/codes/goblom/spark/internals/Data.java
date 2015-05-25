/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.spark.internals;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author Goblom
 */
public class Data implements Iterable<Entry<String, Object>> {
    
    private final Map<String, Object> data = Maps.newConcurrentMap();
    
    @Override
    public Iterator<Entry<String, Object>> iterator() {
        return data.entrySet().iterator();
    }
    
//    public Set<Map.Entry<String, Object>> entrySet() {
//        return data.entrySet();
//    }
    
    public void clearData() {
        data.clear();
    }
    
    public void set(String key, Object val) {
        data.put(key, val);
    }
    
    public <T> T getOrDefault(String key, T def) {
        if (has(key)) {
            return get(key);
        }
        
        return def;
    }
    
    public <T> T get(String key, Class<T> type) {
        return get(key);
    }
    
    public <T> T get(String key) {
        return (T) data.get(key);
    }
    
    public boolean contains(String key) {
        return has(key);
    }
    
    public boolean has(String key) {
        return data.containsKey(key);
    }

    public int getInt(String key) {
        return get(key);
    }

    public long getLong(String key) {
        return get(key);
    }

    public double getDouble(String key) {
        return get(key);
    }

    public String getString(String key) {
        return get(key);
    }
}
