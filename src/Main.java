import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        Random random = new Random();
        int mas[] = new int[100]; // массив случайных чисел
        System.out.println("Создан массив с генерацией случайных чисел");

        for (int i = 0; i < 100; i++){
            mas[i] = random.nextInt(100); // заполнение массива
        }

        for (int i = 0; i < 100; i++) {
            System.out.println("Случайно сгенерированные числа:" + mas[i]);
        }

        int choice = sc.nextInt();
        switch(choice){
                case 1:
                   Thread thread1 = new Thread(new ThreadS1(mas));
                   Thread thread2 = new Thread(new ThreadS2(mas));
                    thread1.start();
                    thread2.start();

                    break;
                case 2:
                    Thread thread3 = new Thread(new ThreadL1(mas));
                    thread3.start();
                    Thread thread4 = new Thread(new ThreadL2(mas));
                    thread4.start();

                    break;
                default:
                    System.out.println("Нет такого варианта");
        }

        String message = " Работу выполнили Лучицкий и Спринчан";
        for (char ch : message.toCharArray()) {
            System.out.print(ch);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("\nПоток был прерван");
                break;
            }
        }
        System.out.println();

    }
}