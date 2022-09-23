import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Lab3a {
    private static class Bear extends Thread {
        public Jar jug;

        public Bear(Jar jug) {
            super("Bear-0");
            this.jug = jug;
        }

        @Override
        public void run() {
            while (true) jug.getHoneyFromJar();
        }
    }
    private static class Bee extends Thread{
        public Jar jug;

        public Bee(String name, Jar jug) {
            super(name);
            this.jug = jug;
        }

        @Override
        public void run() {
            while (true) jug.putHoneyToJar();
        }
    }
    private static class Jar {

        private static final int CAPACITY = 100;

        public AtomicInteger currVolumeOfHoney;
        public AtomicBoolean semaphore;

        public Jar() {
            currVolumeOfHoney = new AtomicInteger(0);
            semaphore = new AtomicBoolean(true);
        }

        public synchronized void putHoneyToJar() {
            if(semaphore.get()) {
                System.out.println(Thread.currentThread().getName() + " put -> " + currVolumeOfHoney.get());
                if (currVolumeOfHoney.get() >= CAPACITY) {
                    try {
                        notifyAll();
                        semaphore.set(false);
                        wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                currVolumeOfHoney.incrementAndGet();
            }
        }

        public synchronized void getHoneyFromJar() {
            System.out.println(Thread.currentThread().getName() + " get -> " + currVolumeOfHoney.get() );
            if(currVolumeOfHoney.get() < CAPACITY) {
                try {
                    semaphore.set(true);
                    wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.println(Thread.currentThread().getName() + " get -> " + currVolumeOfHoney.get() );
            while (currVolumeOfHoney.get() != 0) {
                currVolumeOfHoney.decrementAndGet();
            }
            //System.out.println("Get -> " + numberOfSwallows.get() + " " + Thread.currentThread().getName());
            notifyAll();
        }
    }
    private void makeThreads(int numberOfBees) {
        Jar jar = new Jar();
        new Bear(jar).start();
        for (int i = 0; i < numberOfBees; i++)
            new Bee("Bee-"+i, jar).start();
    }
    public static void main(String[] args) {
        new Lab3a().makeThreads(3);
    }
}
