package com.company;

import java.util.Random;

public final class Matrix {

    private final double[] values;
    private final int nrows;
    private final int ncols;

    public Matrix(final int setNrows, final int setNcols) {
        this.nrows = setNrows;
        this.ncols = setNcols;
        this.values = new double[nrows * ncols];
    }
    public void setValues(double[] newValues){
        System.arraycopy(newValues, 0, values, 0, values.length);
    }
    public void set(final int row, final int col, final double val) {
        values[row * ncols + col] = val;
    }

    public void incr(final int row, final int col, final double val) {
        values[row * ncols + col] += val;
    }

    public double get(final int row, final int col) {
        return values[row * ncols + col];
    }

    public int getNRows() {
        return nrows;
    }

    public int getNCols() {
        return ncols;
    }

    public double[] getValues() {
        return values;
    }

    public void init() {
        Random random = new Random();
        for (int i = 0; i < values.length; i++) {
            //values[i] = random.nextDouble();
            values[i] = random.nextInt() % 10;
        }
    }

    public void show() {
        System.out.println("======================================================================================");
        for (int i = 0; i < nrows; i++) {
            for (int j = 0; j < ncols; j++) {
                System.out.print(values[i * ncols + j] + " ");
            }
            System.out.println();
        }
        System.out.println("======================================================================================");
    }
}
