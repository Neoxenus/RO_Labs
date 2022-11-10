import java.util.Arrays;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;


public class Lab5a extends Thread {
    enum Direction {
        LEFT,
        RIGHT;

        public static Direction[] generateRecruits() {
            Direction[] recruits = new Direction[50];
            for(int i = 0; i < recruits.length; ++i) {
                recruits[i] = Math.random() < 0.5 ? LEFT : RIGHT;
            }
            return recruits;
        }


        public static int checkIfLineLooksAtSameDirection(Direction[] recruits) {
            int counter = 0;
            for(int i = 0; i < recruits.length - 1; ++i) {
                if(recruits[i] != recruits[i + 1]) {
                    ++counter;
                }
            }
            return counter;
        }

        @Override
        public String toString() {
            return (this.equals(Direction.LEFT) ?"<":">");
        }
    }
    private static class Line extends Thread {
        private final Lab5a.Direction[] recruits;

        public boolean leftBorderChanged;
        public boolean rightBorderChanged;


        public Line() {
            recruits = Direction.generateRecruits();
        }

        public Lab5a.Direction[] getRecruits() {
            return recruits;
        }

        public boolean isLeftBorderChanged() {
            return leftBorderChanged;
        }

        public boolean isRightBorderChanged() {
            return rightBorderChanged;
        }

        @Override
        public void run() {
            while(true) {
                try {
                    BARRIER.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    break;
                }
                changeDirection();
            }
        }

        public void changeDirection() {
            leftBorderChanged = false;
            rightBorderChanged = false;
            for(int i = 0; i < recruits.length - 1; ++i) {
                if(recruits[i] == Direction.RIGHT && recruits[i + 1] == Direction.LEFT) {
                    recruits[i] = Direction.LEFT;
                    recruits[i + 1] = Direction.RIGHT;
                    if(i == 0) {
                        leftBorderChanged = true;
                    }
                    if(i == 48) {
                        rightBorderChanged = true;
                    }
                    ++i;
                }
            }
        }

    }
    public static final CyclicBarrier BARRIER = new CyclicBarrier(4, new Lab5a());
    private static Line line1;
    private static Line line2;
    private static Line line3;
    private static Line line4;


    @Override
    public void run() {
        checkBorders(line1, line2);
        checkBorders(line2, line3);
        checkBorders(line3, line4);

        System.out.println("Line1 : " + Arrays.toString(line1.getRecruits()));
        System.out.println("Line2 : " + Arrays.toString(line2.getRecruits()));
        System.out.println("Line3 : " + Arrays.toString(line3.getRecruits()));
        System.out.println("Line4 : " + Arrays.toString(line4.getRecruits()));
        System.out.println("-----------------------------------");

        if(Direction.checkIfLineLooksAtSameDirection(line1.getRecruits())
                + Direction.checkIfLineLooksAtSameDirection(line2.getRecruits())
                + Direction.checkIfLineLooksAtSameDirection(line3.getRecruits())
                + Direction.checkIfLineLooksAtSameDirection(line4.getRecruits()) <= 1) {
            line1.interrupt();
            line2.interrupt();
            line3.interrupt();
            line4.interrupt();
        }
    }

    private void checkBorders(Line line1, Line line2) {
        if(!line1.isRightBorderChanged() && !line2.isLeftBorderChanged()
                && line1.getRecruits()[49] == Direction.RIGHT && line2.getRecruits()[0] == Direction.LEFT) {
            line1.getRecruits()[49] = Direction.LEFT;
            line2.getRecruits()[0] = Direction.RIGHT;
        }
    }

    public static void main(String[] args) {
        line1 = new Line();
        line2 = new Line();
        line3 = new Line();
        line4 = new Line();
        line1.start();
        line2.start();
        line3.start();
        line4.start();
    }
}


