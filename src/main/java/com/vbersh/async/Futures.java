package com.vbersh.async;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Futures {

    public static <V> V get(Future<V> future) {
        try{
            return future.get();
        } catch(InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
