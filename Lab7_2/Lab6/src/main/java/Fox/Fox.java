package Fox;

import Matrix.Matrix;
import mpi.Cartcomm;
import mpi.MPI;

public class Fox {
    private final static int MATRIX_SIZE = 1000;

    private static class Grid {
        int processCount;
        int dimensions;
        int row;
        int col;
        int gridRank;
        int worldRank;
        Cartcomm gridComm;
        Cartcomm rowComm;
        Cartcomm colComm;

        public Grid() {
            int[] dimensions = new int[2];
            boolean[] wrap_around = {false,true}; //new boolean[2];
            int[] coordinates;
            this.processCount = MPI.COMM_WORLD.Size();
            this.worldRank = MPI.COMM_WORLD.Rank();

//            if (!isPowerOfTwo(this.processCount)) {
//                if (this.worldRank == 0) {
//                    System.out.println("Error! Number of processors should be even power of two (1, 4, 16, 64, etc...)");
//                }
//                MPI.Finalize();
//                System.exit(1);
//            }

            this.dimensions = (int) Math.sqrt(this.processCount);
            dimensions[0] = this.dimensions;
            dimensions[1] = this.dimensions;

            this.gridComm = MPI.COMM_WORLD.Create_cart(dimensions, wrap_around, true);
            this.gridRank = this.gridComm.Rank();
            coordinates = this.gridComm.Coords(this.gridRank);
            this.row = coordinates[0];
            this.col = coordinates[1];

            boolean[] freeCoords = {false,true};//new boolean[2];
            this.rowComm = this.gridComm.Sub(freeCoords);

            freeCoords[0] = true;
            freeCoords[1] = false;
            this.colComm = this.gridComm.Sub(freeCoords);
        }
    }

    private static boolean isPowerOfTwo(int n) {
        int value = 0;
        int i = 0;
        while (value < n) {
            value = (int) Math.pow(2, i);
            if (value == n) {
                return i % 2 == 0;
            }
            i++;
        }
        return false;
    }

    private static void unpack_matrix(int[] buff, Matrix a, int size) {
        int k = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                a.set(i, j, buff[k]);
                k++;
            }
        }
    }

    private static void fox_multiply(int matrixSize, Grid grid, Matrix a, Matrix b, Matrix c)
    {
        //Status status;
        int blockSize = matrixSize / grid.dimensions;
        Matrix tempA = new Matrix(blockSize);
        int[] buff;
        int src = (grid.row + 1) % grid.dimensions;
        int dst = (grid.row + grid.dimensions - 1) % grid.dimensions;

        for (int stage = 0; stage < grid.dimensions; stage++)
        {
            int root = (grid.row + stage) % grid.dimensions;
            if (root == grid.col)
            {
                buff = a.packMatrix(blockSize);
                grid.rowComm.Bcast(buff, 0, blockSize * blockSize, MPI.INT, root);
                unpack_matrix(buff, a, blockSize);
                c.incrementMatrix(a.multiplyMatrix(b, blockSize));
            }
            else
            {
                buff = tempA.packMatrix(blockSize);
                grid.rowComm.Bcast(buff, 0, blockSize * blockSize, MPI.INT, root);
                unpack_matrix(buff, tempA, blockSize);
                c.incrementMatrix(tempA.multiplyMatrix(b, blockSize));
            }
            buff = b.packMatrix(blockSize);
            grid.colComm.Sendrecv_replace(buff, 0, blockSize * blockSize, MPI.INT, dst, 0, src, 0);
            unpack_matrix(buff, b, blockSize);
        }
    }


    public static void main(String[] args) {
        int blockSize;
        MPI.Init(args);
        long start = 0;
        Grid grid = new Grid();
        if (grid.worldRank == 0) {
            System.out.println("===================================================");
            System.out.println("Grid setup finished, total number of processors: " + grid.processCount);
        }
        grid.gridComm.Barrier();
        Matrix a = new Matrix(MATRIX_SIZE);
        Matrix b = new Matrix(MATRIX_SIZE);
        if (grid.worldRank == 0) {
            a.initialise();
            b.initialise();
            start = System.currentTimeMillis();
        }
        grid.gridComm.Barrier();
        MPI.COMM_WORLD.Bcast(a.getPackedValues(), 0, MATRIX_SIZE * MATRIX_SIZE, MPI.INT, 0);
        MPI.COMM_WORLD.Bcast(b.getPackedValues(), 0, MATRIX_SIZE * MATRIX_SIZE, MPI.INT, 0);
        grid.gridComm.Barrier();
        blockSize = MATRIX_SIZE / grid.dimensions;
        int baseRow = grid.row * blockSize;
        int baseCol = grid.col * blockSize;

        Matrix localA = new Matrix(blockSize);
        Matrix localB = new Matrix(blockSize);
        Matrix localC = new Matrix(blockSize);

        for (int i = baseRow; i < baseRow + blockSize; i++)
        {
            for (int j = baseCol; j < baseCol + blockSize; j++)
            {
                localA.set(i - baseRow, j - baseCol, a.get(i, j));
                localB.set(i - baseRow, j - baseCol, b.get(i, j));
            }
        }

        fox_multiply(MATRIX_SIZE, grid, localA, localB, localC);

        int[] resultBuff = new int[MATRIX_SIZE * MATRIX_SIZE];
        int[] localBuff = localC.packMatrix(blockSize);

        grid.gridComm.Gather(localBuff, 0, blockSize * blockSize, MPI.INT, resultBuff, 0, blockSize * blockSize, MPI.INT, 0);
        grid.gridComm.Barrier();
        if (grid.worldRank == 0) {
            Matrix c = new Matrix(MATRIX_SIZE);
            int k = 0;
            for (int bi = 0; bi < grid.dimensions; bi++) {
                for (int bj = 0; bj < grid.dimensions; bj++) {
                    for (int i = bi * blockSize; i < bi * blockSize + blockSize; i++) {
                        for (int j = bj * blockSize; j < bj * blockSize + blockSize; j++) {
                            c.set(i, j, resultBuff[k]);
                            k++;
                        }
                    }
                }
            }

            long stop = System.currentTimeMillis();
            System.out.println("Time: " + (stop - start) + "ms");
            if(MATRIX_SIZE<=4){
                a.show();
                b.show();
                c.show();
            }
        }
        MPI.Finalize();
    }
}
