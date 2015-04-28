/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.core.internals.monitor.types;

import codes.goblom.core.internals.monitor.Monitor;
import com.google.common.collect.Lists;
import java.text.DecimalFormat;
import java.util.LinkedList;

/**
 *
 * @author Goblom
 */
public class LagMonitor extends Monitor {

    @Monitor.TickInterval private static final long TICK_INTERVAL = 50L;
    @Monitor.TickDelay private static final long TICK_DELAY = 1000L;
    private static final DecimalFormat format = new DecimalFormat("##.##");
    
    private long lastPoll = System.nanoTime();
    private final LinkedList<Double> history = Lists.<Double>newLinkedList();
    
    LagMonitor() {
        super();
        
        history.add(20.0);
    }
    
    public double getAverage() {
        return Double.parseDouble(format.format(calculateAverage()));
    }
    
    private double calculateAverage() {
        double avg = 0.0;
               avg = this.history.stream().map((tps) -> tps).reduce(avg, (accumulator, _item) -> accumulator + _item);
        
        return avg / this.history.size();
    }
    
    @Override
    public void update() {
        final long startTime = System.nanoTime();
        long timeSpent = (startTime - this.lastPoll) / TICK_DELAY;
             timeSpent = timeSpent == 0L ? 1L : timeSpent;
        
        if (this.history.size() > 10) {
            this.history.remove();
        }
        
        final double tps = 5.0E7 / timeSpent;
        
        if (tps <= 21.0) {
            this.history.add(tps);
        }
        
        this.lastPoll = startTime;
    }
    
}
