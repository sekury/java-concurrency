package sekury.concurrency.synchronizers;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

public class CyclicBarrierDemo {

    public static void main(String[] args) {
        final int parties = 5;
        CyclicBarrier barrier = new CyclicBarrier(parties,
                () -> System.out.println(Thread.currentThread().getName() + ": All threads are done!"));

        Runnable worker = () -> {
            System.out.println(Thread.currentThread().getName() + ": Works.");
            try {
                TimeUnit.MILLISECONDS.sleep((long) (Math.random() * 1000));
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + ": Done.");
        };

        for (int n = 0; n < 2; n++) {
            Thread[] threads = new Thread[parties];
            for (int i = 0; i < parties; i++) {
                threads[i] = new Thread(worker);
            }
            for (Thread thread : threads) {
                thread.start();
            }
        }
    }
}
