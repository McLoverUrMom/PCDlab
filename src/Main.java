import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class Main {

    private static final String STUDENT_NAME = "Артур";            // будет печатать Th2
    private static final String STUDENT_SURNAME = "Лучицкий";    // будет печатать Th1
    private static final String GROUP = "CR-233";            // будет печатать Th4
    private static final String DISCIPLINE = "Programarea concurentă și distribuită"; // будет печатать Th3

    private static final Semaphore semTh2 = new Semaphore(1); // Th2 стартует первым
    private static final Semaphore semTh4 = new Semaphore(0);
    private static final Semaphore semTh1 = new Semaphore(0);
    private static final Semaphore semTh3 = new Semaphore(0);

    public static void main(String[] args) throws InterruptedException {

        List<Integer> rangeA = buildRange(11, 548);    // для Th1 и Th2
        List<Integer> rangeB = buildRange(1234, 678);  // для Th3 и Th4

        Thread th1 = new Thread(() -> task1(rangeA), "Th1");
        Thread th2 = new Thread(() -> task2(rangeA), "Th2");
        Thread th3 = new Thread(() -> task3(rangeB), "Th3");
        Thread th4 = new Thread(() -> task4(rangeB), "Th4");

        th1.start();
        th2.start();
        th3.start();
        th4.start();

        th1.join();
        th2.join();
        th3.join();
        th4.join();

        System.out.println("\nВсе потоки завершены.");
    }

    private static List<Integer> buildRange(int a, int b) {
        List<Integer> list = new ArrayList<>();
        if (a <= b) {
            for (int i = a; i <= b; i++) list.add(i);
        } else {
            for (int i = a; i >= b; i--) list.add(i);
        }
        return list;
    }

    private static void task1(List<Integer> range) {
        try {
            System.out.println(Thread.currentThread().getName() + ": старт задачи 1 (по началу диапазона).");

            BigInteger prodOdd = BigInteger.ONE;
            BigInteger prodEven = BigInteger.ONE;
            for (int i = 0; i < range.size(); i++) {
                int val = range.get(i);
                if ((i % 2) == 0) prodOdd = prodOdd.multiply(BigInteger.valueOf(val));
                else prodEven = prodEven.multiply(BigInteger.valueOf(val));
            }
            BigInteger diff = prodOdd.subtract(prodEven);
            System.out.println(Thread.currentThread().getName() + ": prodOdd digits=" + prodOdd.toString().length()
                    + ", prodEven digits=" + prodEven.toString().length());
            System.out.println(Thread.currentThread().getName() + ": diff (prodOdd - prodEven) = " + diff);

            semTh1.acquire();
            printWithDelay(STUDENT_SURNAME);
            semTh3.release();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void task2(List<Integer> range) {
        try {
            System.out.println(Thread.currentThread().getName() + ": старт задачи 2 (по концу диапазона).");

            BigInteger prodOdd = BigInteger.ONE;
            BigInteger prodEven = BigInteger.ONE;
            int pos = 1;
            for (int i = range.size() - 1; i >= 0; i--) {
                int val = range.get(i);
                if ((pos % 2) == 1) prodOdd = prodOdd.multiply(BigInteger.valueOf(val));
                else prodEven = prodEven.multiply(BigInteger.valueOf(val));
                pos++;
            }
            BigInteger diff = prodOdd.subtract(prodEven);
            System.out.println(Thread.currentThread().getName() + ": prodOdd digits=" + prodOdd.toString().length()
                    + ", prodEven digits=" + prodEven.toString().length());
            System.out.println(Thread.currentThread().getName() + ": diff (prodOdd - prodEven) = " + diff);

            semTh2.acquire();
            printWithDelay(STUDENT_NAME);
            semTh4.release();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void task3(List<Integer> range) {
        try {
            System.out.println(Thread.currentThread().getName() + ": старт задачи 3 (пройти с начала по rangeB).");
            long sum = 0;
            int count = 0;
            for (int v : range) {
                sum += v;
                count++;
            }
            System.out.printf("%s: rangeB start -> sum=%d, count=%d, avg=%.3f%n",
                    Thread.currentThread().getName(), sum, count, (count == 0 ? 0.0 : (double) sum / count));

            semTh3.acquire();
            printWithDelay(DISCIPLINE);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void task4(List<Integer> range) {
        try {
            System.out.println(Thread.currentThread().getName() + ": старт задачи 4 (пройти с конца по rangeB).");
            long sum = 0;
            int count = 0;
            for (int i = range.size() - 1; i >= 0; i--) {
                sum += range.get(i);
                count++;
            }
            System.out.printf("%s: rangeB end -> sum=%d, count=%d, avg=%.3f%n",
                    Thread.currentThread().getName(), sum, count, (count == 0 ? 0.0 : (double) sum / count));

            semTh4.acquire();
            printWithDelay(GROUP);

            semTh1.release();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void printWithDelay(String text) throws InterruptedException {
        System.out.println();
        for (char c : text.toCharArray()) {
            System.out.print(c);
            System.out.flush();
            Thread.sleep(100);
        }
        System.out.println();
    }
}
