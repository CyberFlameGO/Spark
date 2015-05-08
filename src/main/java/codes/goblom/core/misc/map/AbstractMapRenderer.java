/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.core.misc.map;

import codes.goblom.core.misc.utils.MapUtils;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

/**
 *
 * @author Goblom
 */
abstract class AbstractMapRenderer extends MapRenderer {
    
    boolean doRemoveRenderers = true;
    
    public AbstractMapRenderer() {
        this(false);
    }
    
    public AbstractMapRenderer(boolean contextual) {
        super(contextual);
    }
    
    @Override
    public void initialize(MapView map) {
        if (doRemoveRenderers) {
            MapUtils.removeRenderers(map);
        }
    }
}
