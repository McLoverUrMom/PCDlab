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
                    Thread thread4 = new Thread(new ThreadL2(mas));
                    thread3.start();
                    thread4.start();
                default:
                    System.out.println("Нет такого варианта");
        }
    }
}