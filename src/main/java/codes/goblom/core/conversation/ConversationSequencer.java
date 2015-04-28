/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.core.conversation;

import codes.goblom.core.misc.Sequence;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;

/**
 *
 * @author Goblom
 */
public interface ConversationSequencer extends Sequence<ConversationStep> {
    
    boolean isAborted();
    
    boolean isStarted();
    
    Conversation getConversation();
    
    ConversationFactory getFactory();
    
    Conversable getConversable();
    
    void start();
    
    void abort();
    
    default void stop() {
        abort();
    }
    
    String getPrefix();
    
    void setPrefix(String prefix);
}
