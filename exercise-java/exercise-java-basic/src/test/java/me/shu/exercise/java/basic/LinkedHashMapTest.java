package me.shu.exercise.java.basic;


import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

public class LinkedHashMapTest {

    @Test
    public void basicTest() {

        final int cacheSize = 5;

        LinkedHashMap<Integer, Integer> lruCache = new LinkedHashMap<Integer, Integer>(10, 0.8f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) {
                return this.size() > cacheSize;
            }
        };


        for (int i = 0; i < 5; i++) {
            lruCache.put(i, i);
        }

        for (Map.Entry entry : lruCache.entrySet()) {
            System.out.println(entry.getValue());
        }

    }
}
