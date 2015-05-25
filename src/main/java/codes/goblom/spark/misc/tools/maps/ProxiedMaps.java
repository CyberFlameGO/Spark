/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.spark.misc.tools.maps;

import codes.goblom.spark.Log;
import codes.goblom.spark.misc.tools.maps.ExpiringMap.Builder;
import codes.goblom.spark.misc.tools.maps.ExpiringMap.ExpirationListener;
import java.lang.reflect.Method;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;
import lombok.Getter;

/**
 * This is used to determine when a Map is written to
 * @author Goblom
 */
public class ProxiedMaps {
    
    private ProxiedMaps() { }
    
    public static <K, V> HashMap<K, V> newHashMap() {      
        ProxyFactory factory = new ProxyFactory();
                     factory.setSuperclass(HashMap.class);
                     factory.setFilter(ProxiedHandler.getInstance());
                    
        Class<?> c = factory.createClass();
        HashMap map = newInstance(c);
        
        if (map == null) return null;
        
        ((Proxy) map).setHandler(ProxiedHandler.getInstance());
        
        return map;
    }
    
    public static <K, V> ConcurrentMap<K, V> newConcurrentMap() {
        ProxyFactory factory = new ProxyFactory();
                     factory.setSuperclass(ConcurrentHashMap.class);
                     factory.setFilter(ProxiedHandler.getInstance());
                    
        Class<?> c = factory.createClass();
        ConcurrentHashMap map = newInstance(c);
                
        ((Proxy) map).setHandler(ProxiedHandler.getInstance());
        
        return map;
    }
    
    public static <K extends Enum<K>, V> EnumMap<K, V> newEnumMap(Class<K> type) {
        ProxyFactory factory = new ProxyFactory();
                     factory.setSuperclass(EnumMap.class);
                     factory.setFilter(ProxiedHandler.getInstance());
                     
        Class<?> c = factory.createClass();
        try {
            EnumMap map = (EnumMap) c.getConstructor(Class.class).newInstance(type);
            if (map == null) return null;
            ((Proxy) map).setHandler(ProxiedHandler.getInstance());
            
            return map;
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
        
        return null;
    }
    
    public static <K, V> ExpiringMap<K, V> newExpiringMap(long durration, TimeUnit unit) {
        ProxyFactory factory = new ProxyFactory();
                     factory.setSuperclass(ExpiringMap.class);
                     factory.setFilter(ProxiedHandler.getInstance());
                     
        Class<?> c = factory.createClass();
        
        try {
            Builder builder = ExpiringMap.builder();
                    builder.expiration(durration, unit);
                    
            ExpiringMap map = (ExpiringMap) c.getConstructor(Builder.class).newInstance(builder);
            ((Proxy) map).setHandler(ProxiedHandler.getInstance());
            return map;
        } catch (Exception e) { e.printStackTrace(); }
        
        return null;
    }
    
    public static <K, V> ExpiringMap<K, V> newExpiringMap(long durration, TimeUnit unit, ExpirationListener<K, V> listener) {
        ProxyFactory factory = new ProxyFactory();
                     factory.setSuperclass(ExpiringMap.class);
                     factory.setFilter(ProxiedHandler.getInstance());
                     
        Class<?> c = factory.createClass();
        
        try {
            Builder builder = ExpiringMap.builder();
                    builder.expiration(durration, unit);
                    builder.expirationListener(listener);
                    
            ExpiringMap map = (ExpiringMap) c.getConstructor(Builder.class).newInstance(builder);
            ((Proxy) map).setHandler(ProxiedHandler.getInstance());
            return map;
        } catch (Exception e) { e.printStackTrace(); }
        
        return null;
    }
    
    // *****************************
    // Internals
    // *****************************
    private static <T> T newInstance(Class<?> clazz) {
        try {
            return (T) clazz.newInstance();
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }
    
    static class ProxiedHandler implements MethodFilter, MethodHandler {

        @Getter
        static ProxiedHandler instance = new ProxiedHandler();
        
        @Override
        public boolean isHandled(Method m) {
            return m.getName().equals("put");
//            return true;
        }

        @Override
        public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
            Log.debug("[ProxiedMap - {%s}] Called: %s", self.getClass().getSimpleName(), thisMethod.getName());
            
            for (int i = 0; i < args.length; i++) {
                Log.debug("[ProxiedMap - {%s}] Args %s: %s", i, args[i]);
            }
            
            Log.debug("[ProxiedMap - {%s}] This Map now has %s entries", self.getClass().getSimpleName(), self.getClass().getMethod("size").invoke(self));
            
            return proceed.invoke(self, args);
        }
    }
}

