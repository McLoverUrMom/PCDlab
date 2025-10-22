import java.util.LinkedList;

public class chinese {
    public static void chinese_start() {
     ThreadGroup main = new ThreadGroup("main");
     Thread.currentThread().getThreadGroup().list();
     LinkedList<Thread> threads = new LinkedList<>();
     ThreadGroup GN = new ThreadGroup(main, "GN");

      ThreadGroup GH = new ThreadGroup(GN, "GH");

      Thread Tha = new Thread(GH, new thred(), "Tha");
      Tha.setPriority(4);
      threads.add(Tha);

      Thread Thb = new Thread(GH, new thred(), "Thb");
      Thb.setPriority(3);
      threads.add(Thb);

      Thread Thc = new Thread(GH, new thred(), "Thc");
      Thc.setPriority(6);
      threads.add(Thc);

      Thread Thd = new Thread(GH, new thred(), "Thd");
      Thd.setPriority(3);
      threads.add(Thd);

      GH.list();

      Thread ThA = new Thread(GN, new thred(), "ThA");
      ThA.setPriority(3);
      threads.add(ThA);

      ThreadGroup GM = new ThreadGroup(main, "GM");

      Thread Th1 = new Thread(GM, new thred(), "Th1");
      Th1.setPriority(2);
      threads.add(Th1);

      Thread Th2 = new Thread(GM, new thred(), "Th2");
      Th2.setPriority(3);
      threads.add(Th2);

      Thread Th3 = new Thread(GM, new thred(), "Th3");
      Th3.setPriority(3);
      threads.add(Th3);

      GM.list();

      Thread Th1_main = new Thread(new thred(), "Th1");
      Th1_main.setPriority(8);
      threads.add(Th1_main);

      Thread Th2_main = new Thread(new thred(), "Th2");
      Th1_main.setPriority(3);
      threads.add(Th2_main);

      for (int i = 0; i < threads.toArray().length; i++) {
          threads.get(i).start();
      }

      main.list();
    }
}
