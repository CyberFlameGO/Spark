/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.spark.internals.tools;

import com.google.common.collect.Maps;
import java.util.Map;
import lombok.Getter;
import org.bukkit.ChatColor;

/**
 *
 * @author Goblom
 */
public final class JSONColor {
    
    private static final Map<String, JSONColor> byName = Maps.newHashMap();
    private static final Map<ChatColor, JSONColor> byColor = Maps.newHashMap();
    
    /**
     * Colors
     */
    public static final JSONColor 
            BLACK, DARK_GREEN, DARK_BLUE, DARK_AQUA, DARK_RED, 
            DARK_PURPLE, DARK_GRAY, GOLD, GRAY, BLUE, GREEN, 
            AQUA, RED, LIGHT_PURPLE, YELLOW, WHITE;
    
    /**
     * Styles
     */
    public static final JSONColor 
            OBFUSCATED, BOLD, STRIKETHROUGH, 
            UNDERLINE, ITALIC;
    
    static {
        BLACK = $("black", ChatColor.BLACK);
        DARK_GREEN = $("dark_green", ChatColor.DARK_GREEN);
        DARK_BLUE = $("dark_blue", ChatColor.DARK_BLUE);
        DARK_AQUA = $("dark_aqua", ChatColor.DARK_AQUA);
        DARK_RED = $("dark_red", ChatColor.DARK_RED);
        DARK_PURPLE = $("dark_purple", ChatColor.DARK_PURPLE);
        DARK_GRAY = $("dark_gray", ChatColor.DARK_GRAY);
        GOLD = $("gold", ChatColor.GOLD);
        GRAY = $("gray", ChatColor.GRAY);
        BLUE = $("blue", ChatColor.BLUE);
        GREEN = $("green", ChatColor.GREEN);
        AQUA = $("aqua", ChatColor.AQUA);
        RED = $("red", ChatColor.RED);
        LIGHT_PURPLE = $("light_purple", ChatColor.LIGHT_PURPLE);
        YELLOW = $("yellow", ChatColor.YELLOW);
        WHITE = $("white", ChatColor.WHITE);
        
        OBFUSCATED = $("obfuscated", ChatColor.MAGIC);
        BOLD = $("bold", ChatColor.BOLD);
        STRIKETHROUGH = $("strikethrough", ChatColor.STRIKETHROUGH);
        UNDERLINE = $("underline", ChatColor.UNDERLINE);
        ITALIC = $("italic", ChatColor.ITALIC);
    }
    
    private static JSONColor $(String str, ChatColor color) {
        str = str.toLowerCase();
        JSONColor json = new JSONColor(str, color);
        
        byName.put(str, json);
        byColor.put(color, json);
        
        return json;
    }
    
    public static JSONColor fromName(String str) {        
        return byName.get(str.toLowerCase().replace(" ", "_"));
    }
    
    public static JSONColor fromColor(ChatColor color) {
        return byColor.get(color);
    }
    
    @Getter
    private final String minecraftColor;
    
    @Getter
    private final boolean color;
    
    @Getter
    private final ChatColor bukkitColor;
    
    public JSONColor(String minecraftColor, ChatColor bukkitColor) {
        this.minecraftColor = minecraftColor;
        this.bukkitColor = bukkitColor;
        this.color = bukkitColor.isColor();
    }
}
