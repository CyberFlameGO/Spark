/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.core.misc.utils;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.map.MapView;

/**
 *
 * @author Goblom
 */
public class MapUtils {
    
    public static MapView createMap(World world) {
        return Bukkit.createMap(world);
    }
    
    public static MapView createMap() {
        return createMap(Bukkit.getWorlds().get(0));
    }
    
    public static void removeRenderers(MapView map) {
        map.getRenderers().stream().forEach((mr) -> {
            map.removeRenderer(mr);
        });
    }
}
