package codes.goblom.spark.configuration.jafig;

import net.burngames.jafig.serialize.JafigSerializer;
import net.burngames.jafig.serialize.types.SerializedJafig;
import net.burngames.jafig.serialize.types.SerializedPrimitive;
import net.burngames.jafig.serialize.types.SerializedValue;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * @author PaulBGD
 */
public class LocationSerializer extends JafigSerializer<Location> {

    public LocationSerializer() {
        super(Location.class);
    }

    @Override
    public SerializedValue serialize(Location location, Field field) {
        HashMap<String, SerializedValue> values = new HashMap<>();
        values.put("world", new SerializedPrimitive(location.getWorld().getName()));
        values.put("x", new SerializedPrimitive(location.getX()));
        values.put("y", new SerializedPrimitive(location.getY()));
        values.put("z", new SerializedPrimitive(location.getZ()));
        values.put("yaw", new SerializedPrimitive(location.getYaw()));
        values.put("pitch", new SerializedPrimitive(location.getPitch()));
        return new SerializedJafig(values);
    }

    @Override
    public Location deserialize(SerializedValue value, Field field) {
        Validate.isTrue(value instanceof SerializedJafig, "Value must be SerializedJafig");
        SerializedJafig jafig = (SerializedJafig) value;
        HashMap<String, SerializedValue> children = jafig.getChildren();

        String world = (String) children.get("world").toBasic();
        World bukkitWorld = Bukkit.getWorld(world);
        Validate.notNull(bukkitWorld, "World " + world + " does not exist anymore!");

        double x = (double) children.get("x").toBasic();
        double y = (double) children.get("y").toBasic();
        double z = (double) children.get("z").toBasic();
        float yaw = (float) children.get("yaw").toBasic();
        float pitch = (float) children.get("pitch").toBasic();

        return new Location(bukkitWorld, x, y, z, yaw, pitch);
    }
}
