package codes.goblom.core.conversation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.bukkit.conversations.Conversable;

/**
 *
 * @author Goblom
 */
public class ConversationApi {
    
//    private static ConversationApi api = null;
//    
//    public static ConversationApi getApi() {
//        if (api == null) {
//            api = new ConversationApi();
//        }
//        
//        return api;
//    }
    
    protected static Map<Conversable, ConversationSequencer> conversations = new HashMap();
    
    public static ConversationSequencer prepare(Conversable convo, boolean allowConsole, List<ConversationStep> steps) {
        ConversationStep[] array = steps.toArray(new ConversationStep[steps.size()]);
        return prepare(convo, allowConsole, array);
    }
    
    public static ConversationSequencer prepare(Conversable convo, boolean allowConsole, ConversationStep[] steps) { 
        ConversationSequencerImpl sequence = new ConversationSequencerImpl(convo, allowConsole, steps);
        
        conversations.put(convo, sequence);
        
        return sequence;
    }
    
    public static ConversationSequencer getSequence(Conversable convo) {
        return conversations.get(convo);
    }
    
    public static void removeSequence(Conversable convo) {
        conversations.remove(convo).abort();
    }
    
    public static Iterator<Map.Entry<Conversable, ConversationSequencer>> iterator() {
        return Collections.unmodifiableMap(conversations).entrySet().iterator();
    }
}
