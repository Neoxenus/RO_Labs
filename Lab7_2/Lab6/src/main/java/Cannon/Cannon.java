package Cannon;

import mpi.Datatype;
import mpi.MPI;

import static java.lang.Math.sqrt;

public class Cannon {
    private static final int MATRIX_SIZE = 1000;
    private static final boolean SHOW_RESULT = MATRIX_SIZE <=10;

    private static int[][] MatrixA, MatrixB, MatrixC;
    private static int[] blockA, blockB, blockC, tempA, tempB;
    private static int sizeOfBlock, sizeOfBlock2, numProcess, sqrtNumProcess;

    public static void main(String[] args) {
        MPI.Init(args);

        numProcess = MPI.COMM_WORLD.Size();
        int myRank = MPI.COMM_WORLD.Rank();

        sqrtNumProcess = (int) sqrt(numProcess);
        if (sqrtNumProcess * sqrtNumProcess != numProcess) {
            if (myRank == 0)
                System.out.println("Number of processors is not a quadratic number!\n");
            MPI.Finalize();
            return;
        }
        sizeOfBlock = MATRIX_SIZE / sqrtNumProcess;
        sizeOfBlock2 = sizeOfBlock * sizeOfBlock;
        int myCol =  myRank % sqrtNumProcess;
        int myRow = (myRank - myCol) / sqrtNumProcess;

        blockA = new int[sizeOfBlock2];
        blockB = new int[sizeOfBlock2];
        blockC = new int[sizeOfBlock2];
        for(int i = 0; i< sizeOfBlock2; ++i)
            blockC[i] = 0;
        tempA = new int[sizeOfBlock2];
        tempB = new int[sizeOfBlock2];

        long start = 0;
        if (myRank == 0) {

            MatrixA = new int[MATRIX_SIZE][MATRIX_SIZE];
            MatrixB = new int[MATRIX_SIZE][MATRIX_SIZE];
            MatrixC = new int[MATRIX_SIZE][MATRIX_SIZE];
            randomAB();
            if(SHOW_RESULT){
                print(MatrixA);
                print(MatrixB);
            }
            start = System.currentTimeMillis();
            scatterAB();
        } else {
            MPI.COMM_WORLD.Recv(blockA,0, sizeOfBlock2, MPI.INT, 0 , 1);
            MPI.COMM_WORLD.Recv(blockB, 0, sizeOfBlock2, MPI.INT, 0 , 2);
        }
        initAlignment(myRow,myCol);

        mainShift(myRow,myCol);

        if(myRank == 0)
            togetherResult();
         else
            MPI.COMM_WORLD.Send(blockC,0, sizeOfBlock2,MPI.INT,0,1);

        MPI.COMM_WORLD.Barrier();
        if(myRank == 0){
            long stop = System.currentTimeMillis();
            System.out.println("Time: " + (stop - start) + "ms");
            if(SHOW_RESULT)
                print(MatrixC);
        }


        MPI.Finalize();
    }

    private static int getIndex(int row, int col, int sqrtNum) {
        return ((row + sqrtNum) % sqrtNum) * sqrtNum + (col + sqrtNum) % sqrtNum;
    }

    private static void randomAB() {
        for(int i = 0; i < MATRIX_SIZE; ++i)
            for(int j = 0; j < MATRIX_SIZE; ++j) {
                MatrixA[i][j] = (int) (Math.random()*10);
                MatrixB[i][j] = (int) (Math.random()*10);
                MatrixC[i][j] = 0;
            }
    }


    private static void scatterAB() {
        int l;
        int pIMin,pIMax,pJMin,pJMax;
        for(int k=0; k< numProcess; ++k) {
            pJMin = (k % sqrtNumProcess) * sizeOfBlock;
            pJMax = (k % sqrtNumProcess + 1) * sizeOfBlock - 1;
            pIMin = (k - (k % sqrtNumProcess))/ sqrtNumProcess * sizeOfBlock;
            pIMax = ((k - (k % sqrtNumProcess))/ sqrtNumProcess + 1) * sizeOfBlock - 1;
            l = 0;

            for(int i=pIMin; i<=pIMax; ++i) {
                for(int j=pJMin; j<=pJMax; ++j) {
                    tempA[l] = MatrixA[i][j];
                    tempB[l] = MatrixB[i][j];
                    ++l;
                }
            }

            if(k==0) {
                System.arraycopy(tempA, 0, blockA, 0, sizeOfBlock2);
                System.arraycopy(tempB, 0, blockB, 0, sizeOfBlock2);
            } else {
                MPI.COMM_WORLD.Send(tempA, 0, sizeOfBlock2, MPI.INT, k, 1);
                MPI.COMM_WORLD.Send(tempB,0, sizeOfBlock2, MPI.INT, k, 2);
            }
        }
    }

    private static void initAlignment(int myRow, int myCol) {

        MPI.COMM_WORLD.Sendrecv(blockA, 0, sizeOfBlock2, MPI.INT, getIndex(myRow,myCol-myRow, sqrtNumProcess), 1,
                tempA, 0, sizeOfBlock2, MPI.INT, getIndex(myRow,myCol+myRow, sqrtNumProcess), 1);
        System.arraycopy(tempA, 0, blockA, 0, sizeOfBlock2);

        MPI.COMM_WORLD.Sendrecv(blockB, 0, sizeOfBlock2, MPI.INT, getIndex(myRow-myCol,myCol, sqrtNumProcess), 1,
                tempB, 0, sizeOfBlock2, MPI.INT, getIndex(myRow+myCol,myCol, sqrtNumProcess), 1);
        System.arraycopy(tempB,0, blockB,0, sizeOfBlock2);

    }

    private static void mainShift(int myRow, int myCol) {
        for(int l=0; l< sqrtNumProcess; ++l) {

            for(int i=0; i< sizeOfBlock; ++i)
                for(int j=0; j< sizeOfBlock; ++j)
                    for(int k=0; k< sizeOfBlock; ++k)
                        blockC[i* sizeOfBlock +j] += blockA[i * sizeOfBlock + k] * blockB[k * sizeOfBlock + j];
            MPI.COMM_WORLD.Sendrecv_replace(blockA, 0, sizeOfBlock2, MPI.INT, getIndex(myRow, myCol-1, sqrtNumProcess),
                    1,getIndex(myRow, myCol + 1, sqrtNumProcess),1);
            MPI.COMM_WORLD.Sendrecv_replace(blockB, 0, sizeOfBlock2, MPI.INT, getIndex(myRow-1, myCol, sqrtNumProcess),
                    1,getIndex(myRow+1, myCol, sqrtNumProcess),1);

        }
    }

    private static void togetherResult() {
        int i2,j2;
        int pIMin,pIMax,pJMin,pJMax;

        for (int i = 0; i < sizeOfBlock; ++i)
            System.arraycopy(blockC, i * sizeOfBlock, MatrixC[i], 0, sizeOfBlock);

        for (int k = 1; k < numProcess; ++k) {

            MPI.COMM_WORLD.Recv(blockC, 0, sizeOfBlock2, MPI.INT, k, 1);
            pJMin = (k % sqrtNumProcess) * sizeOfBlock;
            pJMax = (k % sqrtNumProcess + 1) * sizeOfBlock - 1;
            pIMin =  (k - (k % sqrtNumProcess)) / sqrtNumProcess * sizeOfBlock;
            pIMax = ((k - (k % sqrtNumProcess)) / sqrtNumProcess + 1) * sizeOfBlock -1;

            i2=0;

            for(int i=pIMin; i<=pIMax; ++i) {
                j2=0;
                for(int j=pJMin; j<=pJMax; ++j) {
                    MatrixC[i][j] = blockC[i2* sizeOfBlock +j2];
                    j2++;
                }
                i2++;
            }
        }
    }


    private static void print(int[][] m) {
        System.out.println("=================================================================================");
        for(int i = 0; i< MATRIX_SIZE; ++i) {
            for(int j = 0; j< MATRIX_SIZE; ++j)
                System.out.printf("%15d    ",m[i][j]);
            System.out.print("\n");
        }
        System.out.println("=================================================================================");
    }
}

