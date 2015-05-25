/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.spark.configuration.types;

import codes.goblom.spark.configuration.Config;
import codes.goblom.spark.configuration.ConfigType;
import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Goblom
 */
public class YamlConfig implements Config {
    
    private final File file;
    private FileConfiguration config;
    
    public YamlConfig(Plugin plugin, String file) {
        this(plugin, null, file);
    }
    
    public YamlConfig(Plugin plugin, File external, String f) {
        f = f.endsWith(".yml") ? f : f + ".yml";
        
        if (f.equals("config.yml")) {
            plugin.saveDefaultConfig();
        }
        
        this.file = new File(external == null ? plugin.getDataFolder() : external, f);
        
        if (!this.file.exists()) {
            try {
                plugin.saveResource(f, false); //TODO: Support inner-most files. not just top level
            } catch (Exception e) { 
                e.printStackTrace(); 
            }
        }
        
        if (!this.file.exists()) {
            try {
                this.file.createNewFile();
            } catch (Exception e) { 
                e.printStackTrace();
            }
        }
        
        this.config = YamlConfiguration.loadConfiguration(file);
    }
    
    public FileConfiguration getConfiguration() {
        return this.config;
    }
    
    @Override
    public File getFile() {
        return this.file;
    }
    
    @Override
    public ConfigType getType() {
        return ConfigType.YAML;
    }

    @Override
    public boolean contains(String path) {
         return this.config.contains(path);
    }

    @Override
    public void set(String path, Object value, boolean save) {
        this.config.set(path, value);
        
        if (save) {
            save();
        }
    }

    @Override
    public <T> T get(String path) {
        return (T) this.config.get(path);
    }

    @Override
    public <T> T get(String path, T def) {
        if (!contains(path)) {
            set(path, def, true);
            
            return def;
        }
        
        return (T) this.config.get(path);
    }

    @Override
    public void save() {
        try {
            this.config.save(getFile());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reload() {
        this.config = YamlConfiguration.loadConfiguration(file);
    }
}
