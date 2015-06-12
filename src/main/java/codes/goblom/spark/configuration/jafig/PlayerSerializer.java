package codes.goblom.spark.configuration.jafig;

import net.burngames.jafig.serialize.JafigSerializer;
import net.burngames.jafig.serialize.types.SerializedPrimitive;
import net.burngames.jafig.serialize.types.SerializedValue;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.lang.reflect.Field;
import java.util.UUID;

/**
 * @author PaulBGD
 */
public class PlayerSerializer extends JafigSerializer<OfflinePlayer> {

    public PlayerSerializer() {
        super(OfflinePlayer.class);
    }

    @Override
    public SerializedValue serialize(OfflinePlayer offlinePlayer, Field field) {
        return new SerializedPrimitive(offlinePlayer.getUniqueId().toString());
    }

    @Override
    public OfflinePlayer deserialize(SerializedValue value, Field field) {
        Validate.isTrue(value instanceof SerializedPrimitive, "Value must be Primitive");
        String uuidString = (String) value.toBasic();
        UUID uuid = UUID.fromString(uuidString);
        OfflinePlayer player = Bukkit.getPlayer(uuid);
        if (player == null) {
            player = Bukkit.getOfflinePlayer(uuid);
        }
        return player;
    }
}
