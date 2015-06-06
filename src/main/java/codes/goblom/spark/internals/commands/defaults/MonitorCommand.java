/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.spark.internals.commands.defaults;

import codes.goblom.spark.internals.ExecutorArgs;
import codes.goblom.spark.internals.commands.SparkCommand;
import codes.goblom.spark.internals.monitor.Monitor;
import codes.goblom.spark.internals.monitor.Monitors;
import codes.goblom.spark.misc.utils.PlayerUtils;
import java.util.Iterator;
import lombok.Getter;

/**
 *
 * @author Goblom
 */
public final class MonitorCommand implements SparkCommand {

    @Getter
    private final String[] aliases = { "monitor", "monitors", "lm", "m" };

    @Getter
    private final String description = "Display all currently loaded monitors";

    @Override
    public Boolean execute(ExecutorArgs args) throws Throwable {
        Iterator<Monitor> it = Monitors.iterator();
        StringBuilder sb = new StringBuilder();
        int size = 0;
        
        while (it.hasNext()) {
            size++;
            Monitor monitor = it.next();
            
            if (sb.length() > 0) {
                sb.append(", ");
            }
            
            sb.append(monitor.getClass().getSimpleName());
        }
        
        PlayerUtils.sendMessage(getSender(args), "Monitors (%s): %s", size, sb.toString());
        return true;
    }

}
