/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.spark;

import codes.goblom.spark.internals.Spark;

/**
 *
 * @author Goblom
 */
public class Log {
    
    private Log() { }
    
    public static void info(String message, Object... vals) {
        if (vals != null && vals.length != 0) {
            Spark.getInstance().getLogger().info(String.format(message, vals));
        } else {
            Spark.getInstance().getLogger().info(message);
        }
    }
    
    public static void warning(String message, Object... vals) {
        if (vals != null && vals.length != 0) {
            Spark.getInstance().getLogger().warning(String.format(message, vals));
        } else {
            Spark.getInstance().getLogger().warning(message);
        }
    }
    
    public static void severe(String message, Object... vals) {
        if (vals != null && vals.length != 0) {
            Spark.getInstance().getLogger().severe(String.format(message, vals));
        } else {
            Spark.getInstance().getLogger().severe(message);
        }
    }
    
    public static void debug(String message, Object... vals) {
        if (Configs.CORE.get("log.debug", false)) {
            info("[Debug] " + message, vals);
        }
    }
}
