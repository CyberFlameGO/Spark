package codes.goblom.spark.conversation;

import codes.goblom.spark.SparkPlugin;
import codes.goblom.spark.misc.utils.Utils;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.bukkit.conversations.Conversable;
import org.bukkit.plugin.java.JavaPlugin;

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
    
    private ConversationApi() { }
    
    protected static Map<Conversable, ConversationSequencer> conversations = new HashMap();
    
    public static ConversationSequencer prepare(Conversable convo, boolean allowConsole, List<ConversationStep> steps) {
        ConversationStep[] array = steps.toArray(new ConversationStep[steps.size()]);
        return prepare(convo, allowConsole, array);
    }
    
    public static ConversationSequencer prepare(Conversable convo, boolean allowConsole, ConversationStep[] steps) {
        if (!Utils.isValid(steps)) {
            throw new UnsupportedOperationException("Steps canno have zero entries");
        }
        
        ConversationSequencerImpl sequence = new ConversationSequencerImpl(JavaPlugin.getProvidingPlugin(steps[0].getClass()), convo, allowConsole, steps);
        
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
