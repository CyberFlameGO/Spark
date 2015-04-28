package codes.goblom.core.conversation.functions;

import codes.goblom.core.conversation.ConversationStep;
import codes.goblom.core.misc.utils.PlayerUtils;
import org.bukkit.conversations.ConversationContext;

/**
 *
 * @author Goblom
 */
public abstract class PlayerNameFunction extends ConversationStep {
    
    @Override
    public boolean isValid(ConversationContext context, String input) {
//        Player player = Bukkit.getPlayer(input);
//        
//        if (player == null) {
//            for (Player online : Bukkit.getOnlinePlayers()) {
//                if (online.getName().contains(input)) {
//                    player = online;
//                    break;
//                }
//            }
//        }
//        
//        return player != null;
        return PlayerUtils.matchPlayer(input) != null;
    }
}
