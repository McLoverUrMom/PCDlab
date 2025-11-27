import java.util.ArrayList;
import java.util.Random;

public class Main {

    public static final int X = 10;
    public static final int Y = 3;
    public static final int Z = 3;
    public static final int D = 5;

    public static void main(String[] args) throws InterruptedException {

        Store store = new Store();

        Thread p1 = new Thread(new Producer(store), "Производитель №1");
        p1.setDaemon(true);
        Thread p2 = new Thread(new Producer(store), "Производитель №2");
        p2.setDaemon(true);
        Thread p3 = new Thread(new Producer(store), "Производитель №3");
        p3.setDaemon(true);
        Thread p4 = new Thread(new Producer(store), "Производитель №4");
        p4.setDaemon(true);
        Thread p5 = new Thread(new Producer(store), "Производитель №5");
        p5.setDaemon(true);
        Thread p6 = new Thread(new Producer(store), "Производитель №6");
        p6.setDaemon(true);
        Thread p7 = new Thread(new Producer(store), "Производитель №7");
        p7.setDaemon(true);
        Thread p8 = new Thread(new Producer(store), "Производитель №8");
        p8.setDaemon(true);
        Thread p9 = new Thread(new Producer(store), "Производитель №9");
        p9.setDaemon(true);
        Thread p10 = new Thread(new Producer(store), "Производитель №10");
        p10.setDaemon(true);

        Thread c1 = new Thread(new Consumer(store), "Потребитель №1");
        Thread c2 = new Thread(new Consumer(store), "Потребитель №2");
        Thread c3 = new Thread(new Consumer(store), "Потребитель №3");

        p1.start();
        p2.start();
        p3.start();
        p4.start();
        p5.start();
        p6.start();
        p7.start();
        p8.start();
        p9.start();
        p10.start();

        c1.start();
        c2.start();
        c3.start();

        while (c1.isAlive() || c2.isAlive() || c3.isAlive()) {
            Thread.sleep(50);
        }

        System.out.println("Все потоки (потребители) завершены. Программа завершает работу.");
    }
}

class Store {

    public final ArrayList<Integer> stockList = new ArrayList<Integer>();
    public final int capacity = Main.D;

    public synchronized void get(String str) {
        while (stockList.size() < 1) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }

        // Берём последнее число со склада
        int item = stockList.get(stockList.size() - 1);
        stockList.remove(stockList.size() - 1);

        System.out.println(str + " взял со склада: " + item);

        if (stockList.size() != 0) {
            System.out.print("На складе имеется " + stockList.size() + " единиц -> ");
            for (int v : stockList) {
                System.out.print(v + " ");
            }
            System.out.println();
        } else {
            System.out.println("Склад пуст");
        }

        notifyAll();
    }

    // put двух значений (как в примере)
    public synchronized void put(String str, int a, int b) {
        while (stockList.size() + 2 > capacity) {
            System.out.println(">>> " + str + " хочет положить два числа, но склад ПОЛОН (" + stockList.size() + "/" + capacity + "). Ждёт...");
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }

        System.out.print(str + " поместил в хранилище два числа: ");
        stockList.add(a);
        System.out.print(stockList.get(stockList.size() - 1) + ", ");
        stockList.add(b);
        System.out.println(stockList.get(stockList.size() - 1));

        if (stockList.size() != 0) {
            System.out.print("На складе имеется " + stockList.size() + " единиц -> ");
            for (int v : stockList) {
                System.out.print(v + " ");
            }
            System.out.println();
        } else {
            System.out.println("Склад пуст");
        }

        notifyAll();
    }
}

class Producer implements Runnable {

    public final Store s;
    public final Random rnd = new Random();
    public final int[] oddNumbers = new int[]{1, 3, 5, 7, 9, 11, 13, 15, 17, 19};

    public Producer(Store s) {
        this.s = s;
    }

    @Override
    public void run() {
        while (true) {
            int a = oddNumbers[rnd.nextInt(oddNumbers.length)];
            int b = oddNumbers[rnd.nextInt(oddNumbers.length)];
            s.put(Thread.currentThread().getName(), a, b);
            // в примере не было pause, производители быстрые и daemon
        }
    }
}

class Consumer implements Runnable {

    private final Store s;

    public Consumer(Store s) {
        this.s = s;
    }

    @Override
    public void run() {
        for (int i = 0; i < Main.Z; i++) {
            s.get(Thread.currentThread().getName());
        }
        System.out.println(Thread.currentThread().getName() + " взял " + Main.Z + " числа. Поток завершен");
    }
}
