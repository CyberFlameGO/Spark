/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.spark.internals.tools;

import codes.goblom.spark.internals.Validater;
import codes.goblom.spark.internals.tools.JSONChat.JsonEntry;
import codes.goblom.spark.misc.utils.Utils;
import com.google.common.collect.Maps;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * 
 * @author Goblom
 */
public final class JSONChat {

    public static void main(String[] args) {
        JSONChat main = JSONChat.withText("Hello, ")
                 .withColor(JSONColor.YELLOW)
                 .withExtra(JSONChat.withText("Goblom").withColor(JSONColor.LIGHT_PURPLE, JSONColor.ITALIC, JSONColor.UNDERLINE))
                 .withExtra(JSONChat.withText("!").withColor(JSONColor.YELLOW, JSONColor.ITALIC));
        
        System.out.println(main);
        
    }
    
    static {
        Utils.addValidaterCheck((Validater<JsonEntry>) (JsonEntry obj) -> {
            return (obj.key() != null && !obj.key().isEmpty()) && obj.entries() != null;
        });
    }
    
    public static JSONChat withEmpty() {
        return withText("");
    }
    
    public static JSONChat withText(String str) {
        return new JSONChat(str);
    }
    
    private final JSONObject json = new JSONObject();
    private final List<JSONChat> extra = new JSONArray();
    
    private JSONChat(String text) { 
        json.put("text", text);
    }
    
    public JSONChat withEntry(JsonEntry entry) {
        if (!Utils.isValid(entry)) {
            throw new UnsupportedOperationException("JsonEntry is not valid");
        }
        
        json.put(entry.key(), new JSONObject(entry.entries()));
        
        return this;
    }
    
    public JSONChat withHover(HoverAction action, String value) {
        return withEntry(new HoverEventEntry(action, value));
    }
    
    public JSONChat withClick(ClickAction action, String value) {
        return withEntry(new ClickEventEntry(action, value));
    }
    
    public JSONChat withColor(JSONColor... colors) {
        for (JSONColor color : colors) {
            if (color.isColor()) {
                json.put("color", color.getMinecraftColor());
            } else {
                json.put(color.getMinecraftColor(), "true");
            }
        }
        
        return this;
    }
    
    public JSONChat withExtra(JSONChat... chat) {
        extra.addAll(Arrays.asList(chat));
        json.put("extra", extra);
        
        return this;
    }
    
    @Override
    public String toString() {
        return json.toJSONString();
    }
    
    static interface Action {
        
        public String actionType();
    }
    
    public static interface JsonEntry {
        
        String key();
        
        Map<String, String> entries();
    }
    
    @RequiredArgsConstructor
    static abstract class EventEntry<T extends Action> implements JsonEntry {
        
        private final T action;
        private final String value;
        
        @Override
        public Map<String, String> entries() {
            String[] actionEntry = { "action", action.actionType() };
            String[] valueEntry = { "value", value };
            
            return toMapFrom(actionEntry, valueEntry);
        }
    }
    
    public static enum HoverAction implements Action {
        SHOW_TEXT, SHOW_ITEM, SHOW_ENTITY, SHOW_ACHIEVEMENT;

        @Override
        public String actionType() {
            return name().toLowerCase();
        }
    }
    
    public static enum ClickAction implements Action {
        RUN_COMMAND, SUGGEST_COMMAND, CHANGE_PAGE, 
        OPEN_URL {
            @Override
            boolean isValid(String str) {
                try {
                    new URL(str);
                } catch (Exception e) { return false; }
                
                return true;
            }
        };
        
        boolean isValid(String str) {
            return true;
        }
        
        @Override
        public String actionType() {
            return name().toLowerCase();
        }
    }
    
    public static class HoverEventEntry extends EventEntry<HoverAction> {

        public HoverEventEntry(HoverAction action, String value) {
            super(action, value);
        }
        
        @Override
        public String key() {
            return "hoverEvent";
        }
    }
    
    public static class ClickEventEntry extends EventEntry<ClickAction> {

        public ClickEventEntry(ClickAction action, String value) {
            super(action, value);
        }
        
        @Override
        public String key() {
            return "clickEvent";
        }
    }
    
    /// #### YOLO
    static Map<String, String> toMapFrom(String[]... entries) {
        Map<String, String> map = Maps.newHashMap();
        
        for (String[] entry : entries) {
            if (entry.length != 2) continue;
            
            map.put(entry[0], entry[1]);
        }
        
        return map;
    }
}
