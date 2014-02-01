package com.vbersh;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * http://www.javaspecialists.eu/archive/Issue125.html
 * http://code.google.com/p/guava-libraries/wiki/CachesExplained
 * http://code.google.com/p/concurrentlinkedhashmap/wiki/ExpirableCache
 */
public class ExpirableCache<K, V> {

    private static final int TIME_TO_LIVE = 15 * 60 * 1000;
    private static final int MAXIMUM_CAPACITY = 1000;

    private ConcurrentMap<K, CacheEntry<V>> cache = new ConcurrentLinkedHashMap.Builder<K, CacheEntry<V>>()
            .maximumWeightedCapacity(MAXIMUM_CAPACITY)
            .build();

    public V get(final K key, Callable<V> eval) {
        for (;;) {
            CacheEntry<V> existing = cache.get(key);
            if (existing != null && existing.hasExpired()) {
                cache.remove(key, existing);
                existing = null;
            }

            if(existing == null) {
                CacheEntry<V> expirable = new CacheEntry<V>(eval, TIME_TO_LIVE);

                existing = cache.putIfAbsent(key, expirable);
                if (existing == null) {
                    existing = expirable;
                    existing.run();
                }
            }

            try {
                return existing.get();
            } catch (CancellationException e) {
                cache.remove(key, existing);
            } catch (ExecutionException e) {
                // Kabutz: this is my addition to the code...
                try {
                    cache.remove(key, existing);
                    throw e.getCause();
                } catch (RuntimeException ex) {
                    throw ex;
                } catch (Error ex) {
                    throw ex;
                } catch (Throwable t) {
                    throw new IllegalStateException("Not unchecked", t);
                }
            } catch (InterruptedException e) {
                cache.remove(key, existing);
                existing.cancel(true);
            }
        }
    }

    private static class CacheEntry<V> extends FutureTask<V> {
        private long expiration;

        CacheEntry(Callable<V> callable, long ttl) {
            super(callable);

            this.expiration = System.currentTimeMillis() + ttl;
        }

        public boolean hasExpired() {
            return System.currentTimeMillis() > expiration;
        }
    }

    public static void main(String... args) {
        ExpirableCache<String, String> cache = new ExpirableCache<String, String>();

        cache.get("foo", new Callable<String>() {
            @Override
            public String call() {
                return "bar";
            }
        });
    }

}

