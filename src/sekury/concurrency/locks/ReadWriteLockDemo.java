package sekury.concurrency.locks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLockDemo {

    public static void main(String[] args) {
        final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
        final ReentrantReadWriteLock.ReadLock readLock = readWriteLock.readLock();
        final ReentrantReadWriteLock.WriteLock writeLock = readWriteLock.writeLock();
        List<Integer> sharedList = new ArrayList<>();

        new Thread(() -> {
            for (var i = 0; i < 10; i++) {
                writeLock.lock();
                try {
                    sharedList.add(i);
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } finally {
                    writeLock.unlock();
                }
            }
        }).start();

        new Thread(() -> {
            while (true) {
                readLock.lock();
                try {
                    System.out.println(sharedList);
                    if (sharedList.size() >= 9) {
                        break;
                    }
                } finally {
                    readLock.unlock();
                }
            }
        }).start();
    }
}
