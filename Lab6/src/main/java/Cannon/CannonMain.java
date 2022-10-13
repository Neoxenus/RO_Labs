package Cannon;

import mpi.Cartcomm;
import mpi.Comm;
import mpi.MPI;

import static Cannon.Cannon.*;
import com.company.Matrix;


class CannonMain{

    //    /**
//     * Computes the residual norm
//     */
//    private static double computeResidualNorm(double[] serialC, double[] parallelC) {
//        double norm = 0.0;
//        for (int i = 0; i < n; ++i) {
//            double num = serialC[i] - parallelC[i];
//            norm += num * num;
//        }
//        return Math.sqrt(norm);
//    }

    /**
     * Usage:
     * ./program size
     *
     * every process will deal with a size x size sub-matrix
     */
    public static void main(String[] args) {
        MPI.Init(args);
        int size = 2;
        long start = 0;
        int repetitions = 1;

        int me = MPI.COMM_WORLD.Rank();
        int np = MPI.COMM_WORLD.Size();


        int n = np * size * size;
        //System.out.println(np + " " + n);

//generating C

//generating A
            Matrix A = new Matrix(size,size);
            A.init();

//generating B
        Matrix B = new Matrix(size,size);
        B.init();

        //we are going to use a column-major distribution of processes
        /*
         * For example, for 16 processors
         * 0	4	8	12
         * 1	5	9	13
         * 2	6	10	14
         * 3	7	11	15
         */
        int dist = (int) Math.sqrt(np);
        int rowCoordinate = me % dist;
        int columnCoordinate = me / dist;
//        Cartcomm cartcomm = MPI.COMM_WORLD.Create_cart(new int[], new boolean[], false);
//        Comm grid = MPI.COMM_NULL;
//        cartcomm.C
//        grid = cartcomm;
//        //MPI.Co
//        cartcomm.
//        //grid = cartcomm.Sub()
//
//
//        grid.
        //starting repetitions
        Matrix copyA = new Matrix(size,size);
        Matrix copyB = new Matrix(size,size);
        Matrix copyC = new Matrix(size,size);

        for (int i = 0; i < repetitions; ++i) {
            copyA.setValues(A.getValues());
            copyB.setValues(B.getValues());
            //previous synchronization
            MPI.COMM_WORLD.Barrier();

            //measuring the time
            //clock_t t = clock();

            //calling the cannon function
            if (me == 0) {
                start = System.currentTimeMillis();
            }
            cannon(np, rowCoordinate, columnCoordinate, n, copyC, copyA,
                    copyB);
            //System.out.println("end");



            MPI.COMM_WORLD.Barrier();
            if(me == 0){
                long stop = System.currentTimeMillis();
                System.out.println("Time: " + (stop - start) + "ms");
                copyA.show();
                copyB.show();
                copyC.show();
            }

        }
        MPI.Finalize();
    }
}