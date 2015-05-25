/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.spark.internals.tools;

import codes.goblom.spark.SparkPlugin;
import codes.goblom.spark.Log;
import codes.goblom.spark.internals.Executor;
import codes.goblom.spark.internals.ExecutorArgs;
import codes.goblom.spark.internals.Validater;
import codes.goblom.spark.misc.utils.PlayerUtils;
import codes.goblom.spark.misc.utils.Utils;
import codes.goblom.spark.reflection.Reflection;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.conversations.Conversable;
import org.bukkit.entity.Player;

/**
 *
 * @author Goblom
 */
public class Placeholders {
    
    // TODO: Add more
    static {
        Utils.addValidaterCheck((Validater<Placeholder>) (placeholder) -> {
            return Utils.isValid(placeholder.getKey());
        });
        
        register(new AbstractPlaceholder("{player:name}", false, true) {
            
            @Override
            public Object execute(ExecutorArgs args) throws Throwable {
                return args.getAs(Player.class).getName();
            }
        });
        
        register(new AbstractPlaceholder("{player:level}", false, true) {

            @Override
            public Object execute(ExecutorArgs args) throws Throwable {
                return args.getAs(Player.class).getLevel();
            }
        });
        
        register(new AbstractPlaceholder("{player:ping}", false, true) {

            @Override
            public Object execute(ExecutorArgs args) throws Throwable {
                return PlayerUtils.getPing(args.getAs(Player.class));
            }
        });
        
        register(new AbstractPlaceholder("{server:ip}", false, false){

            @Override
            public Object execute(ExecutorArgs args) throws Throwable {
                return Bukkit.getIp();
            }
        });
        
        register(new AbstractPlaceholder("{server:name}", false, false) {

            @Override
            public Object execute(ExecutorArgs args) throws Throwable {
                return Bukkit.getServerName();
            }
        });
        
        register(new AbstractPlaceholder("{server:version}", false, false) {

            @Override
            public Object execute(ExecutorArgs args) throws Throwable {
                return Reflection.getVersion();
            }
        });
    }
    
    @AllArgsConstructor
    @RequiredArgsConstructor
    public static abstract class AbstractPlaceholder implements Placeholder {
        final String key;
        boolean regex = false, requiresPlayer = false;
        
        @Override
        public String getKey() { return key; }
        
        @Override
        public boolean isRegex() { return regex; }
        
        @Override
        public boolean requiresPlayer() { return requiresPlayer; }
    }
    
    private Placeholders() { }
    
    private static Map<String, Placeholder> placeholders = Maps.newConcurrentMap();
    private static Map<String, Pattern> patterns = Maps.newConcurrentMap();
    
    public static boolean register(Placeholder placeholder) {
        if (!Utils.isValid(placeholder)) {
            Log.warning("A Placeholders key cannot be null or empty");
            return false;
        }
        
        if (placeholders.containsKey(placeholder.getKey())) {
            return false;
        }
        
        if (placeholder.isRegex()) {
            patterns.put(placeholder.getKey(), Pattern.compile(placeholder.getKey()));
        }
                
        placeholders.put(placeholder.getKey(), placeholder);
        
        return true;
    }
    
    public static String parse(String msg) {
        return parse(msg, null);
    }
    
    public static String parse(String msg, Conversable convo) {
        return parse(msg, convo instanceof Player ? (Player) convo : null);
    }
    
    public static String parse(String msg, Player player) {
        for (Placeholder p : placeholders.values()) {
            if (p.requiresPlayer() && player == null) {
                continue;
            }
            
            if (p.isRegex()) {
                Pattern pattern;
                
                if (patterns.containsKey(p.getKey())) {
                    pattern = patterns.get(p.getKey());
                } else {
                    patterns.put(p.getKey(), pattern = Pattern.compile(p.getKey()));
                }
                
                Matcher matcher = pattern.matcher(msg);
                
                try {
                    if (matcher.matches()) {
                        msg = matcher.replaceAll(p.execute(ExecutorArgs.Builder().put(player).build()).toString());
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            } else {
                try {
                    msg = msg.replace(p.getKey(), p.execute(ExecutorArgs.Builder().put(player).build()).toString());
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
        
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
    
    public static interface Placeholder extends Executor<Object, Throwable> {
        
        String getKey();
        
        default boolean isRegex() {
            return false;
        }
        
        default boolean requiresPlayer() {
            return false;
        }
    }
}
