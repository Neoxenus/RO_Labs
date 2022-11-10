package Tape;

import Matrix.Matrix;
import mpi.MPI;

import java.util.Arrays;

public class MatrixMult {

    private MatrixMult() {}

    public static void parallelMatrixMultiply(Matrix a, Matrix b, Matrix c, int rank) {
        // System.out.println("Rank " + rank);
        int size = MPI.COMM_WORLD.Size();
        //int rank = MPI.COMM_WORLD.Rank();
        int matrixSize = c.getSize();

        for(int i = 0, j = rank; i < matrixSize; ++i, ++j) {
            if(j >= matrixSize) {
                j = 0;
            }
            for(int k = 0; k < matrixSize; ++k) {
                c.increment(i, j, a.get(i, k) * b.get(k, j));
            }
        }

        int[] result = new int[matrixSize * matrixSize * size];

        MPI.COMM_WORLD.Gather(
                c.getPackedValues(),
                0,
                matrixSize * matrixSize,
                MPI.INT,
                result,
                0,
                matrixSize * matrixSize,
                MPI.INT,
                0
        );


        if (rank >= Main.MATRIX_SIZE - MPI.COMM_WORLD.Size()) {
            Main.resultMatrix = convertToMatrix(result, matrixSize);
        }

    }

    private static Matrix convertToMatrix(int[] result, int matrixSize) {
        int size = MPI.COMM_WORLD.Size();
        int[][] arr = new int[size][matrixSize * matrixSize];
        for(int i = 0; i < size; ++i) {
            arr[i] = Arrays.copyOfRange(result, i * result.length / size, (i + 1) * result.length / size);
        }
        int[] array = new int[matrixSize * matrixSize];
        for(int i = 0; i < matrixSize * matrixSize; ++i) {
            for(int j = 0; j < size; ++j) {
                array[i] = Math.max(array[i], arr[j][i]);
            }
        }
        return new Matrix(array);
    }
}
