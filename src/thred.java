class thred implements Runnable{
    public void run(){
        System.out.println("Поток" + Thread.currentThread().getName()+"работает");
        try {
            Thread.sleep(500);}
        catch (InterruptedException e) {
            System.out.println("Warning you stoopid");
        }
        System.out.println("Поток завершился");
    }
}
