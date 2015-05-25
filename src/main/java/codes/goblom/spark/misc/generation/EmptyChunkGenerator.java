/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.spark.misc.generation;

import java.util.Random;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

/**
 *
 * @author Goblom
 */
public class EmptyChunkGenerator extends ChunkGenerator {

    private static final byte[] chunk = new byte[256 * 16 * 16];

    @Override
    public byte[] generate(World world, Random rand, int cx, int cz) {
        return chunk;
    }
}
