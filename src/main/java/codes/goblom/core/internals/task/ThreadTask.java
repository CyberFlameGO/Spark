/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.core.internals.task;

import codes.goblom.core.internals.Callback;
import codes.goblom.core.internals.ExecutorArgs;
import codes.goblom.core.internals.ExecutorNoArgs;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author Goblom
 */
public abstract class ThreadTask<T> implements ExecutorNoArgs<T, Throwable> {
    
    private Callback<T> callback = null;
    
    @Getter
    private Thread thread;
            
    public ThreadTask() {
        this(null);
    }
    
    public ThreadTask(Callback<T> callback) {
        this.callback = callback;
        this.thread = new Caller<>(this);
    }
    
    @Override
    public final T execute(ExecutorArgs args) throws Throwable {
        return execute();
    }
    
    public void start() {
        thread.start();
    }

    public void interrupt() {
        thread.interrupt();
    }
    
    public void join() throws InterruptedException {
        thread.join();
    }
    
    public void join(long millis) throws InterruptedException {
        thread.join(millis);
    }
    
    public void join(long millis, int nano) throws InterruptedException {
        thread.join(millis, nano);
    }
       
    @RequiredArgsConstructor
    private static final class Caller<T> extends Thread {
        private final ThreadTask<T> task;
        
        @Override
        public void run() {
            Throwable thrown = null;
            T obj = null;

            try {
                obj = task.execute();
            } catch (Throwable t) {
                thrown = t;
            }

            if (task.callback != null) {
                task.callback.onFinish(obj, thrown);
            }

            if (thrown != null) {
                thrown.printStackTrace();
            }
        }
    }
    
//    public static void main(String[] args) {
//        ThreadTask<String> tt = new ThreadTask<String>((String object, Throwable error) -> {
//            System.out.println(object);
//        }) {
//            @Override
//            public String execute() throws Throwable {
//                return "Test ThreadTask";
//            }
//        };
//        
//        tt.start();
//    }
}
