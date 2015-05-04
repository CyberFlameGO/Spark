/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.core;

import codes.goblom.core.configuration.ConfigType;
import codes.goblom.core.configuration.types.JsonConfig;
import codes.goblom.core.configuration.types.YamlConfig;

/**
 *
 * @author Goblom
 */
public class Configs {
    
    private Configs() { }
    
    public static final YamlConfig DEFAULT = (YamlConfig) GoPlugin.getInstance().getConfig("config");
    
    public static final YamlConfig CORE = (YamlConfig) GoPlugin.getInstance().getConfig("core");
    
    public static final JsonConfig TEST_JSON = (JsonConfig) GoPlugin.getInstance().getConfig(ConfigType.JSON, "test_json");
}
