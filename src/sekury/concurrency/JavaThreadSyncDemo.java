package sekury.concurrency;

public class JavaThreadSyncDemo {

    public static class Counter {
        private static int counter;
        public synchronized int getNext() {
            return ++counter;
        }
    }

    public static class StaticCounter {
        private static int counter;
        public static synchronized int getNext() {
            return ++counter;
        }
    }

    private static int result;
    private static final Object o = new Object();

    public static void main(String[] args) {
        Counter c = new Counter();
        System.out.println(c.getNext());
        System.out.println(StaticCounter.getNext());
        Thread t = new Thread(() -> {
            synchronized (o) {
                result++;
            }
        });
        t.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {}
        synchronized (o) {
            System.out.println(result);
        }
    }
}
