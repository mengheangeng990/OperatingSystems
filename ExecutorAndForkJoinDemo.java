import java.util.concurrent.*;

public class ExecutorAndForkJoinDemo {

    // -----------------------------
    // 1. SingleThreadExecutor Demo
    // -----------------------------
    public static void demoSingleThreadExecutor() {
        System.out.println("\n=== SingleThreadExecutor Demo ===");
        ExecutorService executor = Executors.newSingleThreadExecutor();

        for (int i = 1; i <= 3; i++) {
            final int taskId = i;
            executor.submit(() -> {
                System.out.println("SingleThread Task " + taskId + " running on: " + Thread.currentThread().getName());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.out.println("SingleThread Task " + taskId + " completed.");
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // -----------------------------
    // 2. CachedThreadPool Demo
    // -----------------------------
    public static void demoCachedThreadPool() {
        System.out.println("\n=== CachedThreadPool Demo ===");
        ExecutorService executor = Executors.newCachedThreadPool();

        for (int i = 1; i <= 5; i++) {
            final int taskId = i;
            executor.submit(() -> {
                System.out.println("CachedPool Task " + taskId + " running on: " + Thread.currentThread().getName());
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.out.println("CachedPool Task " + taskId + " completed.");
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // -----------------------------
    // 3. Fork-Join Framework Demo (with visualization)
    // -----------------------------
    static class SumTask extends RecursiveTask<Long> {
        private final long start;
        private final long end;
        private static final long THRESHOLD = 10_000; // Split if larger than this

        public SumTask(long start, long end) {
            this.start = start;
            this.end = end;
        }

        @Override
        protected Long compute() {
            long diff = end - start;
            String threadName = Thread.currentThread().getName();

            // Visualization: print task details
            System.out.println(threadName + " | Computing sum from " + start + " to " + end + " (size=" + (diff + 1) + ")");

            if (diff <= THRESHOLD) {
                // Base case: compute directly
                long sum = 0;
                for (long i = start; i <= end; i++) {
                    sum += i;
                }
                System.out.println(threadName + " | Direct sum [" + start + "," + end + "] = " + sum);
                return sum;
            } else {
                // Recursive case: split
                long mid = start + diff / 2;
                SumTask leftTask = new SumTask(start, mid);
                SumTask rightTask = new SumTask(mid + 1, end);

                // Fork the left task (asynchronous)
                leftTask.fork();
                // Compute right task in current thread
                Long rightResult = rightTask.compute();
                // Wait for left result
                Long leftResult = leftTask.join();

                long total = leftResult + rightResult;
                System.out.println(threadName + " | Combined [" + start + "," + end + "] = " + total);
                return total;
            }
        }
    }

    public static void demoForkJoin() {
        System.out.println("\n=== Fork-Join Framework Visualization ===");
        ForkJoinPool forkJoinPool = new ForkJoinPool();

        // Compute sum from 1 to 50,000
        SumTask task = new SumTask(1, 50_000);
        long result = forkJoinPool.invoke(task);

        System.out.println("\n✅ Final Result (1 to 50,000) = " + result);
        System.out.println("Expected: " + (50_000L * 50_001L / 2));

        forkJoinPool.shutdown();
    }

    // -----------------------------
    // Main Method
    // -----------------------------
    public static void main(String[] args) {
        demoSingleThreadExecutor();
        demoCachedThreadPool();
        demoForkJoin();
    }
}