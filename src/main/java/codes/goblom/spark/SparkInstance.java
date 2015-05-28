/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.spark;

import codes.goblom.spark.internals.commands.DefaultSparkCommand;
import codes.goblom.spark.internals.misc.SparkPluginsCommand;
import com.google.common.collect.ImmutableList;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Goblom
 */
public class SparkInstance extends JavaPlugin {
    
    @Override
    public void onLoad() {
        ImmutableList.of("plugins", "pl").forEach((name) -> { new SparkPluginsCommand(name); });
        ImmutableList.of("spark", "spk", "sk").forEach((name) -> { new DefaultSparkCommand(name); });
    }
}
