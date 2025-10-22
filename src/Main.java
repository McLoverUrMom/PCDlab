import java.util.Scanner;

public class Main {
   public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Choose your fighter: 1.Sprincean, 2.Lutschii");
        int var = sc.nextInt();

        switch(var){
            case (1): chinese.chinese_start();
            case (2): medvedi.medvedi_start();
        }
    }
}