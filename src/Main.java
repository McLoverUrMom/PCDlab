import java.util.Random;
public class Main {
    public static void main(String[] args) {


        Random random = new Random;
        int mas[] = new mas[100]; // массив случайных чисел
        System.out.println("Создан массив с генерацией случайных чисел")

        for (int =0;i < 100;i++){
            mas[i] = random.nextInt(100); // заполнение массива
        }

        for (int i = 0; i < 100; i++) {
            System.out.println("Случайно сгенерированные числа:" + mas[i]);
        }
        switch (choice){
                case 1:
                    thread = new Thread(new ThreadS1(mas));
                    thread = new Thread(new ThreadS2(mas));
                    ThreadS1.start();
                    ThreadS2.start();
                    break;
                case 2:
                    thread = new Thread(new ThreadL1(mas));
                    break;
                default:
                    System.out.println("Нет такого варианта")
        }
    }
}