package sekury.concurrency.executors;

import java.util.concurrent.*;

public class ExecutorDemo {

    static class DirectExecutor implements Executor {
        @Override
        public void execute(Runnable command) {
            command.run();
        }
    }

    public static void main(String[] args) {
        Executor directExecutor = new DirectExecutor();
        directExecutor.execute(() -> System.out.println("Hello my executor!"));

        ExecutorService runnableExecutor = Executors.newFixedThreadPool(1);
        runnableExecutor.execute(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Hello executor service!");
        });
        runnableExecutor.shutdown();

        ExecutorService callableExecutor = Executors.newFixedThreadPool(1);
        Future<String> task = callableExecutor.submit(() -> "Hello future!");
        try {
            String msg = task.get();
            System.out.println(msg);
            System.out.println("Is done: " + task.isDone());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        callableExecutor.shutdown();
    }
}
