/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.core;

/**
 *
 * @author Goblom
 */
public class Log {
    
    public static void info(String message, Object... vals) {
        if (vals != null && vals.length != 0) {
            GoPlugin.getInstance().getLogger().info(String.format(message, vals));
        } else {
            GoPlugin.getInstance().getLogger().info(message);
        }
    }
    
    public static void warning(String message, Object... vals) {
        if (vals != null && vals.length != 0) {
            GoPlugin.getInstance().getLogger().warning(String.format(message, vals));
        } else {
            GoPlugin.getInstance().getLogger().warning(message);
        }
    }
    
    public static void severe(String message, Object... vals) {
        if (vals != null && vals.length != 0) {
            GoPlugin.getInstance().getLogger().severe(String.format(message, vals));
        } else {
            GoPlugin.getInstance().getLogger().severe(message);
        }
    }
    
    public static void debug(String message, Object... vals) {
        if (Configs.CORE.get("log.debug", false)) {
            info("[Debug] " + message, vals);
        }
    }
}
