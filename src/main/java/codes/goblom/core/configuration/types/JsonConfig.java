/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.core.configuration.types;

import codes.goblom.core.Log;
import codes.goblom.core.configuration.Config;
import codes.goblom.core.configuration.ConfigType;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import lombok.Getter;
import org.bukkit.plugin.Plugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Goblom
 */
public class JsonConfig implements Config {

    @Getter
    private final File file;
    
    @Getter
    private JSONObject json;
    
    private String tag = null;
    
    @Getter
    private JsonConfig parent = null;
    
    public JsonConfig(Plugin plugin, String file) {
        this(plugin, null, file);
    }
    
    public JsonConfig(Plugin plugin, File external, String f) {
        f = f.endsWith(".json") ? f : f + ".json";
        
        this.file = new File(external == null ? plugin.getDataFolder() : external, f);
        
        if (!this.file.exists()) {
            try {
                plugin.saveResource(f, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        if (!this.file.exists()) {
            try {
                this.file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        try {
            String text = readFile(file);
            
            if (text == null || text.equals("")) {
                this.json = new JSONObject();
            } else {
                this.json = new JSONObject(text);
            }
        } catch (IOException | JSONException e) {
            Log.severe("Unable to load JsonConfig %s. Error: %s", f, e.getMessage());
        }
        
        Log.warning("You are using an experimental version of JsonConfig. Some features might not work.");
    }
    
    private JsonConfig(JsonConfig parent, String tag, JSONObject object) {
        this.parent = parent;
        this.json = object;
        this.tag = tag;
        
        this.file = parent.file;
    }
    
    private String readFile(File file) throws IOException {
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdir();
            }
            
            file.createNewFile();
        }
        
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

        char[] buff = new char[512];

        while (true) {
            int len = br.read(buff, 0, buff.length);
            if (len == -1) {
                break;
            }
            sb.append(buff, 0, len);
        }
        return sb.toString();
    }
    
    @Override
    public ConfigType getType() {
        return ConfigType.JSON;
    }

    @Override
    public boolean contains(String tag) {
        return json.has(tag);
    }

    @Override
    public void set(String tag, Object value, boolean save) {
        json.put(tag, value);
        
        if (save) {
            save();
        }
    }

    public JSONArray getJsonArray(String tag) {
        return json.getJSONArray(tag);
    }
    
    public JSONObject getJSONObject(String tag) {
        return json.getJSONObject(tag);
    }
    
    public JsonConfig deeper(String tag) {
        return new JsonConfig(this, tag, getJSONObject(tag));
    }
    
    @Override
    public <T> T get(String tag) {
        return (T) json.get(tag);
    }

    @Override
    public <T> T get(String tag, T def) {
        if (!contains(tag)) {
            set(tag, def, true);
            
            return def;
        }
        
        return (T) get(tag);
    }

    @Override
    // TODO: Make PrettyJsonWriter work
    public void save() {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new FileWriter(this.file));
            
            try (BufferedWriter bw = new BufferedWriter(pw)) {
                
                if (parent != null) {
                    parent.set(tag, json, true);
                    
                    bw.append(parent.json.toString());
                } else {
                    bw.append(json.toString());
                }
                
                bw.flush();
            }
            
//                if (parent != null) {
//                    parent.set(tag, json, true);
//                    
//                    Writer writer = parent.json.write(new PrettyJsonWriter());
//                           writer.flush();
//                           writer.close();
//                } else {
//                    Writer writer = json.write(new PrettyJsonWriter());
//                           writer.flush();
//                           writer.close();
//                }
        } catch (IOException ex) {
            Log.severe("Unable to save JsonConfig %s. Error: %s", file.getName(), ex.getMessage());
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
    }

    @Override
    @Deprecated // Will cause problems if not on parent JsonConfig
    public void reload() {
        try {
            String text = readFile(file);
            
            if (text == null || text.equals("")) {
                this.json = new JSONObject();
            } else {
                this.json = new JSONObject(text);
            }
        } catch (IOException | JSONException e) {
            Log.severe("Unable to load JsonConfig %s. Error: %s", file.getName(), e.getMessage());
        }
    }
    
    public boolean hasParent() {
        return parent != null;
    }
    
    private static class PrettyJsonWriter extends StringWriter {
        private int indent = 0;

        @Override
        public void write(int c) {
            if (((char) c) == '[' || ((char) c) == '{') {
                super.write(c);
                super.write('\n');
                indent++;
                writeIndentation();
            } else if (((char) c) == ',') {
                super.write(c);
                super.write('\n');
                writeIndentation();
            } else if (((char) c) == ']' || ((char) c) == '}') {
                super.write('\n');
                indent--;
                writeIndentation();
                super.write(c);
            } else {
                super.write(c);
            }
        }

        private void writeIndentation() {
            for (int i = 0; i < indent; i++) {
                super.write("   ");
            }
        }
    }
}
