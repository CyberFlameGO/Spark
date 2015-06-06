/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.spark;

import codes.goblom.spark.internals.Spark;
import codes.goblom.spark.internals.tools.Placeholders;
import com.google.common.collect.Maps;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Goblom
 */
@RequiredArgsConstructor( access = AccessLevel.PRIVATE )
public final class Log {
    
    private static final Map<String, Log> FIND = Maps.newConcurrentMap();
    
    public static Log getMain() {
        Log log = FIND.get(Spark.getMainInstance().getName());
        
        if (log == null) {
            log = new Log(Spark.getMainInstance());
            FIND.put(Spark.getMainInstance().getName(), log);
        }
        
        return log;
    }
    
    public static Log find(@NonNull Plugin plugin) {
        Log log = FIND.get(plugin.getName());
        
        if (log == null) {
            log = new Log(plugin);
            FIND.put(plugin.getName(), log);
        }
        
        return log;
    }
    
    private final Plugin plugin;
    
    public void info(String message, Object... vals) {        
        if (vals != null && vals.length != 0) {
            plugin.getLogger().info(String.format(message, vals));
        } else {
            plugin.getLogger().info(message);
        }
    }
    
    public void warning(String message, Object... vals) {
        if (vals != null && vals.length != 0) {
            plugin.getLogger().warning(String.format(message, vals));
        } else {
            plugin.getLogger().warning(message);
        }
    }
    
    public void severe(String message, Object... vals) {
        if (vals != null && vals.length != 0) {
            plugin.getLogger().severe(String.format(message, vals));
        } else {
            plugin.getLogger().severe(message);
        }
    }
    
    public void debug(String message, Object... vals) {
        if (Spark.isDebug()) {
            info("[Debug] " + message, vals);
        }
    }
    
    public static void sendErrorMessage(CommandSender sender, String message, Object... vals) {
        String msg = "&cError: " + (vals != null && vals.length != 0 ? String.format(message, vals) : message);
        
        sender.sendMessage(Placeholders.parse(msg, sender));
    }
}
