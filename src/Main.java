import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    static final int X = 4;
    static final int Y = 3;
    static final int Z = 50;
    static final int D = 10;

    static final char[] VOWELS = {'a', 'e', 'i', 'o', 'u', 'y'};

    static class Depot {
        private final Deque<Character> buffer = new ArrayDeque<>(D);
        private final ReentrantLock lock = new ReentrantLock(true);

        private final Condition notFull = lock.newCondition();
        private final Condition notEmpty = lock.newCondition();

        private int producedTotal = 0;
        private int consumedTotal = 0;

        private final Random rnd = new Random();
        volatile boolean done = false;


        private boolean fillPhase = true;

        void produce(String name) throws InterruptedException {
            lock.lock();
            try {
                if (producedTotal >= Z) return;


                while (!fillPhase) {
                    System.out.println("Store is full");
                    notFull.await();
                }

                while (buffer.size() == D) {
                    System.out.println(">>> Склад полон (" + buffer.size() + "/" + D + ")");
                    fillPhase = false;
                    notEmpty.signalAll();
                    notFull.await();
                }

                char c = VOWELS[rnd.nextInt(VOWELS.length)];
                buffer.addLast(c);
                producedTotal++;

                System.out.printf("%s произвел '%c' | Запас = %d/%d | total = %d%n",
                        name, c, buffer.size(), D, producedTotal);


                if (buffer.size() == D) {
                    fillPhase = false;
                    notEmpty.signalAll();
                }

            } finally {
                lock.unlock();
            }
        }

        void consume(String name) throws InterruptedException {
            lock.lock();
            try {


                while (fillPhase) {
                    System.out.println("Store is empty");
                    notEmpty.await();
                }

                while (buffer.isEmpty() && !done) {
                    System.out.println("<<< Склад пуст (0/" + D + ")");
                    fillPhase = true;
                    notFull.signalAll();
                    notEmpty.await();
                }

                if (buffer.isEmpty() && done) return;

                char c = buffer.removeFirst();
                consumedTotal++;

                System.out.printf("%s потребил '%c' | Запас = %d/%d | total = %d%n",
                        name, c, buffer.size(), D, consumedTotal);


                if (buffer.isEmpty()) {
                    fillPhase = true;
                    notFull.signalAll();
                }

                if (consumedTotal >= Z) {
                    done = true;
                }

            } finally {
                lock.unlock();
            }
        }
    }


    static class Producer extends Thread {
        private final Depot depot;

        Producer(Depot depot, String name) {
            super(name);
            this.depot = depot;
        }

        @Override
        public void run() {
            try {
                while (!depot.done) {
                    depot.produce(getName());
                    Thread.sleep(80);
                }
            } catch (Exception ignored) {}
        }
    }


    static class Consumer extends Thread {
        private final Depot depot;

        Consumer(Depot depot, String name) {
            super(name);
            this.depot = depot;
        }

        @Override
        public void run() {
            try {
                while (!depot.done) {
                    depot.consume(getName());
                    Thread.sleep(120);
                }
            } catch (Exception ignored) {}
        }
    }


    public static void main(String[] args) throws InterruptedException {

        Depot depot = new Depot();

        Thread[] prod = new Thread[X];
        Thread[] cons = new Thread[Y];

        for (int i = 0; i < X; i++)
            prod[i] = new Producer(depot, "Производитель-" + (i + 1));

        for (int i = 0; i < Y; i++)
            cons[i] = new Consumer(depot, "Потребитель-" + (i + 1));

        for (Thread t : prod) t.start();
        for (Thread t : cons) t.start();

        for (Thread t : prod) t.join();
        for (Thread t : cons) t.interrupt();

        System.out.println("=== Готово! Было произведено и потреблено " + Z + " гласных. ===");
    }
}
