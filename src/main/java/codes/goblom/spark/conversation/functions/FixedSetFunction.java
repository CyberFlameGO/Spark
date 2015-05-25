package codes.goblom.spark.conversation.functions;

import java.util.List;
import codes.goblom.spark.conversation.ConversationStep;
import org.bukkit.conversations.ConversationContext;

/**
 *
 * @author Goblom
 */
public abstract class FixedSetFunction extends ConversationStep {
    
    public abstract List<String> getFixedSet();
    
    @Override
    public boolean isValid(ConversationContext context, String input) {
        return getFixedSet().contains(input);
    }
}
