package sekury.concurrency.synchronizers;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CountDownLatchDemo {

    public static void main(String[] args) {
        final int numberOfThreads = 3;
        final CountDownLatch startSignal = new CountDownLatch(1);
        final CountDownLatch doneSignal = new CountDownLatch(numberOfThreads);

        Runnable runnable = () -> {
            try {
                printThreadInfo("entered");
                startSignal.await();
                printThreadInfo("in progress...");
                TimeUnit.MILLISECONDS.sleep((long) (Math.random() * 1000));
                printThreadInfo("done");
                doneSignal.countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        for (int i = 0; i < numberOfThreads; i++) {
            executor.execute(runnable);
        }

        try {
            printThreadInfo("main thread");
            TimeUnit.SECONDS.sleep(1);
            startSignal.countDown();
            printThreadInfo("signal start");
            doneSignal.await();
            executor.shutdown();
            printThreadInfo("all threads done");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    static void printThreadInfo(String msg) {
        System.out.println(System.currentTimeMillis() + ": " + Thread.currentThread() + ": " + msg);
    }
}
