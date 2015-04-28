/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.core.configuration;

import java.io.File;

/**
 *
 * @author Goblom
 */
public interface Config {
    
    ConfigType getType();
    
    File getFile();
    
    boolean contains(String path);
    
    void set(String path, Object value, boolean save);
    
    <T> T get(String path);
    
    <T> T get(String path, T def);
    
    void save();
    
    void reload();
    
    default boolean has(String path) {
        return contains(path);
    }
    
    default void set(String path, Object value) {
        set(path, value, false);
    }
}
