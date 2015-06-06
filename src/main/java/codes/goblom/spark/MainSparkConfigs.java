/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.spark;

import codes.goblom.spark.configuration.types.YamlConfig;
import codes.goblom.spark.internals.Spark;

/**
 *
 * @author Goblom
 */
public final class MainSparkConfigs {
    
    private MainSparkConfigs() { }
    
    public static final YamlConfig DEFAULT = (YamlConfig) Spark.getMainInstance().getConfig("config");
    
    public static final YamlConfig CORE = (YamlConfig) Spark.getMainInstance().getConfig("core");
}
