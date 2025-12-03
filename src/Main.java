import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Main {

    private static final Object PRINT_LOCK = new Object();
    private static final AtomicInteger eventCounter = new AtomicInteger(0);

    static void log(String msg) {
        synchronized (PRINT_LOCK) {
            System.out.println("(" + eventCounter.incrementAndGet() + ") " + msg);
        }
    }

    private static final int BUFFER_CAPACITY = 2;  
    private static final int PRODUCER_COUNT = 3;   
    private static final int CONSUMER_COUNT = 3;   
    private static final int CONSUMER_GOAL = 5;    
    private static final int F = 2;              

    private static final BlockingQueue<Integer> buffer =
            new ArrayBlockingQueue<>(BUFFER_CAPACITY);

    private static final int TOTAL_OBJECTS = CONSUMER_GOAL * CONSUMER_COUNT; 

    private static final AtomicInteger totalProduced = new AtomicInteger(0);
    private static final AtomicInteger totalConsumed = new AtomicInteger(0);
    private static final AtomicInteger totalRemaining = new AtomicInteger(TOTAL_OBJECTS); 
    private static final Map<Integer, AtomicInteger> consumerCounters = new ConcurrentHashMap<>();
    private static final Map<Integer, AtomicInteger> producerCounters = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        System.out.printf("Вариант: X=%d, Y=%d, Z=%d, D=%d, F=%d (объекты: нечётные числа)%n%n",
                PRODUCER_COUNT, CONSUMER_COUNT, CONSUMER_GOAL, BUFFER_CAPACITY, F);

        for (int i = 0; i < CONSUMER_COUNT; i++) consumerCounters.put(i, new AtomicInteger(0));
        for (int i = 0; i < PRODUCER_COUNT; i++) producerCounters.put(i, new AtomicInteger(0));

        int totalTasks = PRODUCER_COUNT + CONSUMER_COUNT;
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(); 
        ThreadFactory tf = new ThreadFactory() {
            private final AtomicInteger idx = new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "pool-thread-" + idx.getAndIncrement());
                t.setDaemon(false);
                return t;
            }
        };

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                totalTasks,           
                totalTasks,            
                60L, TimeUnit.SECONDS, 
                workQueue,
                tf,
                new ThreadPoolExecutor.AbortPolicy() 
        );

        for (int i = 0; i < PRODUCER_COUNT; i++) {
            executor.execute(new Producer(i));
        }

        for (int i = 0; i < CONSUMER_COUNT; i++) {
            executor.execute(new Consumer(i));
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(120, TimeUnit.SECONDS)) {
                log("Executor не успел завершиться за 120 секунд, делаем shutdownNow()");
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        System.out.println("\n========== ИТОГОВЫЙ ОТЧЕТ ==========");
        System.out.println("Запланировано объектoв (TOTAL_OBJECTS): " + TOTAL_OBJECTS);
        System.out.println("Произведено (totalProduced): " + totalProduced.get());
        System.out.println("Съедено (totalConsumed): " + totalConsumed.get());
        System.out.println("\nПо потребителям:");
        consumerCounters.forEach((id, count) ->
                System.out.println("  Потребитель " + id + ": " + count.get() + " шт."));
        System.out.println("\nПо производителям:");
        producerCounters.forEach((id, count) ->
                System.out.println("  Производитель " + id + ": " + count.get() + " шт."));
    }

    static class Producer implements Runnable {
        private final int id;
        private final Random rnd = new Random();

        Producer(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    int reserved = reserveBatch();
                    if (reserved <= 0) {
                        log("[P" + id + "] Нечего резервировать — выхожу");
                        return;
                    }

                    for (int i = 0; i < reserved; i++) {
                        int item = generateOdd();

                        if (buffer.remainingCapacity() == 0) {
                            log("[P" + id + "] Store FULL, waiting");
                        }

                        buffer.put(item);

                        int prod = totalProduced.incrementAndGet();
                        producerCounters.get(id).incrementAndGet();

                        log("[P" + id + "] → сделал: " + item +
                                " | producedTotal: " + prod +
                                " | store: " + buffer.size() + "/" + BUFFER_CAPACITY);
                    }

                    Thread.sleep(60 + rnd.nextInt(80));
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log("[P" + id + "] Прерван");
            }
        }

        private int reserveBatch() {
            while (true) {
                int remain = totalRemaining.get();
                if (remain <= 0) return 0;
                int take = Math.min(F, remain);
                if (totalRemaining.compareAndSet(remain, remain - take)) {
                    return take;
                }
            }
        }

        private int generateOdd() {
            return rnd.nextInt(500) * 2 + 1;
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

                    Integer item = buffer.take(); 

                    consumerCounters.get(id).incrementAndGet();
                    int consumed = totalConsumed.incrementAndGet();

                    log("[C" + id + "] → съел: " + item +
                            " | local: " + consumerCounters.get(id).get() +
                            " | totalConsumed: " + consumed +
                            " | store: " + buffer.size());

                    Thread.sleep(40 + rnd.nextInt(30));
                }

                log("[C" + id + "] Насытились! (" + CONSUMER_GOAL + " объекта(ов))");

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log("[C" + id + "] Прерван");
            }
        }
    }
}
