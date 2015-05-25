/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.spark.configuration;

import codes.goblom.spark.configuration.types.JsonConfig;
import codes.goblom.spark.configuration.types.YamlConfig;
import java.io.File;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Goblom
 */
public enum ConfigType {
    
    YAML {
        @Override
        public YamlConfig load(Plugin plugin, String file) {
            return new YamlConfig(plugin, file);
        }
        
        @Override
        public YamlConfig load(Plugin plugin, File external, String f) {
            return new YamlConfig(plugin, external, f);
        }
    },
    
    JSON {
        @Override
        public JsonConfig load(Plugin plugin, String file) {
//            return YAML.load(plugin, file);
            return new JsonConfig(plugin, file);
        }

        @Override
        public JsonConfig load(Plugin plugin, File external, String f) {
//            return YAML.load(plugin, external, f);
            return new JsonConfig(plugin, external, f);
        }
        
    };
    
    public abstract Config load(Plugin plugin, String file);
    
    public abstract Config load(Plugin plugin, File external, String f);
}
