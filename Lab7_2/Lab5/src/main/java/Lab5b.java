import java.util.ArrayList;
import java.util.List;

public class Lab5b {
    private MyThread thread1;
    private MyThread thread2;
    private MyThread thread3;
    private MyThread thread4;
    private final MyCyclicBarrier barrier = new MyCyclicBarrier(4, getBarrierRunnable());

    private Runnable getBarrierRunnable(){
        return () -> {

            List<Integer> numbers = new ArrayList<>();
            numbers.add(numberOfAAndB(thread1.getLine()));
            numbers.add(numberOfAAndB(thread2.getLine()));
            numbers.add(numberOfAAndB(thread3.getLine()));
            numbers.add(numberOfAAndB(thread4.getLine()));
            System.out.println(thread1.getLine() + " " + numbers.get(0));
            System.out.println(thread2.getLine() + " " + numbers.get(1));
            System.out.println(thread3.getLine() + " " + numbers.get(2));
            System.out.println(thread4.getLine() + " " + numbers.get(3));
            System.out.println("-------------------------------------------------------------------------------------------");
            numbers = numbers.stream().sorted().toList();
            if(numbers.get(0).equals(numbers.get(2)) || numbers.get(1).equals(numbers.get(3))){
                thread1.interrupt();
                thread2.interrupt();
                thread3.interrupt();
                thread4.interrupt();
            }
        };
    }

    private int numberOfAAndB(String line) {
        int number = 0;
        for(int i = 0; i < line.length(); ++i){
            if(line.charAt(i) == 'A' || line.charAt(i) == 'B') {
                ++number;
            }
        }
        return number;
    }

    private static class MyCyclicBarrier {
        int initialParties;
        int partiesAwait;
        Runnable cyclicBarrierEvent;

        public MyCyclicBarrier(int parties, Runnable cyclicBarrierEvent) {
            initialParties = parties;
            partiesAwait = parties;
            this.cyclicBarrierEvent = cyclicBarrierEvent;
        }

        public synchronized void await() throws InterruptedException {
            partiesAwait--;
            if(partiesAwait > 0) {
                this.wait();
            } else {
                partiesAwait = initialParties;
                cyclicBarrierEvent.run();
                notifyAll();
            }
        }
    }

    private class MyThread extends Thread {
        private String line;
        private static final int LINE_SIZE = 100;

        public String getLine() {
            return line;
        }

        public MyThread() {
            line = randomLine();
        }

        private String randomLine(){
            StringBuilder line = new StringBuilder();
            String[] letters = {"A", "B", "C", "D"};
            for(int i = 0; i < LINE_SIZE; ++i){
                line.append(letters[(int) (Math.random() * 4)]);
            }
            return line.toString();
        }

        private void changeLine(){
            StringBuilder sb = new StringBuilder(line);
            List<Character> letters = List.of('A', 'B', 'C', 'D');
            for(int i = 0; i < LINE_SIZE; ++i){
                if(Math.random() <= 0.25){
                    sb.setCharAt(i, letters.get((letters.indexOf(sb.charAt(i)) + 2) % 4));
                }
            }
            line = sb.toString();
        }

        @Override
        public void run() {
            while (true){
                try {
                    barrier.await();
                } catch (InterruptedException e) {
                    break;
                }
                changeLine();
            }
        }
    }
    private void makeThreads(){
        thread1 = new MyThread();
        thread2 = new MyThread();
        thread3 = new MyThread();
        thread4 = new MyThread();
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
    }

    public static void main(String[] args) {
        new Lab5b().makeThreads();
    }
}
