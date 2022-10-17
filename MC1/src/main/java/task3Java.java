import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class task3Java {
    public static class Parking {
        private static final Semaphore SEMAPHORE = new Semaphore(5, true);

        public static class Car extends Thread {
            private final int carNumber;

            public Car(int carNumber) {
                super("Thread-" + carNumber);
                this.carNumber = carNumber;
            }

            @Override
            public void run() {
                System.out.println("Автомобіль "+carNumber+ " підїхав до парковки.");
                long start = System.currentTimeMillis();
                try {


                    if(SEMAPHORE.tryAcquire((long) (Math.random() * 6), TimeUnit.SECONDS)){
                        System.out.println("Автомобіль "+carNumber+" припаркувався на місці.");

                        Thread.sleep(5000);

                        System.out.println("Автомобіль " + carNumber + " залишив парковку.");
                        SEMAPHORE.release();
                    }else {
                        System.out.println("Автомобіль " + carNumber + " втомився чекати і залишив паркування.");

                    }
                } catch (InterruptedException ignored) {
                }
            }
        }
    }
    public static void main(String[] args) {
        for (int i = 1; i <= 7; i++) {
            new Thread(new Parking.Car(i )).start();
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
