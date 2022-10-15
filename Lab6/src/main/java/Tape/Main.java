package Tape;

import Matrix.Matrix;
import mpi.MPI;

public class Main {

    public static double start;

    public static final int MATRIX_SIZE = 1000;

    public static Matrix resultMatrix;

    public static void main(String[] args) {
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        Matrix a = new Matrix(MATRIX_SIZE);
        Matrix b = new Matrix(MATRIX_SIZE);
        Matrix c = new Matrix(MATRIX_SIZE);
        if (rank == 0) {
            a.initialise();
            b.initialise();
            // a.show();
            // b.show();
        }
        MPI.COMM_WORLD.Barrier();
        MPI.COMM_WORLD.Bcast(a.getPackedValues(), 0, a.getPackedValues().length, MPI.INT, 0);
        MPI.COMM_WORLD.Bcast(b.getPackedValues(), 0, b.getPackedValues().length, MPI.INT, 0);
        start = System.currentTimeMillis();
        for(int i = 0; i < MATRIX_SIZE / MPI.COMM_WORLD.Size(); ++i) {
            MatrixMult.parallelMatrixMultiply(a, b, c, rank + MPI.COMM_WORLD.Size() * i);
        }
        if (MPI.COMM_WORLD.Rank() == 0) {
            double stop = System.currentTimeMillis();
            System.out.println(stop - Main.start + "ms");
            //  resultMatrix.show();
        }
        MPI.Finalize();
    }
}