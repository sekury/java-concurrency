package sekury.concurrency.locks;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockDemo {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        final ReentrantLock lock = new ReentrantLock();

        Runnable runnable = () -> {
            String name = Thread.currentThread().getName();
            lock.lock();
            try {
                if (lock.isHeldByCurrentThread()) {
                    System.out.printf("Thread %s entered critical section.%n", name);
                    try {
                        TimeUnit.MILLISECONDS.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.printf("Thread %s exiting critical section.%n", name);
                }
            } finally {
                lock.unlock();
            }
        };

        executor.execute(runnable);
        executor.execute(runnable);
        executor.shutdown();
    }
}
