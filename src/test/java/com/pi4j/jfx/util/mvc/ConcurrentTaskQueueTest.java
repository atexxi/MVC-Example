package com.pi4j.jfx.util.mvc;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class ConcurrentTaskQueueTest {

    @Test
    void testSequenceGuarantees() throws InterruptedException {
        // given
        final ConcurrentTaskQueue<Integer>   taskQueue = new ConcurrentTaskQueue<>();
        final ConcurrentLinkedQueue<Integer> collector = new ConcurrentLinkedQueue<>();

        // when we concurrently produce and consume some numbers
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            taskQueue.submit(
                () -> {
                    try {
                        Thread.sleep(10); // force some thread switching to make the test more realistic
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return finalI;
                },
                collector::add
            );
        }

        // special in the test case: wait until all concurrent tasks are finished
        // such that we can synchronously assert the outcome.
        // The general idea is to submit a last task to the CTQ that concurrently sets a state that we can
        // synchronously wait for. To that end, we create an extra executor, that we shut down in the task,
        // and wait for termination in the tests main thread.
        final ExecutorService waitForFinishedService = Executors.newFixedThreadPool(1);
        taskQueue.submit( () -> {
            waitForFinishedService.shutdown(); // would be nice if this could just be a method reference
            return null;
        });
        waitForFinishedService.awaitTermination(5, TimeUnit.SECONDS);

        // then no number is missing and the sequence is retained
        Integer[] expected = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        assertArrayEquals( expected, collector.toArray() );
    }



}
