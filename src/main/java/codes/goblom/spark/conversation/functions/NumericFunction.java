package codes.goblom.spark.conversation.functions;

import codes.goblom.spark.conversation.ConversationStep;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.conversations.ConversationContext;

/**
 *
 * @author Goblom
 */
public abstract class NumericFunction extends ConversationStep {
    
    @Override
    public boolean isValid(ConversationContext context, String input) {
        return NumberUtils.isNumber(input);
    }
}
