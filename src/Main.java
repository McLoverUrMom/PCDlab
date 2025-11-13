import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        Store sklad = new Store();

        Producer p1 = new Producer(sklad);
        p1.setDaemon(true);
        p1.setName("Производитель 1");

        Producer p2 = new Producer(sklad);
        p2.setDaemon(true);
        p2.setName("Производитель 2");

        Producer p3 = new Producer(sklad);
        p3.setDaemon(true);
        p3.setName("Производитель 3");

        Producer p4 = new Producer(sklad);
        p4.setDaemon(true);
        p4.setName("Производитель 4");

        Consumer c1 = new Consumer(sklad);
        c1.setName("1-й потребитель");

        Consumer c2 = new Consumer(sklad);
        c2.setName("2-й потребитель");

        Consumer c3 = new Consumer(sklad);
        c3.setName("3-й потребитель");

        p1.start();
        p2.start();
        p3.start();
        p4.start();
        c1.start();
        c2.start();
        c3.start();

        while(c1.isAlive() || c2.isAlive() || c3.isAlive()) {}

        System.out.println("Все потоки завершены");
    }
}


class Store {
    ArrayList<String> stockList=new ArrayList<String>();

    public synchronized void get(String str) {
        while (stockList.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {}
        }

        String item = stockList.getLast();
        stockList.remove(stockList.size() - 1);

        String vowels = item.replaceAll("(?i)[^aeiouаyеёиоуыэюя]", "");
        System.out.println(str + " взял со склада строку: " + item);

        System.out.println(str + " извлёк гласные: " + vowels);

        if (!stockList.isEmpty()) {
            System.out.print("На складе имеется " + stockList.size() + " единиц -> ");
            for (String s : stockList) System.out.print(s + " ");
            System.out.println();
        } else {
            System.out.println("Склад пуст");
        }
        notifyAll();
    }

    public synchronized void put(String str,String a, String b) {
        while (stockList.size()>=10) {  System.out.println(">>> " + str + " хочет положить, но склад ПОЛОН (" + stockList.size() + "). Ждёт...");
            try {
                wait();
            } catch (InterruptedException e) { }
        }
        System.out.print(str + " поместил в хранилище две строки: ");
        stockList.add(a);
        System.out.print(stockList.get(stockList.size()-1)+", ");
        stockList.add(b);
        System.out.println(stockList.get(stockList.size()-1));
        if(!stockList.isEmpty()){
            System.out.print("На складе имеется " + stockList.size()+" единиц -> ");
            for(String letters : stockList){
                System.out.print(letters+ " ");
            }
            System.out.println(" ");}
        else{
            System.out.println("Склад пуст");
        }
        notifyAll();
    }
}

class Producer extends Thread{

    Store s;

    public Producer(Store s) {
        this.s = s;
    }
    @Override
    public void run() {
        String[] words ={"aboba","congo","java","laupa","pupa"};
        while(true){
            String a = words[(int)(Math.random()* words.length)];
            String b = words[(int)(Math.random()* words.length)];
            s.put(getName(),a, b);
        }
    }
}

class Consumer extends Thread{

    Store s;
    public Consumer(Store s) {
        this.s = s;
    }
    @Override
    public void run() {
        int cons = 0;
        for(int i = 0; i < 3; i++){
            s.get(getName());
        }
        System.out.println(getName() + " взял 3 буквы. Поток завершен");
    }
}