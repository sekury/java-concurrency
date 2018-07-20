package sekury.concurrency.synchronizers;

import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

public class PhaserDemo {
    public static void main(String[] args) {
        Phaser phaser = new Phaser(1);  // register self

        for (int i = 0; i < 10; i++) {

            phaser.register();

            Runnable worker = () -> {
                System.out.println(Thread.currentThread().getName() + ": Works.");
                try {
                    TimeUnit.MILLISECONDS.sleep((long) (Math.random() * 1000));
                    int phase = phaser.arriveAndAwaitAdvance();
                    System.out.println(Thread.currentThread().getName() + ": Done in phase=" + phase);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            };

            Thread thread = new Thread(worker);
            thread.start();
        }

        phaser.arriveAndDeregister();           // deregister self
    }
}
