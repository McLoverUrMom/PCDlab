import java.util.concurrent.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    private static final Object PRINT_LOCK = new Object();
    private static final AtomicInteger eventCounter = new AtomicInteger(0);

    static void log(String msg) {
        synchronized (PRINT_LOCK) {
            System.out.println("(" + eventCounter.incrementAndGet() + ") " + msg);
        }
    }

    private static final int BUFFER_CAPACITY = 9;
    private static final BlockingQueue<Character> buffer =
            new ArrayBlockingQueue<>(BUFFER_CAPACITY);

    private static final int CONSUMER_GOAL = 3;
    private static final int PRODUCER_COUNT = 4;
    private static final int CONSUMER_COUNT = 3;

    private static final int TOTAL_OBJECTS = CONSUMER_GOAL * CONSUMER_COUNT;

    private static final AtomicInteger totalProduced = new AtomicInteger(0);
    private static final AtomicInteger totalConsumed = new AtomicInteger(0);
    private static final Map<Integer, AtomicInteger> consumerCounters = new ConcurrentHashMap<>();


    public static void main(String[] args) {

        ExecutorService executor = Executors.newFixedThreadPool(PRODUCER_COUNT + CONSUMER_COUNT);

        for (int i = 0; i < CONSUMER_COUNT; i++) {
            consumerCounters.put(i, new AtomicInteger(0));
        }

        for (int i = 0; i < PRODUCER_COUNT; i++) {
            executor.execute(new Producer(i));
        }

        for (int i = 0; i < CONSUMER_COUNT; i++) {
            executor.execute(new Consumer(i));
        }

        executor.shutdown();

        try {
            executor.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n========== ИТОГОВЫЙ ОТЧЕТ ==========");
        System.out.println("Произведено: " + totalProduced.get());
        System.out.println("Съедено: " + totalConsumed.get());
        consumerCounters.forEach((id, count) ->
                System.out.println("Потребитель " + id + ": " + count.get() + " штук"));
    }


    static class Producer implements Runnable {
        private final int id;
        private final Random rnd = new Random();
        private final char[] vocals = "aeiouAEIOU".toCharArray();

        Producer(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {

                while (true) {

                    int current = totalProduced.incrementAndGet();
                    if (current > TOTAL_OBJECTS) {
                        totalProduced.decrementAndGet();
                        return;
                    }

                    char item = vocals[rnd.nextInt(vocals.length)];

                    if (buffer.remainingCapacity() == 0) {
                        log("[P" + id + "] Store FULL, waiting");
                    }

                    buffer.put(item);

                    log("[P" + id + "] → сделал: " + item +
                            " | store: " + buffer.size() + "/" + BUFFER_CAPACITY);

                    // 🔥 задержка приготовления блюда
                    Thread.sleep(120 + rnd.nextInt(80)); // 120–200 ms
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    static class Consumer implements Runnable {
        private final int id;
        private final Random rnd = new Random();

        Consumer(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {

                while (consumerCounters.get(id).get() < CONSUMER_GOAL) {

                    if (buffer.isEmpty()) {
                        log("[C" + id + "] Store EMPTY, waiting");
                    }

                    char item = buffer.take();

                    consumerCounters.get(id).incrementAndGet();
                    int consumed = totalConsumed.incrementAndGet();

                    log("[C" + id + "] → схавал: " + item +
                            " | local: " + consumerCounters.get(id).get() +
                            " | total: " + consumed +
                            " | store: " + buffer.size());

                    // 🔥 Consumer ест быстро
                    Thread.sleep(40 + rnd.nextInt(30)); // 40–70 ms
                }

                log("[C" + id + "] Насытились! (3 объекта)");

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
