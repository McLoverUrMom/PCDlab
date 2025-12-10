import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;
import java.util.concurrent.Phaser;
import java.util.concurrent.locks.ReentrantLock;
 
public class Main {

    static final int X_PRODUCERS = 3;   
    static final int Y_CONSUMERS = 3;   
    static final int D_CAPACITY = 8;    
    static final int Z_TOTAL = 48;      
    static final int F_PER_PRODUCE = 1; 

    static class Depot {
        final Deque<Integer> buffer = new ArrayDeque<>(D_CAPACITY);
        final ReentrantLock lock = new ReentrantLock(true); 
        final Random rnd = new Random();
        int producedTotal = 0;
        int consumedTotal = 0;
        volatile boolean done = false;

        void printFullIfNeeded() {
            if (buffer.size() == D_CAPACITY) {
                System.out.printf(">>> Склад заполнен (%d/%d)%n", buffer.size(), D_CAPACITY);
            }
        }

        void printEmptyIfNeeded() {
            if (buffer.isEmpty()) {
                System.out.printf("<<< Склад пуст (0/%d)%n", D_CAPACITY);
            }
        }

        boolean tryProduce(String name) {
            lock.lock();
            try {
                if (producedTotal >= Z_TOTAL) return false; 
                if (buffer.size() >= D_CAPACITY) return false; 
                int value = rnd.nextInt(1000) * 2 + 1; 
                buffer.addLast(value);
                producedTotal++;
                System.out.printf("%s произвел %d | запас=%d/%d | totalProduced=%d%n",
                        name, value, buffer.size(), D_CAPACITY, producedTotal);
                if (buffer.size() == D_CAPACITY) printFullIfNeeded();
                return true;
            } finally {
                lock.unlock();
            }
        }

        boolean tryConsume(String name) {
            lock.lock();
            try {
                if (buffer.isEmpty()) return false;
                int value = buffer.removeFirst();
                consumedTotal++;
                System.out.printf("%s потребил %d | запас=%d/%d | totalConsumed=%d%n",
                        name, value, buffer.size(), D_CAPACITY, consumedTotal);
                if (buffer.isEmpty()) printEmptyIfNeeded();
                if (consumedTotal >= Z_TOTAL && buffer.isEmpty()) {
                    done = true;
                }
                return true;
            } finally {
                lock.unlock();
            }
        }

        int size() {
            lock.lock();
            try {
                return buffer.size();
            } finally {
                lock.unlock();
            }
        }
    }

    static class Producer extends Thread {
        private final Depot depot;
        private final Phaser phaser;

        Producer(Depot depot, Phaser phaser, String name) {
            super(name);
            this.depot = depot;
            this.phaser = phaser;
        }

        @Override
        public void run() {
            try {
                while (!depot.done) {
                    int phase = phaser.getPhase();
                    if (phase % 2 == 0) { 
                        while (!depot.done) {
                            boolean made = depot.tryProduce(getName());
                            if (!made) break; 
                        }
                        phaser.arriveAndAwaitAdvance();
                    } else {
                        phaser.arriveAndAwaitAdvance();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                try { phaser.arriveAndDeregister(); } catch (IllegalStateException ignored) {}
            }
        }
    }

    static class Consumer extends Thread {
        private final Depot depot;
        private final Phaser phaser;

        Consumer(Depot depot, Phaser phaser, String name) {
            super(name);
            this.depot = depot;
            this.phaser = phaser;
        }

        @Override
        public void run() {
            try {
                while (!depot.done) {
                    int phase = phaser.getPhase();
                    if (phase % 2 == 1) { 
                        while (!depot.done) {
                            boolean took = depot.tryConsume(getName());
                            if (!took) break; 
                        }
                        phaser.arriveAndAwaitAdvance();
                    } else {
                        phaser.arriveAndAwaitAdvance();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                try { phaser.arriveAndDeregister(); } catch (IllegalStateException ignored) {}
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Depot depot = new Depot();

        Phaser phaser = new Phaser(X_PRODUCERS + Y_CONSUMERS);

        Thread[] producers = new Thread[X_PRODUCERS];
        Thread[] consumers = new Thread[Y_CONSUMERS];

        for (int i = 0; i < X_PRODUCERS; i++) {
            producers[i] = new Producer(depot, phaser, "Producer-" + (i + 1));
        }
        for (int i = 0; i < Y_CONSUMERS; i++) {
            consumers[i] = new Consumer(depot, phaser, "Consumer-" + (i + 1));
        }

        for (Thread t : producers) t.start();
        for (Thread t : consumers) t.start();

        for (Thread t : producers) t.join();
        for (Thread t : consumers) t.join();

        System.out.println("=== Готово: произведено = " + depot.producedTotal +
                ", потреблено = " + depot.consumedTotal + " (цель " + Z_TOTAL + ") ===");
    }
}
