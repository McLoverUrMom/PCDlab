class ThreadL1 implements Runnable {
    private int[] mas;

    public ThreadL1(int[] mas) {
        this.mas = mas;
    }

    @Override
    public void run() {
        if (mas.length == 0) {
            System.out.println("1.Массив пуст");
            return;
        }

        int oddCount = 0;
        for (int num : mas) {
            if (num % 2 != 0) {
                oddCount++;
            }
        }

        if (oddCount < 2) {
            System.out.println("1.Недостаточно нечётных чисел для формирования пар");
            return;
        }

        int[] oddNumbers = new int[oddCount];
        int idx = 0;
        for (int num : mas) {
            if (num % 2 != 0) {
                oddNumbers[idx++] = num;
            }
        }

        int pairCount = oddNumbers.length / 2;
        int[] products = new int[pairCount];

        System.out.println("1.Произведения пар нечётных чисел:");

        for (int i = 0; i < pairCount; i++) {
            System.out.println("Умножаем: " + oddNumbers[2 * i] + " * " + oddNumbers[2 * i + 1]);
            products[i] = oddNumbers[2 * i] * oddNumbers[2 * i + 1];
        }

        int result = 0;
        System.out.println("1.Разницы между соседними произведениями:");
        for (int i = 0; i + 1 < pairCount; i++) {
            int diff = products[i] - products[i + 1];
            result += diff;
            System.out.println("Разница между произведениями (" + oddNumbers[2 * i] + " * " + oddNumbers[2 * i + 1] + ") и (" +
                    oddNumbers[2 * (i + 1)] + " * " + oddNumbers[2 * (i + 1) + 1] + ") = " + diff);
        }

        result += mas[0];

        System.out.println("1.Нечётные числа из массива:");
        for (int val : oddNumbers) {
            System.out.print(val + " ");
        }
        System.out.println();

        System.out.println("1.Выполненное действие первого потока: " + result);
    }
}