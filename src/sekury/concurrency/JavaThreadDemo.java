package sekury.concurrency;

public class JavaThreadDemo {

    public static void printThreadInfo(Thread t) {
        System.out.printf("Thread %s with id %d is in %s state, is%s alive, is %sdaemon%n",
                t.getName(),
                t.getId(),
                t.getState(),
                t.isAlive() ? "" : " not",
                t.isDaemon() ? "" : "non");
    }

    public static void main(String[] args) {
        System.out.println("BEGIN");
        Runtime runtime = Runtime.getRuntime();
        System.out.printf("Available processors: \t%d%n", runtime.availableProcessors());

        Runnable r1 = () -> {
            System.out.println("Hello from thread");
            Thread t = Thread.currentThread();
            printThreadInfo(t); // RUNNABLE state, alive: thread is started and running
        };
        Thread t1 = new Thread(r1);
        t1.setName("t1");
        t1.setPriority(Thread.MIN_PRIORITY);
        printThreadInfo(t1); // NEW state, not alive: thread has not yet started

        Thread t2 = new Thread(() -> {
            System.out.println("Hello from daemon thread");
            Thread t = Thread.currentThread();
            while (true) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // Should never arrive here because interrupt() is never called.
                }
                System.out.printf("Thread %s is in a busy-loop%n", t.getName());
            }
        }, "t2");
        t2.setDaemon(true);
        t2.setPriority(Thread.MAX_PRIORITY);

        Thread t3 = new Thread(() -> {
            Thread t = Thread.currentThread();
            while (true) {
                printThreadInfo(t);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    break;
                }
                if (Thread.interrupted()) {
                    System.out.printf("Thread %s is interrupted%n", t.getName());
                    break;
                }
            }
        }, "t3");

        t1.start();
        t2.start();
        t3.start();

        try {
            t1.join();  // Wait indefinitely for thread t1 to die.
        } catch (InterruptedException e) {
            /*
            Should never arrive here because interrupt() is never called.
             */
        }

        System.out.printf("Thread %s is%s interrupted%n", t3.getName(), t3.isInterrupted() ? "" : " not");
        t3.interrupt(); // Set interrupt status

        System.out.println("END");
    }
}
