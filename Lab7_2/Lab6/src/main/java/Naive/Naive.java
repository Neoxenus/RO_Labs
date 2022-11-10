package Naive;

import Matrix.Matrix;

public class Naive {
    private static final int MATRIX_SIZE = 1000;
    private static final boolean SHOW_RESULT = MATRIX_SIZE <=10;

    public static void main(String[] args) {
        Matrix a = new Matrix(MATRIX_SIZE);
        Matrix b = new Matrix(MATRIX_SIZE);
        a.initialise();
        b.initialise();
        if(SHOW_RESULT){
            a.show();
            b.show();
        }
        long start = System.currentTimeMillis();
        Matrix c = a.multiplyMatrix(b, MATRIX_SIZE);
        long stop = System.currentTimeMillis();
        System.out.println("Time: " + (stop - start) + "ms");
        if(SHOW_RESULT)
            c.show();
    }
}
