/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.spark.misc.map;

import codes.goblom.spark.internals.ExecutorNoArgs;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapView;
import org.bukkit.map.MapView.Scale;

/**
 *
 * @author Goblom
 */
@RequiredArgsConstructor
public final class ImageRenderer extends AbstractMapRenderer {

    private final BufferedImage image;
    
    @Getter
    private boolean rendered = false;
    
    public ImageRenderer(File imageFile) throws IOException {
        this.image = ImageIO.read(imageFile);
    }
    
    public ImageRenderer(ExecutorNoArgs<BufferedImage, Throwable> executor) throws Throwable {
        this.image = executor.execute();
        
        if (this.image == null) {
            throw new NullPointerException("image is null");
        }
    }
    
//    @Override
//    public void initialize(MapView map) { 
//        SafeField<List<MapRenderer>> f = new SafeField(map.getClass(), "renderers");
//                  f.setAccessible(true);
//                  
//        List<MapRenderer> renderers = f.get(map);
//                          renderers.clear();
//    }
    
    @Override
    public void render(MapView map, MapCanvas canvas, Player player) {
        if (!rendered && image != null) {
            map.setScale(Scale.FARTHEST);
            canvas.drawImage(0, 0, image);
            
            rendered = true;

            player.sendMap(map);
        }
    }
    
}
