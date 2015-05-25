/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.spark.reflection.safe;

import codes.goblom.spark.Log;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Arrays;

/**
 *
 * @author Goblom
 */
public class SafeConstructor implements Safe<Constructor<?>> {

    private Constructor<?> unsafe;
    int params = Integer.MIN_VALUE;
    
    public SafeConstructor(Constructor con) {
        this.unsafe = con;
    }
    
    public SafeConstructor(Class<?> clazz, SafeClass... safeParams) {
        Constructor con = null;
        Class[] params = new Class[safeParams.length];
        
        for (int i = 0; i < safeParams.length; i++) {
            params[i] = safeParams[i].unsafe();
        }
        
        try {
            con = clazz.getDeclaredConstructor(params);
        } catch (Throwable t) { }
        
        if (con == null) {
            try {
                con = clazz.getConstructor(params);
            } catch (Throwable t) { }
        }
        
        if (con == null) {
//            throw new RuntimeException(String.format("Cound not find Constructor for class %s with params %s", clazz.getSimpleName(), Arrays.asList(params).toString()));
            Log.severe("Cound not find Constructor for class %s with safeparams %s", clazz.getSimpleName(), Arrays.asList(params).toString());
        }
        
        this.params = params.length;
        this.unsafe = con;
    }
    
    public SafeConstructor(Class<?> clazz, Class<?>... params) {
        Constructor con = null;
        
        try {
            con = clazz.getDeclaredConstructor(params);
        } catch (Throwable t) { }
        
        if (con == null) {
            try {
                con = clazz.getConstructor(params);
            } catch (Throwable t) { }
        }
        
        if (con == null) {
//            throw new RuntimeException(String.format("Cound not find Constructor for class %s with params %s", clazz.getSimpleName(), Arrays.asList(params).toString()));
            Log.severe("Cound not find Constructor for class %s with params %s", clazz.getSimpleName(), Arrays.asList(params).toString());
        }
        
        this.params = params.length;
        this.unsafe = con;
    }
    
    public SafeObject newInstance(Object... initargs) {
        if (params != Integer.MIN_VALUE && initargs.length != params) {
            Log.warning("Entered params for Constructor on class % do not match length of %s", unsafe().getClass().getSimpleName(), params);
        }
        
        try {
            return new SafeObject(unsafe().newInstance(initargs));
        } catch (Throwable t) {
            t.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public Constructor<?> unsafe() {
        return unsafe;
    }

    public Constructor unsafe2() {
        return unsafe();
    }
    
    @Override
    public String getName() {
        return unsafe().getName();
    }

    @Override
    public boolean isPublic() {
        return Modifier.isPublic(unsafe().getModifiers());
    }

    @Override
    @Deprecated
    public boolean isReadOnly() {
        return Modifier.isFinal(unsafe().getModifiers());
    }

    @Override
    @Deprecated
    public boolean isStatic() {
        return Modifier.isStatic(unsafe().getModifiers());
    }

    @Override
    public void setAccessible(boolean flag) {
        unsafe().setAccessible(flag);
    }
    
    @Override
    public String toString() {
        return String.format("SafeConstructor{%s}", unsafe().toString());
    }
}
