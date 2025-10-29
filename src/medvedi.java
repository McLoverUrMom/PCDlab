import java.util.ArrayList;
import java.util.List;

public class medvedi {

        static class MyThread extends Thread {
            public MyThread(ThreadGroup group, String name) {
                super(group, name);
            }

            @Override
            public void run() {

                String gname = (getThreadGroup() != null) ? getThreadGroup().getName() : "null";
                System.out.printf("I am thread '%s', group='%s', priority=%d%n",
                        getName(), gname, getPriority());

                try {
                    Thread.sleep(5_000);
                } catch (InterruptedException ignored) {
                }
            }
        }

        public static void medvedi_start() {
            ThreadGroup main = new ThreadGroup("Main");

            ThreadGroup G7 = new ThreadGroup(main, "G7");

            ThreadGroup G3 = new ThreadGroup(G7, "G3");

            List<Thread> threads = new ArrayList<>();

            MyThread Tha = new MyThread(G3, "Tha"); Tha.setPriority(6); threads.add(Tha);
            MyThread Thb = new MyThread(G3, "Thb"); Thb.setPriority(3); threads.add(Thb);
            MyThread Thc = new MyThread(G3, "Thc"); Thc.setPriority(6); threads.add(Thc);
            MyThread Thd = new MyThread(G3, "Thd"); Thd.setPriority(3); threads.add(Thd);

            MyThread ThA = new MyThread(G7, "ThA"); ThA.setPriority(7); threads.add(ThA);
            MyThread ThB = new MyThread(G7, "ThB"); ThB.setPriority(6); threads.add(ThB);

            MyThread Th2_main = new MyThread(main, "Th2"); Th2_main.setPriority(3); threads.add(Th2_main);

            ThreadGroup G2 = new ThreadGroup(main, "G2");
            MyThread Th1 = new MyThread(G2, "Th1"); Th1.setPriority(7); threads.add(Th1);
            MyThread Th2 = new MyThread(G2, "Th2"); Th2.setPriority(3); threads.add(Th2);
            MyThread Th3 = new MyThread(G2, "Th3"); Th3.setPriority(3); threads.add(Th3);

            for (Thread t : threads) t.start();

            try { Thread.sleep(200); } catch (InterruptedException ignored) {}

            int estimatedSize = main.activeCount() * 2 + 10;
            Thread[] all = new Thread[estimatedSize];
            int found = main.enumerate(all, true);
            System.out.printf("%n--- Перечисление потоков в группе '%s' и её подгруппах (найдено: %d) --- %n", main.getName(), found);
            for (int i = 0; i < found; i++) {
                Thread t = all[i];
                System.out.printf("Name='%s', Group='%s', Priority=%d, isAlive=%b%n",
                        t.getName(),
                        (t.getThreadGroup() != null ? t.getThreadGroup().getName() : "null"),
                        t.getPriority(),
                        t.isAlive());
            }

            System.out.println("\n--- main.list() output ---");
            main.list();

            for (Thread t : threads) {
                try {
                    t.join();
                } catch (InterruptedException ignored) {}
            }

            System.out.println("\nВсе потоки завершены.");
        }

        public static void main(String[] args) {
            medvedi_start();
        }

}
