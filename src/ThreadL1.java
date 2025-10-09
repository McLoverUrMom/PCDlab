class ThreadL1 implements Runnable {
    private int[] mas;

    public ThreadL1(int[] mas) {
        this.mas = mas;
    }

    @Override
    public void run() {
        // Проверяем, что массив не пустой
        if (mas.length == 0) {
            System.out.println("Массив пуст");
            return;
        }

        // Собираем только нечётные числа из массива
        int oddCount = 0;
        for (int num : mas) {
            if (num % 2 != 0) {
                oddCount++;
            }
        }

        // Если нечётных чисел меньше 2, то нечего обрабатывать
        if (oddCount < 2) {
            System.out.println("Недостаточно нечётных чисел для формирования пар");
            return;
        }

        // Массив для нечётных чисел
        int[] oddNumbers = new int[oddCount];
        int idx = 0;
        for (int num : mas) {
            if (num % 2 != 0) {
                oddNumbers[idx++] = num;
            }
        }

        // Считаем произведения пар нечётных чисел
        int pairCount = oddNumbers.length / 2;
        int[] products = new int[pairCount];

        System.out.println("Произведения пар нечётных чисел:");

        for (int i = 0; i < pairCount; i++) {
            // Выводим, какие конкретно числа умножаются
            System.out.println("Умножаем: " + oddNumbers[2 * i] + " * " + oddNumbers[2 * i + 1]);
            products[i] = oddNumbers[2 * i] * oddNumbers[2 * i + 1];
        }

        // Теперь считаем разницу между соседними произведениями
        int result = 0;
        System.out.println("Разницы между соседними произведениями:");
        for (int i = 0; i + 1 < pairCount; i++) {
            int diff = products[i] - products[i + 1];
            result += diff;
            // Выводим разницу, указывая конкретные числа
            System.out.println("Разница между произведениями (" + oddNumbers[2 * i] + " * " + oddNumbers[2 * i + 1] + ") и (" +
                    oddNumbers[2 * (i + 1)] + " * " + oddNumbers[2 * (i + 1) + 1] + ") = " + diff);
        }

        // Прибавляем первый элемент массива (если нужно)
        result += mas[0];

        // Выводим нечётные числа для наглядности
        System.out.println("Нечётные числа из массива:");
        for (int val : oddNumbers) {
            System.out.print(val + " ");
        }
        System.out.println();

        System.out.println("Выполненное действие первого потока: " + result);
    }
}
