package sekury.concurrency.locks;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

public class ConditionDemo {

    static class SharedData<T> {

        private volatile boolean available;
        private T data;
        private final Lock lock;
        private final Condition condition;

        public SharedData() {
            this.lock = new ReentrantLock();
            this.condition = lock.newCondition();
        }

        public T getData() {
            lock.lock();
            try {
                while (!available) {
                    condition.await();
                }
                available = false;
                condition.signal();
            } finally {
                lock.unlock();
                return data;
            }
        }

        public void setData(T data) {
            lock.lock();
            try {
                while (available) {
                    condition.await();
                }
                this.data = data;
                available = true;
                condition.signal();
            } finally {
                lock.unlock();
                return;
            }
        }
    }

    public static void main(String[] args) {
        SharedData<Integer> sharedData = new SharedData<>();
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.execute(() -> {
            int data = sharedData.getData();
            System.out.println(data);
            while (data != 10) {
                data = sharedData.getData();
                System.out.println(data);
            }
        });
        executor.execute(() -> IntStream.range(1, 11).forEach(data -> {
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sharedData.setData(data);
        }));
        executor.shutdown();
    }
}
