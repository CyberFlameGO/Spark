package codes.goblom.spark.conversation;

import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;

/**
 *
 * @author Goblom
 */
final class FinalStep extends ConversationStep {

    protected FinalStep() { }
    
    protected FinalStep(ConversationSequencer convo) {        
        convo.abort();
    }
    
    @Override
    public String getMessage(ConversationContext context) {
        return "Congratulations! I am done talking to you; You are so needy";
    }

    @Override
    public String getFailedText(ConversationContext context, String input) {
        return generate(ChatColor.RED + "How did I get here!? What have you done to me!!!");
    }

    @Override
    public StepResult run(ConversationContext context, String input) {
        return StepResult.REPEAT_STEP;
    }
    
}
