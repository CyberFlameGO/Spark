/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.spark.misc.map;

import codes.goblom.spark.internals.tools.Placeholders;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapFont;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

/**
 *
 * @author Goblom
 */
public abstract class TextRenderer extends AbstractMapRenderer {

    @Getter
    @Setter
    private String defaultText;
    
    @Getter
    @Setter
    private MapFont defaultFont;
    
    @Setter
    private int defaultX, defaultY;
    
    public TextRenderer(String text) {
        this(text, 0, 0);
    }
    
    public TextRenderer(String text, int x, int y) {
        this(text, x, y, new MapFont());
    }
    
    public TextRenderer(String text, int x, int y, MapFont font) {
        super(true);
        
        this.defaultX = x;
        this.defaultY = y;
        this.defaultText = text;
        this.defaultFont = font;
    }
    
    @Override
    public void render(MapView map, MapCanvas canvas, Player player) {
        RenderData data = new RenderData();
        
        onRender(map, player, data);
        
        canvas.drawText(data.x, data.y, data.font, Placeholders.parse(data.text, player));
        
        player.sendMap(map);
    }
    
    public abstract void onRender(MapView map, Player player, RenderData data);
            
    public class RenderData {
        
        public int x = defaultX;
        public int y = defaultY;
        public MapFont font = defaultFont;
        public String text = defaultText;
    }
}
