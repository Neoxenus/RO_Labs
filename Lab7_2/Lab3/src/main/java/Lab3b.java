import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class Lab3b {
    private static class Barber extends Thread{
        private final BarberShop barberShop;

        public Barber(String name, BarberShop barberShop) {
            super(name);
            this.barberShop = barberShop;
        }

        @Override
        public void run() {
            while(true) barberShop.haircut();
        }
    }
    private static class Visitor extends Thread{
        private final BarberShop barberShop;

        public Visitor(String name, BarberShop barberShop) {
            super(name);
            this.barberShop = barberShop;
        }

        @Override
        public void run() {
            barberShop.stayInQueue();
        }
    }
    private static class BarberShop{
        private final Semaphore isHaircutting;
        private final AtomicInteger visitorsInQueue;
        public BarberShop() {
            this.isHaircutting = new Semaphore(1);
            visitorsInQueue = new AtomicInteger(0);
        }

        public synchronized void haircut(){
            if(visitorsInQueue.get() == 0 && isHaircutting.availablePermits() == 1){
                try {
                    System.out.println(Thread.currentThread().getName() + " is sleeping now");
                    wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.println(Thread.currentThread().getName() + " is haircutting a visitor");
            try {
                Thread.sleep(100);
                if(visitorsInQueue.get() != 0) {
                    System.out.println(Thread.currentThread().getName() + " wake up another visitor");
                    notify();
                    visitorsInQueue.getAndDecrement();
                } else {
                    System.out.println(Thread.currentThread().getName() + " is sleeping without visitors");
                    isHaircutting.release();
                    wait();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        public synchronized void stayInQueue(){
            if(isHaircutting.tryAcquire()){
                System.out.println(Thread.currentThread().getName() + " wake up hairdresser and get haircut");
                notify();
                System.out.println(Thread.currentThread().getName() + " get haircut");
            } else {
                visitorsInQueue.getAndIncrement();
                try {
                    System.out.println(Thread.currentThread().getName() + " is sleeping in queue");
                    wait();
                    System.out.println(Thread.currentThread().getName() + " get haircut");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

        }
    }


    private void makeThreads(int numberOfVisitors) {
        BarberShop barberShop = new BarberShop();
        new Barber("Barber", barberShop).start();
        for(int i = 0; i < numberOfVisitors; ++i)
            new Visitor("Visitor-" + i,barberShop).start();
    }

    public static void main(String[] args) {
        new Lab3b().makeThreads(5);
    }
}