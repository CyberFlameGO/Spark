package codes.goblom.spark.configuration.jafig.types;

import net.burngames.jafig.serialize.JafigSerializer;
import net.burngames.jafig.serialize.types.SerializedPrimitive;
import net.burngames.jafig.serialize.types.SerializedValue;
import org.apache.commons.lang.Validate;

import java.lang.reflect.Field;
import java.util.UUID;

/**
 * @author PaulBGD
 */
public class UUIDSerializer extends JafigSerializer<UUID> {

    public UUIDSerializer() {
        super(UUID.class);
    }

    @Override
    public SerializedValue serialize(UUID uuid, Field field) {
        return new SerializedPrimitive(uuid.toString());
    }

    @Override
    public UUID deserialize(SerializedValue value, Field field) {
        Validate.isTrue(value instanceof SerializedPrimitive, "Value must be Primitive");
        String uuidString = (String) value.toBasic();
        return UUID.fromString(uuidString);
    }
}
