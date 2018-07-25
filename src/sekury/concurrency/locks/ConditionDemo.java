package sekury.concurrency.locks;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConditionDemo {

    static class SharedData {
        private int value;
        private final Lock lock;
        private final Condition condition;

        public SharedData() {
            this.lock = new ReentrantLock();
            this.condition = lock.newCondition();
        }
    }

    public static void main(String[] args) {

    }
}
