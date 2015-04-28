package codes.goblom.core.conversation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;

/**
 *
 * @author Goblom
 */
public abstract class ConversationStep {
    
    protected String getPrefix() {
        return getHandle().prefix.getPrefix();
    }
    
    protected String generate(String message) {
        return ChatColor.translateAlternateColorCodes('&', getPrefix() + message);
    }
    
    protected void sendMessage(ConversationContext context, String message) {
        context.getForWhom().sendRawMessage(generate(message));
    }
    
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    protected ConversationSequencerImpl handle;
    
    public abstract String getMessage(ConversationContext context);
    
    public boolean isValid(ConversationContext context, String input) {
        return true;
    }
    
    public abstract String getFailedText(ConversationContext context, String input);
    
    public abstract StepResult run(ConversationContext context, String input);
    
    public boolean canSkip(ConversationContext context) {
        return true;
    }
    
    public enum StepResult {
        MOVE_TO_NEXT_STEP,
        REPEAT_STEP,
        INVALID
    }
}
