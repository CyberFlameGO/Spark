/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.spark.misc.utils;

import codes.goblom.spark.internals.Validater;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Goblom
 */
public class Utils {

    private Utils() { }
    
    public static final double PERFECT_TPS = 20.0000D;
    public static final Random RANDOM = new Random();
    
    private static final List<Validater> VALID_CHECK =  Lists.newLinkedList();
    
    static {
        VALID_CHECK.add((Validater<String>) (String obj) -> obj.equals(""));
        VALID_CHECK.add((Validater<String[]>) (String[] obj) -> obj.length != 0);
        VALID_CHECK.add((Validater<Object[]>) (Object[] obj) -> obj.length != 0);
    }
    
    public static String getFormattedTime(int time) {
        if (time < 60) {
            return new StringBuilder("0:" + (time < 10 ? "0" : "")).append(time).toString();
        }
        
        int minutes = time / 60;
        int seconds = time % 60;
        
        return new StringBuilder(String.valueOf(minutes)).append(":").append(
                (seconds < 10 ? 
                        new StringBuilder("0").append(seconds).toString() : 
                        seconds))
            .toString();
    }
    
    public static String mergeArgs(String[] args, int start) {
        StringBuilder builder = new StringBuilder();
        for (int i = start; i < args.length; i ++) {
            if (i != start) builder.append(" ");
            builder.append(args[i]);
        }
        return builder.toString();
    }
    
    // TODO: Update
    @Deprecated
    public static int roundInventorySize(final int size) {
        if (size > 9 && size <= 18) {
            return 18;
        } else if (size > 18 && size <= 27) {
            return 27;
        } else if (size > 27 && size <= 36) {
            return 36;
        } else if (size > 36 && size <= 45) {
            return 45;
        } else if (size > 45) {
            return 54;
        }

        return 9;
    }
    
    public static void addValidaterCheck(Validater v) {
        VALID_CHECK.add(v);
    }
    
    public static boolean isValid(Object o) {
        boolean is = (o == null);
        Iterator<Validater> it = VALID_CHECK.iterator();
        
        while (!is && it.hasNext()) {
            Validater v = it.next();
            
            try {
                is = v.validate(o);
            } catch (Exception e) { }
        }
        
        return is;
    }
    
    public static String addCommas(final Object obj) {
        final StringBuilder sb = new StringBuilder();
        final String str = obj.toString();
        
        for (int len = str.length(), i = 0; i < len; ++i) {
            if (len % 3 == i % 3 && i != 0) {
                sb.append(",");
            }
            
            sb.append(str.charAt(i));
        }
        
        return sb.toString();
    }
}
