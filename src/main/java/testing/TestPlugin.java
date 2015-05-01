/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package testing;

import codes.goblom.core.GoPlugin;
import codes.goblom.core.Log;

/**
 *
 * @author Goblom
 */
public class TestPlugin extends GoPlugin {

    @Override
    public void enable() {
        Log.info("We are Alive!");
    }
    
}
