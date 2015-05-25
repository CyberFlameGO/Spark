/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.spark;

import codes.goblom.spark.configuration.ConfigType;
import codes.goblom.spark.configuration.types.JsonConfig;
import codes.goblom.spark.configuration.types.YamlConfig;
import codes.goblom.spark.internals.Spark;

/**
 *
 * @author Goblom
 */
public class Configs {
    
    private Configs() { }
    
    public static final YamlConfig DEFAULT = (YamlConfig) Spark.getInstance().getConfig("config");
    
    public static final YamlConfig CORE = (YamlConfig) Spark.getInstance().getConfig("core");
    
    public static final JsonConfig TEST_JSON = (JsonConfig) Spark.getInstance().getConfig(ConfigType.JSON, "test_json");
}
