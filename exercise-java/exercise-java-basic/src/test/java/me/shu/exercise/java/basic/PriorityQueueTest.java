package me.shu.exercise.java.basic;


import org.junit.Test;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class PriorityQueueTest {

    @Test
    public void basicTest() {

        PriorityQueue<Integer> queue = new PriorityQueue<Integer>(10, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1 - o2;
            }
        });

        for (int i = 0; i < 10; i++) {
            queue.add(i);
        }

        while (!queue.isEmpty()) {
            System.out.println(queue.poll());
        }
    }
}
