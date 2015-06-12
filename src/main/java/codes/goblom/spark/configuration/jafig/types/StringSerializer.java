package codes.goblom.spark.configuration.jafig.types;

import net.burngames.jafig.serialize.JafigSerializer;
import net.burngames.jafig.serialize.types.SerializedPrimitive;
import net.burngames.jafig.serialize.types.SerializedValue;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.Validate;

import java.lang.reflect.Field;

/**
 * This allows the plugin to use ChatColors in strings,
 * and have them serialized correctly to file as '&'
 *
 * @author PaulBGD
 */
public class StringSerializer extends JafigSerializer<String> {

    public StringSerializer() {
        super(String.class);
    }

    @Override
    public SerializedValue serialize(String s, Field field) {
        return new SerializedPrimitive(s.replace(ChatColor.COLOR_CHAR, '&'));
    }

    @Override
    public String deserialize(SerializedValue value, Field field) {
        Validate.isTrue(value instanceof SerializedPrimitive, "Value must be primitive");
        String string = (String) value.toBasic();
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}
