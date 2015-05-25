package codes.goblom.spark.conversation;

import codes.goblom.spark.Log;
import lombok.Getter;
import codes.goblom.spark.conversation.ConversationStep.StepResult;
import codes.goblom.spark.internals.Spark;
import codes.goblom.spark.internals.tools.Placeholders;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.ConversationPrefix;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;

/**
 *
 * @author Goblom
 */
class ConversationSequencerImpl extends ValidatingPrompt implements ConversationSequencer {
    protected final ConversationSequencerPrefix prefix = new ConversationSequencerPrefix();
    
    private int index = 0;
    private ConversationStep[] steps;
    
    @Getter
    private boolean aborted = false, started = false;
    
    @Getter
    private Conversation conversation = null;
    
    @Getter
    private ConversationFactory factory;
    
    @Getter
    private Conversable conversable;
    
    private ConversationSequencerImpl() { }
    
    ConversationSequencerImpl(Conversable player, boolean allowConsole, ConversationStep[] steps) {
        ConversationFactory factory = new ConversationFactory(Spark.getInstance());
                            factory.withLocalEcho(false);
                            factory.withPrefix(prefix);
                            factory.withModality(true);
                            factory.withEscapeSequence("abort");
                            factory.withEscapeSequence("cancel");
                            factory.withFirstPrompt(this);
        if (!allowConsole) {
            factory.thatExcludesNonPlayersWithMessage("No, bad console");
        }
        
        this.steps = steps;
        this.conversable = player;
        this.factory = factory;
        
        for (ConversationStep step : steps) {
            step.setHandle(this);
        }
        
        Log.debug("[ConversationSequencer] Registered %s Steps for %s.", steps.length, player instanceof CommandSender ? ((CommandSender) player).getName() : player.toString());
    }
    
    @Override
    public ConversationStep current() {
        try {
            return steps[index];
        } catch (Exception e) { }
        
        return new FinalStep(this);
    }

    @Override
    public ConversationStep get(int index) {
        return steps[index];
    }
    
    @Override
    public ConversationStep next() {
        ++index;
        if (index >= steps.length) {
            return null;
        } else {
            return steps[index];
        }
    }
    
    @Override
    public ConversationStep previous() {
        --index;
        if (index <= 0) {
            index = steps.length - 1;
            return steps[index];
        } else {
            return steps[index];
        }
    }
        
    @Override
    public void set(int index, ConversationStep step) {
        try {
            steps[index] = step;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    protected final boolean isInputValid(ConversationContext context, String input) {
        boolean special = input.equalsIgnoreCase("back") || input.equalsIgnoreCase("next") || input.equalsIgnoreCase("skip") || input.startsWith("/");
        return special || current().isValid(context, input);
    }

    @Override
    protected final Prompt acceptValidatedInput(ConversationContext context, String input) {
        if (input.startsWith("/")) {
            if (context.getForWhom() instanceof CommandSender) {
                Bukkit.dispatchCommand((CommandSender) context.getForWhom(), input);
            }
            
            return this;
        }
        
        if (input.equalsIgnoreCase("back")) {
            previous();
            return this;
        }
        
        if (input.equalsIgnoreCase("next") || input.equalsIgnoreCase("skip")) {
            if (current().canSkip(context)) {
                next();
            } else {
                context.getForWhom().sendRawMessage(prefix.getPrefix() + ChatColor.RED + "You are not allowed to skip this step.");
            }
            
            return this;
        }

        StepResult result = current().run(context, input);
        
        switch (result) {
            case MOVE_TO_NEXT_STEP:
                if (next() == null) {
                    ConversationApi/*.getApi()*/.conversations.remove(getConversable());
                    return Prompt.END_OF_CONVERSATION;
                }
                break;
            case INVALID:
                getFailedValidationText(context, input);  
            case REPEAT_STEP: break; //do nothing
        }
        
        return this;
    }

    @Override
    public final String getPromptText(ConversationContext context) {
        return Placeholders.parse(current().getMessage(context), this.conversable);
    }
    
    @Override
    protected final String getFailedValidationText(ConversationContext context, String input) {
        return Placeholders.parse(current().getFailedText(context, input), this.conversable);
    }
    
    @Override
    public final void start() {   
        if (conversation == null) {
            this.conversation = factory.buildConversation(this.conversable);
        }
        
        conversation.begin();
        
        this.started = true;
    }
    
    @Override
    public final void abort() {
        if (aborted) {
            return;
        }
        
        if (conversation == null) {
            this.conversation = factory.buildConversation(this.conversable);
        }
        
        conversation.abandon();
        
        this.aborted = true;
        
        ConversationApi.removeSequence(conversable);
    }
    
    @Override
    public final void setPrefix(String prefix) {
        this.prefix.setPrefix(prefix);
    }
    
    @Override
    public final String getPrefix() {
        return prefix.getPrefix();
    }
    
    protected class ConversationSequencerPrefix implements ConversationPrefix {

        @Getter
        @Setter
        public String prefix = "&7[&bAI&7]";
        
        @Override
        public String getPrefix(ConversationContext context) {
            return ChatColor.translateAlternateColorCodes('&', prefix) + ChatColor.RESET + " ";
        }
    }
}
