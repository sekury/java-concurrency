package sekury.concurrency.synchronizers;

import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class SemaphoreDemo {

    static class SharedDataPool {

        private static final int MAX_PERMITS = 5;

        private Semaphore semaphore = new Semaphore(MAX_PERMITS, false);
        private String[] items = new String[MAX_PERMITS];
        private boolean[] usedItems = new boolean[MAX_PERMITS];

        {
            for (int i = 0; i < MAX_PERMITS; i++) {
                items[i] = "Item" + i;
            }
        }

        public void putItem(String item) {
            boolean shouldRelease = false;
            synchronized (this) {
                for (int i = 0; i < MAX_PERMITS; i++) {
                    if (item == items[i]) {
                        if (usedItems[i]) {
                            usedItems[i] = false;
                            shouldRelease = true;
                        }
                    }
                }
            }
            if (shouldRelease) {
                semaphore.release();
            }
        }

        public String getItem() throws InterruptedException {
            semaphore.acquire();
            synchronized (this) {
                for (int i = 0; i < MAX_PERMITS; i++) {
                    if (!usedItems[i]) {
                        usedItems[i] = true;
                        return items[i];
                    }
                }
                return null;
            }
        }
    }

    public static void main(String[] args) {
        SharedDataPool sharedDataPool = new SharedDataPool();
        Runnable runnable = () -> {
            try {
                String threadName = Thread.currentThread().getName();
                System.out.printf("Thread %s trying get item...%n", threadName);
                String item = sharedDataPool.getItem();
                System.out.printf("Thread %s got item %s%n", threadName, item);
                TimeUnit.MILLISECONDS.sleep(new Random().nextInt(2000));
                System.out.printf("Thread %s putting item back %s%n", threadName, item);
                sharedDataPool.putItem(item);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(runnable);
            thread.start();
        }
    }
}
