package codes.goblom.spark.configuration.jafig.types;

import net.burngames.jafig.serialize.JafigSerializer;
import net.burngames.jafig.serialize.types.SerializedPrimitive;
import net.burngames.jafig.serialize.types.SerializedValue;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;

import java.lang.reflect.Field;

/**
 * @author PaulBGD
 */
public class MaterialSerializer extends JafigSerializer<Material> {

    public MaterialSerializer() {
        super(Material.class);
    }

    @Override
    public SerializedValue serialize(Material material, Field field) {
        return new SerializedPrimitive(material.name());
    }

    @Override
    public Material deserialize(SerializedValue serializedValue, Field field) {
        Validate.isTrue(serializedValue instanceof SerializedPrimitive, "Value must be Primitive");
        String material = (String) serializedValue.toBasic();
        return Material.valueOf(material);
    }
}
