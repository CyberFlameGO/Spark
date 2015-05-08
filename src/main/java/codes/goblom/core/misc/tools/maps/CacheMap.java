/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.core.misc.tools.maps;

import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Getter;

/**
 * 
 * @deprecated lazy
 * @author Goblom
 */
@Deprecated
public class CacheMap<K, V> implements Map<K, V> {
    
    private final long timeToLive;
    
    @Getter
    private final Map<K, CacheObject> map;
    
    @Getter
    private Thread thread;
    
    public CacheMap(long live, long interval) {
        this.timeToLive = live * 1000;
        this.map = Maps.<K, CacheObject>newConcurrentMap();
        
        if (timeToLive > 0 && interval > 0) {
            this.thread = new Thread() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(interval * 1000);
                        } catch (Throwable t) { }
                        
                        cleanup();
                    }
                }
            };
            
            this.thread.setDaemon(true);
            this.thread.start();
        }
    }
    
    public void cleanup() {
        long now = System.currentTimeMillis();
        List<K> deleteKey = null;
        
        synchronized (map) {
            Iterator<Map.Entry<K, CacheObject>> it = map.entrySet().iterator();
            deleteKey = new ArrayList((map.size() / 2) + 1);
            
            K key = null;
            CacheObject cache = null;
            
            while (it.hasNext()) {
                Map.Entry<K, CacheObject> entry = it.next();
                
                key = entry.getKey();
                cache = entry.getValue();
                
                if (cache != null && (now > timeToLive + cache.lastAccessed)) {
                    deleteKey.add(key);
                }
            }
        }
        
        for (K key : deleteKey) {
            synchronized (map) {
                map.remove(key);
            }
        }
        
        Thread.yield();
    }

    @Override
    public int size() {
        synchronized (map) { 
            return map.size();
        }
    }

    @Override
    public boolean isEmpty() {
        synchronized (map) { 
            return map.isEmpty();
        }
    }

    @Override
    public boolean containsKey(Object key) {
        synchronized (map) { 
            return map.containsKey(key);
        }
    }

    @Override
    public boolean containsValue(Object value) {
        synchronized (map) { 
            return map.containsValue(value);
        }
    }

    @Override
    public V get(Object key) {
        synchronized (map) { 
            CacheObject cache = map.get(key);
            
            if (cache == null) {
                return null;
            } else {
                cache.lastAccessed = System.currentTimeMillis();
                return cache.value;
            }
        }
    }

    @Override
    public V put(K key, V value) {
        synchronized (map) { 
            return map.put(key, new CacheObject(value)).value;
        }
    }

    @Override
    public V remove(Object key) {
        synchronized (map) { 
            return map.remove(key).value;
        }
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        synchronized (map) {
            try {
                m.entrySet().stream().forEach((entry) -> {
                    K key = (K) entry.getKey();
                    V value = (V) entry.getValue();
                    
                    map.put(key, new CacheObject(value));
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void clear() {
        synchronized (map) { 
            map.clear();
        }
    }

    @Override
    public Set<K> keySet() {
        synchronized (map) { 
            return map.keySet();
        }
    }

    @Override
    public Collection<V> values() {
        synchronized (map) {
            return Collections2.transform(map.values(), (CacheObject input) -> {
                input.lastAccessed = System.currentTimeMillis();
                return input.value;
            });
        }
    }

    @Override
    @Deprecated //Intense resource use
    public Set<Map.Entry<K, V>> entrySet() {
        synchronized (map) {
            Set<Map.Entry<K, V>> set = Sets.newConcurrentHashSet();
            Iterator<Map.Entry<K, CacheObject>> it = map.entrySet().iterator();
            
            while (it.hasNext()) {
                Map.Entry<K, CacheObject> entry = it.next();
                
                K key = entry.getKey();
                CacheObject cache = entry.getValue();
                            cache.lastAccessed = System.currentTimeMillis();
                            
                AbstractMap.SimpleEntry<K, V> simpleEntry = new AbstractMap.SimpleEntry(key, cache.value);
                
                set.add(simpleEntry);
            }
            
            return set;
        }
    }
    
    protected class CacheObject {
        public long lastAccessed = System.currentTimeMillis();
        public V value;
        
        protected CacheObject(V value) {
            this.value = value;
        }
    }
}
