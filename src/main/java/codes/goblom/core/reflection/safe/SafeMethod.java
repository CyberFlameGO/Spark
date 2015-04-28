/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.core.reflection.safe;

import codes.goblom.core.Log;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

/**
 *
 * @author Goblom
 */

//TODO: Support SafeClass Constructor params
public class SafeMethod<T> implements Safe<Method> {

    private Method unsafe;
    
    public SafeMethod(Method method) {
        this.unsafe = method;
    }
    
    public SafeMethod(Class<?> coreClass, String methodName, Class<?>[] params) {
        Method method = getMethod(coreClass, methodName, params);
        Class superClass = coreClass;
        
        while (method == null && superClass != null) {
            superClass = coreClass.getSuperclass();
            method = getMethod(coreClass, methodName, params);
        }
        
        if (method == null) {
            String types = "[none]";
            if (params != null && params.length != 0) {
                types = Arrays.asList(params).toString();
            }
            
            Log.severe("Class [%s] does does not have a superclass that has method [%s] with types %s", coreClass.getSimpleName(), methodName, types);
        }
        
        this.unsafe = method;
    }
    
    private Method getMethod(Class<?> clazz, String name, Class<?>[] params) {
        try {
            if (params != null && params.length != 0) {
                return clazz.getDeclaredMethod(name, params);
            } else {
                return clazz.getDeclaredMethod(name);
            }
        } catch (Exception e) { }
        
        try {
            if (params != null && params.length != 0) {
                return clazz.getMethod(name, params);
            } else {
                return clazz.getMethod(name);
            }
        } catch (Exception e) { }
        
        return null;
    }
    
    @Override
    public Method unsafe() {
        return this.unsafe;
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
    public boolean isReadOnly() {
        return Modifier.isFinal(unsafe().getModifiers());
    }

    @Override
    public boolean isStatic() {
        return Modifier.isStatic(unsafe().getModifiers());
    }

    @Override
    public void setAccessible(boolean flag) {
        unsafe().setAccessible(flag);
    }
    
    public SafeObject invoke(Object instance, Object... args) {
        if (!isStatic() && instance == null) {
            throw new UnsupportedOperationException("Non-static fields require a non-null instance passed in!");
        }
        
        try {
            return new SafeObject(unsafe().invoke(instance, args));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
}
