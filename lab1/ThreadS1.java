class ThreadS1 implements Runnable {
    private int[] mas = new int[100];
    public ThreadS1(int mas[]) {
        this.mas = mas;
    }
    public void run() {
        int evenNumberCount = 0;
        for (int i = 0; i < 100; i++){
            if (mas[i]%2 == 0){
                evenNumberCount++; // находит количество четных чисел
            }
        }
        int[] evenNumbers = new int[evenNumberCount];
        int index = 0;
        for (int i = 0; i <100; i++) {
            if (mas[i] % 2 == 0) {
                evenNumbers[index] = mas[i];// если элемент mas[i] чётный, то сохраняем его в массив evenNumbers
                index++;
            }
        }
        int firstEvenNumber = 0;
        int sum = 0;
        for (int i = 0; i < evenNumbers.length; i++){
            sum += (firstEvenNumber + ( firstEvenNumber+2));
        }

        for (int i = 0; i < evenNumbers.length; i++){
            System.out.print("Четные числа: " + evenNumbers[i]);
        }
        System.out.println("Выполненное действие первого потока" + sum);
    }
}