/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.core.reflection;

import codes.goblom.core.GoPlugin;
import codes.goblom.core.reflection.safe.SafeClass;
import com.google.common.collect.Maps;
import java.util.Map;
import lombok.Getter;
import org.bukkit.Bukkit;

/**
 *
 * @author Goblom
 */
public class Reflection {
    
    public static final Reflection instance() {
        return GoPlugin.getInstance().getReflection();
    }
    
    @Getter
    private final String version;
    
    private final Map<String, Map<String, SafeClass>> storage = Maps.newHashMap();
    
    public Reflection() {
        this(Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3]);
    }
    
    public Reflection(String version) {
        this.version = version;
        
        this.storage.put("nms", Maps.newHashMap());
        this.storage.put("craft", Maps.newHashMap());
        this.storage.put("other", Maps.newHashMap());
    }
    
    public SafeClass getNMSClass(String clazz) {
        Map<String, SafeClass> classes = storage.get("nms");
        String classFor = String.format("net.minecraft.server.%s.%s", version, clazz);
        
        if (classes.containsKey(classFor)) {
            return classes.get(classFor);
        }
        
        SafeClass safeClass = new SafeClass(classFor);
        
        if (safeClass.isOk()) {
            classes.put(classFor, safeClass);
        }
        
        return safeClass;
    }
    
    public SafeClass getCraftClass(String clazz) {
        Map<String, SafeClass> classes = storage.get("craft");
        String classFor = String.format("org.bukkit.craftbukkit.%s.%s", version, clazz);
        
        if (classes.containsKey(classFor)) {
            return classes.get(classFor);
        }
        
        SafeClass safeClass = new SafeClass(classFor);
        
        if (safeClass.isOk()) {
            classes.put(classFor, safeClass);
        }
        
        return safeClass;
    }
    
    public SafeClass getClass(String clazz) {
        Map<String, SafeClass> classes = storage.get("other");
        
        if (classes.containsKey(clazz)) {
            return classes.get(clazz);
        }
        
        SafeClass safeClass = new SafeClass(clazz);
        
        if (safeClass.isOk()) {
            classes.put(clazz, safeClass);
        }
        
        return safeClass;
    }
    
    @Override
    public String toString() {
        return this.version;
    }
}
