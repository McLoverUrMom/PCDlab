import java.util.ArrayList;
import java.util.Random;
import java.util.List;
import java.util.concurrent.CountDownLatch;


public class Main {


    static List<Integer> A;

    static final Object PRINT_LOCK = new Object();

    static CountDownLatch latch1 = new CountDownLatch(1);
    static CountDownLatch latch2 = new CountDownLatch(1);
    static CountDownLatch latch3 = new CountDownLatch(1);

    static void printSlow(String s) {
        synchronized (PRINT_LOCK) {
            System.out.print(Thread.currentThread().getName() + ": ");
            for (char c : s.toCharArray()) {
                System.out.print(c);
                long start = System.currentTimeMillis();
                while (System.currentTimeMillis() - start < 100) { /* busy-wait */ }
            }
            System.out.println();
            System.out.flush();
        }
    }
    public static void main(String[] args) {
        System.out.println("--------- Начало программы---------");
        System.out.println("Массив генерируется, числа генерируються");

        Random randomMoiDom = new Random();
        int[] B = new int[200];  // создание массива
        System.out.println(" Массив с генерацией случайных чисел создан");

        for (int i = 0; i < 200; i++ ){
            B[i] = randomMoiDom.nextInt(201); // заполнение рандомными числами от 0 до 200
        }

        // вывод массива В
        System.out.print("Массив B ");
        for (int num : B){
            System.out.print(num + " ");
        }
        System.out.println("\n");

        // вывод массива A
        A = new ArrayList<>();

        for (int num : B){
            if (num % 2 == 0){
                A.add(num);
            }
        }


        Th1 th1 = new Th1();
        th1.setName("Th1");
        th1.start();

        Th2 th2 = new Th2();
        th2.setName("Th2");
        th2.start();

        Th3 th3 = new Th3();
        th3.setName("Th3");
        th3.start();

        Th4 th4 = new Th4();
        th4.setName("Th4");
        th4.start();



    }

    public static class Th1 extends Thread {
        public Th1() {super("Th1");}
        @Override
        public void run() {
            System.out.println(" Th1: Начало задачи 1(подсчет по два с начала)");
                for (int i = 0 ; i <= A.size() - 4; i+= 4 ){
                    int pair1 = A.get(i) + A.get(i + 1);
                    int pair2 = A.get(i + 2) + A.get( i + 3);
                    int result = pair1 + pair2;
                    System.out.println(" Th1: ("+ A.get(i) + "+" + A.get(i + 1) +") + ("+ A.get(i + 2) + "+" + A.get(i + 3) +") = "+ result );
            }
            System.out.println("Th1: первая задача выполнена ");
            latch1.countDown();
            printSlow(" Фамилия: Спринчан");

        }
    }

    public static class Th2 extends Thread {
        public Th2() {super("Th2");}
        @Override
        public void run() {
           try {
               latch1.await();
           } catch (InterruptedException e) {}
           System.out.println(" Th2: Начало задачи 2(подсчет по два с конца)");

           for (int i = A.size() - 1  ; i >= 3; i-= 4 ){
                    int pair1 = A.get(i) + A.get(i - 1);
                    int pair2 = A.get(i - 2) + A.get( i - 3);
                    int result = pair1 + pair2;
                    System.out.println(" Th2: ("+ A.get(i) + "+" + A.get(i - 1) +") + ("+ A.get(i - 2) + "+" + A.get(i - 3) +") = "+ result );
                }
                System.out.println("Th2: вторая задача выполнена ");
                latch2.countDown();
                printSlow( "Имя: Даниил");
       }
    }

    public static class Th3 extends Thread {
        public Th3() {super("Th3");}
        @Override
        public void run() {
            try {
                latch2.await();
            } catch (InterruptedException e) {}

            System.out.println(" Th3: Начало задачи 3(вывод интервала с 100 по 500)");
            for (int i = 100; i <= 500; i++){
                System.out.println(i);
            }
            System.out.println("Th3: третья задача выполнена ");
            latch3.countDown();
            printSlow( "Предмет: Programarea concurentă și distribuită");
        }
    }

    public static class Th4 extends Thread {
        public Th4() {super("Th4");}
        @Override
        public void run() {
            try {
                latch3.await();
            } catch (InterruptedException e) {}

            System.out.println(" Th3: Начало задачи 4(вывод интервала с 300 по 700)");
            for (int i = 700; i >= 300; i--){
                System.out.println(i);
            }
            System.out.println("Th4: четвёртая задача выполнена ");
            printSlow( "Группа: CR-233");
        }
    }





}
