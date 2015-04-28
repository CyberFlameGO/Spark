/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.core.internals.monitor;

import codes.goblom.core.internals.policy.LoadPolicy;
import codes.goblom.core.GoPlugin;
import codes.goblom.core.Log;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.reflections.Reflections;

/**
 *
 * @author Goblom
 */
public class Monitors /*implements Iterable<Monitor>*/ {

    private static final Reflections reflections = new Reflections("codes.goblom.core.internals.monitors.types");

    private static final Map<Class<? extends Monitor>, LoadPolicy> POLICIES = Maps.newConcurrentMap();
    private static final Set<Class<? extends Monitor>> MONITOR_CLASSES = Sets.newHashSet();
    private static final Map<Class<? extends Monitor>, Monitor> MONITORS = Maps.newConcurrentMap();

    private Monitors() { }
    
    // *****************************
    // Add static Iterable Methods
    // *****************************
    public static Iterator<Monitor> iterator() {
        return MONITORS.values().iterator();
    }
    
    public static void forEach(Consumer<? super Monitor> action) {
        Objects.requireNonNull(action);
//        iterator().forEachRemaining(action);
//
//
//        Iterator<Monitor> it = iterator();
//        
//        while (it.hasNext()) {
//            Monitor monitor = it.next();
//            
//            action.accept(monitor);
//        }
        
        
        MONITORS.values().stream().forEach((monitor) -> {
            action.accept(monitor);
        });
    }
    
    public static Spliterator<Monitor> spliterator() {
        return Spliterators.spliteratorUnknownSize(iterator(), 0);
    }
    
    // *****************************
    // End static Iterable Methods
    // *****************************
    
    public static void addPolicy(@NonNull LoadPolicy<Monitor> policy) {
        if (policy.isValid()) {
            POLICIES.put(policy.forClass, policy);
        } else {
            Log.warning("Monitor Policy for %s is invalid.", policy.forClass.getSimpleName());
        }
    }

    public static void addMonitor(Class<? extends Monitor> monitor) {
        if (!MONITOR_CLASSES.contains(monitor)) {
            MONITOR_CLASSES.add(monitor);
        }
    }

    private static boolean hasPolicy(Class<? extends Monitor> clazz) {
        return POLICIES.containsKey(clazz);
    }

    public static <M extends Monitor> M loadNoStore(Class<M> clazz) {
        return loadNoStore(clazz, null);
    }
    
    public static <M extends Monitor> M loadNoStore(Class<M> clazz, LoadPolicy<M> policy) {
        M monitor = null;

        try {
            final Monitor m;

            if (policy != null) {
                Constructor<? extends Monitor> c = clazz.getConstructor(policy.constructor);
                                               c.setAccessible(true);

                m = c.newInstance(policy.values);
            } else {
                if (clazz.getConstructors().length != 0) {
                    Constructor c = clazz.getConstructors()[0];
                                c.setAccessible(true);

                    m = (Monitor) c.newInstance();
                } else {
                    m = clazz.newInstance();
                }
            }

            // Start the running task.
            long tickInterval = 0L;
            long tickDelay = 0L;
            boolean async = clazz.isAnnotationPresent(Monitor.Async.class);

            for (Field f : clazz.getDeclaredFields()) {
                if (Modifier.isStatic(f.getModifiers())) {
                    if (f.getType() == long.class || f.getType() == Long.class/* || f.getType() == int.class || f.getType() == Integer.class*/) {
                        if (f.isAnnotationPresent(Monitor.TickInterval.class)) {
                            tickInterval = (Long) f.get(null);
                        }

                        if (f.isAnnotationPresent(Monitor.TickDelay.class)) {
                            tickDelay = (Long) f.get(null);
                        }
                    } else {
                        Log.debug("Found static field[%s] with type of [%s] for Monitor [%s]", f.getName(), f.getType().getSimpleName(), monitor.getName());
                    }
                }
            }

            final Runnable r = () -> { m.update(); };

            if (async) {
                m.runner = Bukkit.getScheduler().runTaskTimerAsynchronously(GoPlugin.getInstance(), r, tickDelay, tickInterval);
            } else {
                m.runner = Bukkit.getScheduler().runTaskTimer(GoPlugin.getInstance(), r, tickDelay, tickInterval);
            }

            monitor = (M) m;

            if (monitor instanceof Listener) {
                GoPlugin.getInstance().register((Listener) monitor);
            }
        } catch (Throwable t) {
            Log.severe("Unable to load Monitor[%s]. Error: %s", clazz.getSimpleName(), t.getMessage());
        }
        
        return monitor;
    }
    
    public static <M extends Monitor> M load(Class<M> clazz) {
        M monitor = null;
        if (MONITORS.containsKey(clazz)) {
            monitor = (M) MONITORS.get(clazz);
        }

        if (monitor == null) {
            try {
                LoadPolicy policy = hasPolicy(clazz) ? POLICIES.get(clazz) : null;
                final Monitor m;
                
                if (policy != null) {
                    Constructor<? extends Monitor> c = clazz.getConstructor(policy.constructor);
                                                   c.setAccessible(true);

                    m = c.newInstance(policy.values);
                } else {
                    if (clazz.getConstructors().length != 0) {
                        Constructor c = clazz.getConstructors()[0];
                                    c.setAccessible(true);
                                    
                        m = (Monitor) c.newInstance();
                    } else {
                        m = clazz.newInstance();
                    }
                }
                
                // Start the running task.
                long tickInterval = 0L;
                long tickDelay = 0L;
                boolean async = clazz.isAnnotationPresent(Monitor.Async.class);
                
                for (Field f : clazz.getDeclaredFields()) {
                    if (Modifier.isStatic(f.getModifiers())) {
                        if (f.getType() == long.class || f.getType() == Long.class/* || f.getType() == int.class || f.getType() == Integer.class*/) {
                            if (f.isAnnotationPresent(Monitor.TickInterval.class)) {
                                tickInterval = (Long) f.get(null);
                            }
                            
                            if (f.isAnnotationPresent(Monitor.TickDelay.class)) {
                                tickDelay = (Long) f.get(null);
                            }
                        } else {
                            Log.debug("Found static field[%s] with type of [%s] for Monitor [%s]", f.getName(), f.getType().getSimpleName(), monitor.getName());
                        }
                    }
                }
                
                final Runnable r = () -> { m.update(); };
                
                if (async) {
                    m.runner = Bukkit.getScheduler().runTaskTimerAsynchronously(GoPlugin.getInstance(), r, tickDelay, tickInterval);
                } else {
                    m.runner = Bukkit.getScheduler().runTaskTimer(GoPlugin.getInstance(), r, tickDelay, tickInterval);
                }
                
                monitor = (M) m;
                
                if (monitor instanceof Listener) {
                    GoPlugin.getInstance().register((Listener) monitor);
                }
                
                //Store the Monitor
                MONITORS.put(clazz, monitor);
            } catch (Throwable t) {
                Log.severe("Unable to load Monitor[%s]. Error: %s", clazz.getSimpleName(), t.getMessage());
            }
        }
        
        return monitor;
    }

    public static void loadAll() {
        Set<Class<? extends Monitor>> classes = reflections.getSubTypesOf(Monitor.class);
                                      classes.forEach((monitor) -> { addMonitor(monitor); });
                                      classes = MONITOR_CLASSES;

        for (Class<? extends Monitor> clazz : classes) {
            load(clazz);
        }
    }
//
//    @Override
//    public Iterator<Monitor> iterator() {
//        return MONITORS.values().iterator();
//    }
}
