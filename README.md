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

When synchronizing on an instance method, the lock is associated with the object, on which the method is called.

When synchronizing on a class method, the lock is associated with the java.lang.Class object.

```java
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
```

### Thread Dead Lock Example

A liveness failure occurs when an application reaches a state in which it can make no further progress. In a single-
threaded application, infinite loop would be an example. In a multithreaded application are deadlock, livelock, and
starvation.

Deadlock: Thread x waits for a resource that thread y is holding exclusively and thread y is waiting for a resource
that thread x is holding exclusively. Neither thread can make progress.

Livelock: Thread x keeps retrying an operation that will always fail. It cannot make progress for this reason.

Starvation: Thread x is continually denied (by the scheduler) access to a needed resource in order to make progress (indefinite postponement).

```java
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
```

## Synchronizers
### CountDownLatch

```java
import java.util.concurrent.*;

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
```

### CyclicBarrier

```java
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

public class CyclicBarrierDemo {

    public static void main(String[] args) {
        final int parties = 5;
        CyclicBarrier barrier = new CyclicBarrier(parties,
                () -> System.out.println(Thread.currentThread().getName() + ": All threads are done!"));

        Runnable worker = () -> {
            System.out.println(Thread.currentThread().getName() + ": Works.");
            try {
                TimeUnit.MILLISECONDS.sleep((long)(Math.random() * 1000));
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + ": Done.");
        };

        for (int n = 0; n < 2; n++) {
            Thread[] threads = new Thread[parties];
            for(int i = 0; i < parties; i++) {
                threads[i] = new Thread(worker);
            }
            for (Thread thread : threads) {
                thread.start();
            }
        }
    }
}
```

### Exchanger

```java
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class ExchangerDemo {

    static class Data {
        private final static int MAX = 10;
        private final List<Integer> items = new ArrayList<>();

        public void fill() {
            IntStream.range(0, 10).forEach(items::add);
        }

        synchronized void add(int value) {
            if (!isFull()) {
                items.add(value);
            }
        }

        synchronized Integer remove() {
            if (!isEmpty()) {
                return items.remove(0);
            }
            return null;
        }

        synchronized boolean isFull() {
            return items.size() == MAX;
        }

        synchronized boolean isEmpty() {
            return items.isEmpty();
        }

        @Override
        public String toString() {
            return "Data{" +
                    "items=" + items +
                    '}';
        }
    }

    public static void main(String[] args) {

        final Exchanger<Data> dataExchanger = new Exchanger<>();

        Runnable producer = () -> {
            Data data = new Data();
            int count = 0;
            try {
                while (true) {
                    System.out.println("Adding: " + count);
                    data.add(count++);
                    TimeUnit.MILLISECONDS.sleep(new Random().nextInt(1000));
                    if (data.isFull()) {
                        System.out.println("Producer waits for exchange: " + data);
                        data = dataExchanger.exchange(data);
                        System.out.println("Producer receives exchange: " + data);
                    }
                }
            } catch (InterruptedException e) {
                System.out.println("Producer interrupted");
            }
        };

        Runnable consumer = () -> {
            Data data = new Data();
            data.fill();
            try {
                while (true) {
                    System.out.println("Removing: " + data.remove());
                    TimeUnit.MILLISECONDS.sleep(new Random().nextInt(1000));
                    if (data.isEmpty()) {
                        System.out.println("Consumer waits for exchange: " + data);
                        data = dataExchanger.exchange(data);
                        System.out.println("Consumer receives exchange: " + data);
                    }
                }
            } catch (InterruptedException e) {
                System.out.println("Producer interrupted");
            }
        };

        new Thread(producer).start();
        new Thread(consumer).start();
    }
}
```
