/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.spark.reflection.safe;

import codes.goblom.spark.Log;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Goblom
 */

public class SafeClass implements Safe<Class<?>> {

    private Class<?> unsafe = null;
    private Map<String, SafeField> fields = Maps.newHashMap();

    public SafeClass(Class<?> clazz) {
        this.unsafe = clazz;
    }
    
    public SafeClass(String clazz) {
        try {
            this.unsafe = Class.forName(clazz);
        } catch (Exception e) { }
        
        if (unsafe == null) {
            Log.getMain().severe("Unable to find class [%s]", clazz);
        }
    }
    
    @Override
    public Class<?> unsafe() {
        return unsafe;
    }

    public Class unsafe2() {
        return unsafe();
    }
    
    @Override
    public String getName() {
        return unsafe().getSimpleName();
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
    @Deprecated
    public void setAccessible(boolean flag) { }
    
    public SafeField getField(String name) {
        if (fields.containsKey(name)) {
            return fields.get(name);
        }
        
        SafeField field = new SafeField(unsafe(), name);
        fields.put(name, field);
        
        return field;
    }
    
    public <T> SafeField<T> getField(String name, Class<T> type) {
        if (fields.containsKey(name)) {
            return fields.get(name);
        }
        
        SafeField<T> field = new SafeField<T>(unsafe(), name);
        fields.put(name, field);
        
        return field;
    }
    
    public List<SafeField> getFields() {
        List<SafeField> list = Lists.newArrayList();
        
        for (Field f : unsafe().getFields()) {
            if (fields.containsKey(f.getName())) {
                list.add(fields.get(f.getName()));
                continue;
            }
            
            SafeField safeField = new SafeField(f);
            fields.put(safeField.getName(), safeField);
            list.add(safeField);
        }
        
        for (Field f : unsafe().getDeclaredFields()) {
            if (fields.containsKey(f.getName())) {
                list.add(fields.get(f.getName()));
                continue;
            }
            
            SafeField safeField = new SafeField(f);
            fields.put(safeField.getName(), safeField);
            list.add(safeField);
        }
        
        return list;
    }
    
    // This does not cache anything. Please use wisely
    public List<SafeMethod> getMethods() {
        List<SafeMethod> list = Lists.newArrayList();
        
        for (Method m : unsafe().getMethods()) {
            list.add(new SafeMethod(m));
        }
        
        for (Method m : unsafe().getDeclaredMethods()) {
            list.add(new SafeMethod(m));
        }
        
        return list;
    }
    
//    public <T> SafeMethod<T> getMethod(String name, SafeClass... params) {
//        return new SafeMethod<T>(unsafe(), name, params);
//    }
    
    public <T> SafeMethod<T> getMethod(String name, Class<?>... params) {
        return new SafeMethod<T>(unsafe(), name, params);
    }
    
    public SafeObject newInstance() {
        try {
            return new SafeObject(unsafe().newInstance());
        } catch (Exception e) { e.printStackTrace(); }
        
        return null;
    }
    
    public SafeConstructor getConstructor(Class<?>... params) {
        return new SafeConstructor(unsafe(), params);
    }
    
    public SafeConstructor getConstructor(SafeClass... params) {
        return new SafeConstructor(unsafe(), params);
    }
    
    // This does not cache anything. Please use wisely
    public List<SafeConstructor> getConstructors() {
        List<SafeConstructor> list = Lists.newArrayList();
        
        for (Constructor c : unsafe().getConstructors()) {
            list.add(new SafeConstructor(c));
        }
        
        for (Constructor c : unsafe().getDeclaredConstructors()) {
            list.add(new SafeConstructor(c));
        }
        
        return list;
    }
    
//    public static void main(String[] args) {
//        SafeClass safeClass = new SafeClass(SafeClass.class);
//        
//        for (SafeField field : safeClass.getFields()) {
//            System.out.println(field.toString());
//        }
//        
//        SafeField<Map> field = safeClass.getField("fields", Map.class);
//                       field.setAccessible(true);
//                       
//        Map map = field.get(safeClass);
//        
//        System.out.println(map.size());
//    }
    
    @Override
    public String toString() {
        return String.format("SafeClass{%s}", unsafe().toString());
    }
}
