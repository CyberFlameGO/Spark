package codes.goblom.spark.configuration.jafig;

import net.burngames.jafig.serialize.JafigSerializer;
import net.burngames.jafig.serialize.types.SerializedPrimitive;
import net.burngames.jafig.serialize.types.SerializedValue;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.lang.reflect.Field;

/**
 * @author PaulBGD
 */
public class WorldSerializer extends JafigSerializer<World> {

    public WorldSerializer() {
        super(World.class);
    }

    @Override
    public SerializedValue serialize(World world, Field field) {
        return new SerializedPrimitive(world.getName());
    }

    @Override
    public World deserialize(SerializedValue value, Field field) {
        Validate.isTrue(value instanceof SerializedPrimitive, "Value must be Primitive");
        String world = (String) value.toBasic();
        World bukkitWorld = Bukkit.getWorld(world);
        Validate.notNull(bukkitWorld, "World " + world + " does not exist anymore");
        return bukkitWorld;
    }
}
