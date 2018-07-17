package sekury.concurrency.synchronizers;

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
