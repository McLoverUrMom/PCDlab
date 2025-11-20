import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    // Параметры варианта
    private static final int X = 10; // producers
    private static final int Y = 3;  // consumers
    private static final int Z = 3;  // each consumer must consume Z items
    private static final int D = 5;  // buffer capacity
    private static final int F = 2;  // each producer produces F items at once

    public static void main(String[] args) throws InterruptedException {
        System.out.printf("Вариант: X=%d, Y=%d, Z=%d, D=%d, F=%d (объекты: нечётные числа)%n",
                X, Y, Z, D, F);

        // сколько всего нужно всем потребителям (чтобы не перепроизводить)
        AtomicInteger totalNeeded = new AtomicInteger(Y * Z);
        AtomicInteger producersAlive = new AtomicInteger(X);

        Store store = new Store(D);

        Thread[] producers = new Thread[X];
        Thread[] consumers = new Thread[Y];

        // создаём производителей
        for (int i = 0; i < X; i++) {
            String name = "Producer-" + (i + 1);
            producers[i] = new Thread(new Producer(name, store, F, totalNeeded, producersAlive), name);
        }

        // создаём потребителей
        for (int i = 0; i < Y; i++) {
            String name = "Consumer-" + (i + 1);
            consumers[i] = new Thread(new Consumer(name, store, Z, producersAlive, totalNeeded), name);
        }

        // стартуем производителей и потребителей
        for (Thread p : producers) p.start();
        for (Thread c : consumers) c.start();

        // ждём завершения потребителей (они гарантированно завершатся, когда получат Z объектов или когда производство закончится)
        for (Thread c : consumers) c.join();

        // после того как все потребители завершили — можно подождать производителей (они завершят сами, когда суммарная потребность исчерпана)
        for (Thread p : producers) p.join();

        System.out.println("Все потоки завершены. Программа закончила работу.");
    }

    // ========== Store (put/get) ==========
    static class Store {
        private final Deque<Integer> buffer = new ArrayDeque<>();
        private final int capacity;

        public Store(int capacity) {
            this.capacity = capacity;
        }

        // put: кладёт один объект; если места нет — ждёт и показывает сообщение "Склад полон"
        public synchronized void put(int value, String producer) throws InterruptedException {
            while (buffer.size() == capacity) {
                System.out.printf("[%s] Склад полон (на складе: %d/%d). Жду...%n", producer, buffer.size(), capacity);
                wait();
            }
            buffer.addLast(value);
            System.out.printf("[%s] Положил: %d (на складе: %d/%d)%n", producer, value, buffer.size(), capacity);
            notifyAll();
        }

        // get: берёт один объект; если пусто — ждёт и показывает сообщение "Склад пуст".
        // Если производителей уже нет и буфер пуст — возвращает null (сигнал для завершения потребителя).
        public synchronized Integer get(String consumer, boolean producersAlive) throws InterruptedException {
            while (buffer.isEmpty()) {
                if (!producersAlive) {
                    // производителей нет и буфер пуст — больше не придёт
                    return null;
                }
                System.out.printf("[%s] Склад пуст. Жду...%n", consumer);
                wait();
            }
            int v = buffer.removeFirst();
            System.out.printf("[%s] Взял: %d (на складе: %d/%d)%n", consumer, v, buffer.size(), capacity);
            notifyAll();
            return v;
        }

        // для отладки/лога можно добавить метод вывода текущего содержимого (не обязателен)
        public synchronized String snapshot() {
            return buffer.toString();
        }
    }

    // ========== Producer ==========
    static class Producer implements Runnable {
        private final String name;
        private final Store store;
        private final int batchSize; // F
        private final AtomicInteger totalNeeded;
        private final AtomicInteger producersAlive;
        private final Random rnd = new Random();

        public Producer(String name, Store store, int batchSize,
                        AtomicInteger totalNeeded, AtomicInteger producersAlive) {
            this.name = name;
            this.store = store;
            this.batchSize = batchSize;
            this.totalNeeded = totalNeeded;
            this.producersAlive = producersAlive;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    // резервируем количество для производства (атомарно) — чтобы суммарно не превысить Y*Z
                    int toProduce = reserveToProduce();
                    if (toProduce <= 0) break;

                    // генерируем toProduce нечётных чисел и кладём их по одному через put()
                    for (int i = 0; i < toProduce; i++) {
                        int odd = generateOdd();
                        store.put(odd, name);
                        // небольшая пауза, чтобы выходы логов были читаемы
                        Thread.sleep(10 + rnd.nextInt(60));
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                int left = producersAlive.decrementAndGet();
                // пробудим всех ожидающих, чтобы потребители могли завершиться, если буфер пуст
                synchronized (store) {
                    store.notifyAll();
                }
                System.out.printf("[%s] Завершил работу. Осталось производителей: %d%n", name, left);
            }
        }

        // атомарное резервирование: уменьшаем totalNeeded на take и возвращаем take
        private int reserveToProduce() {
            while (true) {
                int need = totalNeeded.get();
                if (need <= 0) return 0;
                int take = Math.min(batchSize, need);
                if (totalNeeded.compareAndSet(need, need - take)) {
                    return take;
                }
                // иначе повторяем попытку
            }
        }

        private int generateOdd() {
            return rnd.nextInt(1000) * 2 + 1; // нечётное число
        }
    }

    // ========== Consumer ==========
    static class Consumer implements Runnable {
        private final String name;
        private final Store store;
        private final int target; // Z
        private final AtomicInteger producersAlive;
        private final AtomicInteger totalNeeded; // не обязательно, но может быть полезно

        public Consumer(String name, Store store, int target,
                        AtomicInteger producersAlive, AtomicInteger totalNeeded) {
            this.name = name;
            this.store = store;
            this.target = target;
            this.producersAlive = producersAlive;
            this.totalNeeded = totalNeeded;
        }

        @Override
        public void run() {
            int got = 0;
            try {
                while (got < target) {
                    boolean alive = producersAlive.get() > 0;
                    Integer val = store.get(name, alive);
                    if (val == null) {
                        // буфер пуст и производителей нет — завершение
                        break;
                    }
                    // потребляем объект
                    got++;
                    // эмуляция обработки
                    Thread.sleep(40);
                }
                System.out.printf("[%s] Удовлетворён: получил %d/%d%n", name, got, target);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
