package codes.goblom.spark.configuration.jafig.types;

import net.burngames.jafig.serialize.JafigSerializer;
import net.burngames.jafig.serialize.SerializeUtil;
import net.burngames.jafig.serialize.types.SerializedJafig;
import net.burngames.jafig.serialize.types.SerializedValue;
import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author PaulBGD
 */
public class ItemStackSerializer extends JafigSerializer<ItemStack> {

    public ItemStackSerializer() {
        super(ItemStack.class);
    }

    @Override
    public SerializedValue serialize(ItemStack itemStack, Field field) {
        return SerializeUtil.serialize(itemStack.serialize());
    }

    @Override
    public ItemStack deserialize(SerializedValue serializedValue, Field field) {
        Validate.isTrue(serializedValue instanceof SerializedJafig, "Value must be SerializedJafig");
        SerializedJafig jafig = (SerializedJafig) serializedValue;
        Map<String, Object> data = jafig.toBasic();
        return ItemStack.deserialize(data);
    }
}
