/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package testing;

import codes.goblom.spark.internals.ExecutorArgs;
import codes.goblom.spark.internals.commands.SparkCommand;
import lombok.Getter;

/**
 *
 * @author Goblom
 */
public class TestCommand implements SparkCommand {

    @Getter
    String[] aliases = { "test", "moar" };
    
    @Getter
    String description = "Spark test command";

    @Getter
    boolean mainCommand = true;
    
    @Override
    public Boolean execute(ExecutorArgs exe) throws Throwable {
        getSender(exe).sendMessage("It Works!");
        
        String[] args = getArgs(exe);
        
        for (int i = 0; i < args.length; i++) {
            getSender(exe).sendMessage(String.format("[%s] -> %s", i, args[i]));
        }
        
        return true;
    }
    
}
