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
    }
}