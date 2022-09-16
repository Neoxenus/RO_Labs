import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Thread.currentThread;

public class Lab2a {

    private static final int[][] forest;
    private static final int forestSize;
    private static final AtomicInteger counterOfArea;
    private static final AtomicBoolean isFound;

    static {
        forestSize = 100;
        forest = new int[forestSize][forestSize];
        counterOfArea = new AtomicInteger(0);
        isFound = new AtomicBoolean(false);
    }

    private static Runnable getRunnable() {
        return () -> {
            while (!Thread.interrupted()) {
                findingThroughForest(getNextAreaFromBackPack());
            }
        };
    }


    public static void main(String[] args) {
        int x = 99;//(int)(Math.random() * forestSize);
        int y = 1;//(int)(Math.random() * forestSize);

        forest[x][y] = 1;

        for (int i = 0; i < forestSize || !isFound.get(); ++i) {
            Thread thread = new Thread(getRunnable());
            thread.start();
        }
    }

    private static synchronized int getNextAreaFromBackPack() {
        if(!isFound.get() && counterOfArea.get() < forestSize) {
            System.out.println(counterOfArea.get());
            return counterOfArea.getAndIncrement();
        } else {
            currentThread().interrupt();
            return -1;
        }
    }

    private static void findingThroughForest(int row) {
        if(row == -1) {
            return;
        }
        for(int i = 0; i < forestSize; ++i) {
            if(forest[row][i] == 1) {
                isFound.set(true);
                System.out.println("Y:" + row + "; X:" + i);
                break;
            }
        }
    }
}
