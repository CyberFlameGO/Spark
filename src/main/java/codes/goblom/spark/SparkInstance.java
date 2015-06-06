/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.spark;

import codes.goblom.spark.internals.commands.DefaultSparkCommand;
import codes.goblom.spark.internals.misc.SparkPluginsCommand;
import codes.goblom.spark.internals.monitor.Monitors;
import com.google.common.collect.ImmutableList;

/**
 *
 * @author Goblom
 */
public final class SparkInstance extends SparkPlugin {
        
//    @Getter
//    public static SparkInstance instance;
    
    public SparkInstance() {
        super();
        
//        SparkInstance.instance = this;
    }
    
    @Override
    public void load() {
        ImmutableList.of("plugins", "pl").forEach((name) -> { new SparkPluginsCommand(name); });
        ImmutableList.of("spark", "spk", "sk").forEach((name) -> { new DefaultSparkCommand(name); });
        
        Monitors.loadAll();
    }

    @Override
    public void enable() { }
}
