/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.core.internals.tools;

import codes.goblom.core.internals.Executor;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author Goblom
 */
public class Placeholders {
    
    // TODO: Add more
    static {
        register(new AbstractPlaceholder("{player:name}", false, true) {
            
            @Override
            public Object execute(Player[] args) throws Throwable {
                return args[0].getName();
            }
        });
        
        register(new AbstractPlaceholder("{player:level}", false, true) {

            @Override
            public Object execute(Player[] args) throws Throwable {
                return args[0].getLevel();
            }
        });
    }
    
    static abstract class AbstractPlaceholder implements Placeholder {
        final String key;
        boolean regex = false, requiresPlayer = false;
        
        AbstractPlaceholder(String key, boolean regex, boolean requires) {
            this.key = key;
            this.regex = regex;
            this.requiresPlayer = requires;
        }
        
        @Override
        public String getKey() { return key; }
        
        @Override
        public boolean isRegex() { return regex; }
        
        @Override
        public boolean requiresPlayer() { return requiresPlayer; }
    }
    
    private static Map<String, Placeholder> placeholders = Maps.newConcurrentMap();
    private static Map<String, Pattern> patterns = Maps.newConcurrentMap();
    
    public static boolean register(Placeholder placeholder) {
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
                        msg = matcher.replaceAll(p.execute(new Player[] { player }).toString());
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            } else {
                try {
                    msg = msg.replace(p.getKey(), p.execute(new Player[] { player }).toString());
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
        
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
    
    public static interface Placeholder extends Executor<Object, Player, Throwable> {
        
        String getKey();
        
        default boolean isRegex() {
            return false;
        }
        
        default boolean requiresPlayer() {
            return false;
        }
    }
}
