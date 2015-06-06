/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.spark.misc.tools;

import codes.goblom.spark.Log;
import codes.goblom.spark.SparkPlugin;
import codes.goblom.spark.internals.Callback;
import codes.goblom.spark.internals.task.ThreadTask;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import lombok.Getter;

/**
 *
 * @author Goblom
 */

// TODO: Make pretty like BukkitDevUpdater
// TODO: Support downloading of resource
public abstract class SpigotUpdater implements Callback<String> {
    
    public static String API_KEY = "98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4"; // Plz use your own.
    private static String QUERY = "http://www.spigotmc.org/api/general.php";
    private static String DATA = "key=%s&resource=%s";
    
    @Getter
    private final int resourceId;
    
    @Getter
    private final String currentVersion;
    private final String postData;
    
    private HttpURLConnection connection;
    
    public SpigotUpdater(SparkPlugin plugin, int resourceId) {
        this.resourceId = resourceId;
        this.currentVersion = plugin.getDescription().getVersion();
        this.postData = String.format(DATA, API_KEY, resourceId);
        
        ThreadTask urlTask = new ThreadTask<HttpURLConnection>((HttpURLConnection object, Throwable error) -> {
            if (error != null) {
                Log.find(plugin).severe("Unable to contact spigot in order to run update check", error.getMessage());
                
                return;
            }
            
            this.connection = object;
        }) {
            @Override
            public HttpURLConnection execute() throws Throwable {
                return (HttpURLConnection) new URL(QUERY).openConnection();
            }
        };
        
        urlTask.start();
        
        new ThreadTask<String>(this) {
            @Override
            public String execute() throws Throwable {
                try {
                    urlTask.join();
                } catch (Exception e) { }
                
                if (connection == null) {
                    return "CONNECTION_WAS_NULL";
                }
                
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.getOutputStream().write(postData.getBytes("UTF-8"));
                
                return new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
            }
        }.start();
    }
    
    @Override
    public abstract void onFinish(String webVersion, Throwable error);
    
    public static SpigotUpdater check(SparkPlugin plugin, int id, Callback<String> callback) {
        return new SpigotUpdater(plugin, id) {

            @Override
            public void onFinish(String version, Throwable error) {
                callback.onFinish(version, error);
            }
        };
    }
    
//    public static void main(String[] args) {
//        SpigotUpdater su = new SpigotUpdater(5935) { // Some random project not associated with Goblom
//            
//            @Override
//            public void onFinish(String latestVersion, Throwable error) {
//                if (error != null) {
//                    error.printStackTrace();
//                    
//                    return;
//                }
//                
//                System.out.println("[Found] " + latestVersion);
//            }
//        };
//    }
}
