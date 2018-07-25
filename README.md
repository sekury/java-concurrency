# Java Concurrency Tips

## TOC

* [Essentials](#essentials)
    * [Thread](#thread)
    * [Thread Syncronization](#thread-syncronization)
    * [Thread Dead Lock Example](#thread-dead-lock-example)
* [Executors](#executors)
* [Synchronizers](#synchronizers)
    * [CountDownLatch](#countdownlatch)
    * [CyclicBarrier](#cyclicbarrier)
    * [Exchanger](#exchanger)
    * [Semaphore](#semaphore)
    * [Phaser](#phaser)

## Essentials

### Thread

Parallelism is a condition when at least two threads are executing simultaneously.
Concurrency is a condition when at least two threads are making progress.
Concurrency can include time-slicing as a form of virtual parallelism (equal priority threads are given equals
slices of time).

The operating system uses scheduler to determine when a waiting thread executes.
Schedulers can handle priority change in different ways: a scheduler might delay lower priority threads until higher 
priority threads finish.
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

`isInterrupted` method is used by one thread to query the interrupt status of another, does not change the interrupt 
status flag.

[Example](/src/sekury/concurrency/essentials/JavaThreadDemo.java)

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

[Example](/src/sekury/concurrency/essentials/JavaThreadSyncDemo.java)

### Thread Dead Lock Example

A liveness failure occurs when an application reaches a state in which it can make no further progress. In a single-
threaded application, infinite loop would be an example. In a multithreaded application are deadlock, livelock, and
starvation.

Deadlock: Thread x waits for a resource that thread y is holding exclusively and thread y is waiting for a resource
that thread x is holding exclusively. Neither thread can make progress.

Livelock: Thread x keeps retrying an operation that will always fail. It cannot make progress for this reason.

Starvation: Thread x is continually denied (by the scheduler) access to a needed resource in order to make progress 
(indefinite postponement).

[Example](/src/sekury/concurrency/essentials/JavaThreadDeadlockDemo.java)

## Executors

`Executor` interface decouples task submission from task execution mechanics.
`Executor` focuses exclusively on `Runnable`.

`ExecutorService` interface submits a `Callable` for execution and return `Future` interface that represents the 
result of async computation. `ExecutorService` provides methods to execute a collection of tasks and initiate a 
shutdown.

[Example](/src/sekury/concurrency/executors/ExecutorDemo.java)

## Synchronizers

### CountDownLatch

CountDownLatch causes one or more threads to wait at a gate until another thread opens this gate. 

[Example](/src/sekury/concurrency/synchronizers/CountDownLatchDemo.java)

### CyclicBarrier

CyclicBarrier lets a set of threads wait for each other to reach a common barrier point.

[Example](/src/sekury/concurrency/synchronizers/CyclicBarrierDemo.java)

### Exchanger

Exchanger provides a synchronization point where threads can swap objects.

[Example](/src/sekury/concurrency/synchronizers/ExchangerDemo.java)

### Semaphore

Semaphore restricts the number of threads that can access resource.
A thread attempting to acquire a semaphore permit.
When no permits are available thread blocks until some other thread releases a permit.

[Example](/src/sekury/concurrency/synchronizers/SemaphoreDemo.java)

### Phaser

Phaser coordinates a variable number of threads, which can register at any time.
Phaser lets a group of threads wait on a barrier.

[Example](/src/sekury/concurrency/synchronizers/PhaserDemo.java)