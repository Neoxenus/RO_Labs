import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Thread.currentThread;

public class Lab2a {

    private static final int[][] forest;
    private static final AtomicInteger counterOfArea;
    private static final AtomicBoolean isFound;

    static {
        forest = new int[100][100];
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
        int x = (int)(Math.random() * 100);
        int y = (int)(Math.random() * 100);

        forest[x][y] = 1;

        Thread thread1 = new Thread(getRunnable());
        Thread thread2 = new Thread(getRunnable());
        Thread thread3 = new Thread(getRunnable());


        thread1.start();
        thread2.start();
        thread3.start();

    }

    private static synchronized int getNextAreaFromBackPack() {
        if(!isFound.get()) {
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
        for(int i = 0; i < 100; ++i) {
            if(forest[row][i] == 1) {
                isFound.set(true);
                System.out.println("Y:" + row + "; X:" + i);
                break;
            }
        }
    }
}
