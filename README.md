# Java Concurrency Gists

## Essentials
### Thread

Parallelism is a condition when at least two threads are executing simultaneously.
Concurrency is a condition when at least two threads are making progress.
Concurrency can include time-slicing as a form of virtual parallelism (equal priority threads are given equals
slices of time).

The operating system uses scheduler to determine when a waiting thread executes.
Schedulers can handle priority change in different ways: a scheduler might delay lower priority threads until higher priority threads finish.
This delaying can lead to indefinitely postponement and starvation because lower priority threads are waiting
for their turn to execute.

Daemon thread dies automatically when the last nondaemon thread dies.
Application will not terminate until all nondaemon threads terminate.
All threads are nondaemon by default.

Calling `start()` results scheduling thread execution in which `run()` method is invoked.
Calling `start()` again for the same thread causes `IllegalStateException`.

InterruptedException is thrown when any thread has interrupted the current thread.
The interrupt mechanism is implemented using an internal flag known as the interrupt status.
If this exception is thrown, the interrupted status is cleared.

When a thread checks for an interrupt by invoking the static method `Thread.interrupted`, interrupt status is cleared.

`InterruptedException` is thrown when any thread has interrupted the current thread.
If this exception is thrown, the interrupted status is cleared.

`isInterrupted` method is used by one thread to query the interrupt status of another, does not change the interrupt status flag.

```java
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
```

### Thread Syncronization

A race condition occurs when the correctness of a computation depends on the relative timing or interleaving of
multiple threads by the scheduler. A race condition is often confused with a data race in which two or more
threads access the same memory location concurrently, at least one of the accesses is for writing, and these
threads don't coordinate their access to that memory.

Synchronization ensures that concurrent threads don't simultaneously execute a critical section, which must be
accessed in a serial manner (this property of synchronization is known as mutual exclusion).

Each thread has its own copy of a shared variable. Synchronization exhibits the property of visibility in which
it ensures that a thread always reads these variables from the main memory on entry to critical section and
writes their values to main memory on exit.

Each Java objects is associated with a monitor, which a thread can lock (by acquiring monitor's lock) or unlock
(by releasing monitor's lock). Only one thread can hold a lock. Any other thread trying to lock that monitor
blocks until it can obtain the lock. When thread exits a critical section, it unlocks the monitor.

```java
public class JavaThreadSyncDemo {

    public static class Counter {
        private static int counter;    // 0 by default
        // When synchronizing on an instance method, the lock is associated with the object, on which the method is
        // called.
        public synchronized int getNext() {
            return ++counter;
        }
    }

    public static class StaticCounter {
        private static int counter;    // 0 by default
        // When synchronizing on a class method, the lock is associated with the java.lang.Class object.
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
```

[Thread Dead Lock Example](https://gist.github.com/sekury/59a1552026799b96a94c)

## Synchronizers

[CountDownLatch](https://gist.github.com/sekury/ae435095eb749fcf11d50850d1154405)

[CyclicBarrier](https://gist.github.com/sekury/4ba1b622afb1582750432396f3d61d6f)
