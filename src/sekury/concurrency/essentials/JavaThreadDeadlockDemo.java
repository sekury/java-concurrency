package sekury.concurrency.essentials;

public class JavaThreadDeadlockDemo {

    private final Object lock1 = new Object();
    private final Object lock2 = new Object();

    public void instanceMethod1() {
        System.out.println("first holds lock2: " + Thread.holdsLock(lock2));
        synchronized (lock1) {
            System.out.println("first thread is trying get lock2");
            synchronized (lock2) {
                System.out.println("first thread in instanceMethod1");
            }
        }
    }

    public void instanceMethod2() {
        System.out.println("second thread is trying get lock2");
        synchronized (lock2) {
            System.out.println("second thread is trying get lock1");
            synchronized (lock1) {
                System.out.println("second thread in instanceMethod2");
            }
        }
    }

    public static void main(String[] args) {
        final JavaThreadDeadlockDemo deadlockDemo = new JavaThreadDeadlockDemo();
        Thread first = new Thread(() -> {
            while (true) {
                deadlockDemo.instanceMethod1();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {}
            }
        });
        Thread second = new Thread(() -> {
            while (true) {
                deadlockDemo.instanceMethod2();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {}
            }
        });
        first.start();
        second.start();
    }
}
