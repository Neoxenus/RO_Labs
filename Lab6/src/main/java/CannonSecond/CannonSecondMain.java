package CannonSecond;

import mpi.Cartcomm;
import mpi.MPI;
import mpi.ShiftParms;

/**
 * MPJ_HOME=C:\mpj-v0_44\mpj-v0_44
 * -jar C:\mpj-v0_44\mpj-v0_44\lib\starter.jar -np 4
 */
public class CannonSecondMain {
    private final static int SIZE = 4;

    public static void main(String[] args) {
        int rank, size;
        char ch;
        double[] A = new double[SIZE];
        double[] B = new double[SIZE];
        double[] C = new double[SIZE];
        double[] a = new double[SIZE];
        double[] b = new double[SIZE];
        double[] c = new double[SIZE];
        MPI.Init(args);
        size = MPI.COMM_WORLD.Size();
        rank = MPI.COMM_WORLD.Rank();
        if (rank == 0) {
            for (int i = 0; i < A.length; i++) {
                //values[i] = random.nextDouble();
                A[i] = (int)(Math.random() * 10);
            }
            for (int i = 0; i < B.length; i++) {
                //values[i] = random.nextDouble();
                B[i] = (int)(Math.random() * 10);
            }
        }
            //MPI.COMM_WORLD.Bcast(row, 0, 1, MPI.INT, 0);
            //MPI_Bcast(&row,1,MPI_INT,0,MPI_COMM_WORLD);
            boolean periods[] = {true, true}; //both vertical and horizontal movement;
            int dims[] = {(int) Math.sqrt(SIZE), (int) Math.sqrt(SIZE)};
            int[] coords =  new int[2]; /* 2 Dimension topology so 2 coordinates */
            //ShiftParms right = new ShiftP;     // neighbor ranks
            //ShiftParms down = 0;     // neighbor ranks
            int left = 0;
            //int down = 0;
            int up = 0;
            Cartcomm cart_comm;
            cart_comm = MPI.COMM_WORLD.Create_cart( dims, periods, true);
            //MPI.COMM_WORLD.Create_cart( 2, dims, periods, 1,  );
            cart_comm.Scatter(A, 0, 1, MPI.DOUBLE, a,0, 1, MPI.DOUBLE, 0);
            //MPI_Scatter(A, 1, MPI_FLOAT, & a, 1, MPI_FLOAT, 0, cart_comm);
            //MPI_Scatter(B, 1, MPI_FLOAT, & b, 1, MPI_FLOAT, 0, cart_comm);
            cart_comm.Scatter(B, 0, 1, MPI.DOUBLE, b,0, 1, MPI.DOUBLE, 0);

            cart_comm.Rank();
            coords = cart_comm.Coords(rank);
            //MPI_Cart_coords(cart_comm, rank, 2, coords);
            //MPI_Cart_shift(cart_comm, 1, coords[0], & left,&right);
            ShiftParms leftRight = cart_comm.Shift(1, coords[0]);
            ShiftParms upDown = cart_comm.Shift(0, coords[1]);
            //MPI_Cart_shift(cart_comm, 0, coords[1], & up,&down);
            cart_comm.Sendrecv_replace(a, 0, 1, MPI.DOUBLE, leftRight.rank_source, 11, leftRight.rank_dest,11);
            cart_comm.Sendrecv_replace(b, 0, 1, MPI.DOUBLE, upDown.rank_source, 11, upDown.rank_dest,11);
            //MPI_Sendrecv_replace( & a, 1, MPI_FLOAT, left, 11, right, 11, cart_comm, MPI_STATUS_IGNORE);
            //MPI_Sendrecv_replace( & b, 1, MPI_FLOAT, up, 11, down, 11, cart_comm, MPI_STATUS_IGNORE);
            c[0] = c[0] + a[0] * b[0];
            for (int i = 1; i < a.length; i++) {
                cart_comm.Shift(1,1);
                leftRight = cart_comm.Shift(1, 1);
                upDown = cart_comm.Shift(0, 1);
               // MPI_Cart_shift(cart_comm, 0, 1, & up,&down);
                cart_comm.Sendrecv_replace(a, 0, 1, MPI.DOUBLE, leftRight.rank_source, 11, leftRight.rank_dest,11);
                cart_comm.Sendrecv_replace(b, 0, 1, MPI.DOUBLE, upDown.rank_source, 11, upDown.rank_dest,11);
               // MPI_Sendrecv_replace( & a, 1, MPI_FLOAT, left, 11, right, 11, cart_comm, MPI_STATUS_IGNORE);
               // MPI_Sendrecv_replace( & b, 1, MPI_FLOAT, up, 11, down, 11, cart_comm, MPI_STATUS_IGNORE);
                c[i] = c[i] + a[i] * b[i];
            }
            //C = ( float*)calloc(sizeof( float),row * row);
            cart_comm.Gather( c,0, 1, MPI.DOUBLE, C, 0,1, MPI.DOUBLE, 0);
            //MPI_Gather( & c, 1, MPI_FLOAT, C, 1, MPI_FLOAT, 0, cart_comm);
            if (rank == 0) {
                System.out.println("======================================================================================");
                for (int i = 0; i < (int)Math.sqrt(SIZE); i++) {
                    for (int j = 0; j < (int)Math.sqrt(SIZE); j++) {
                        System.out.print(A[(int) (i * (int)Math.sqrt(SIZE) + j)] + " ");
                    }
                    System.out.println();
                }
                System.out.println("======================================================================================");
                System.out.println("======================================================================================");
                for (int i = 0; i < (int)Math.sqrt(SIZE); i++) {
                    for (int j = 0; j < (int)Math.sqrt(SIZE); j++) {
                        System.out.print(B[ (i * (int)Math.sqrt(SIZE) + j)] + " ");
                    }
                    System.out.println();
                }
                System.out.println("======================================================================================");
                System.out.println("======================================================================================");
                for (int i = 0; i < (int)Math.sqrt(SIZE); i++) {
                    for (int j = 0; j < (int)Math.sqrt(SIZE); j++) {
                        System.out.print(C[(i * (int)Math.sqrt(SIZE) + j)] + " ");
                    }
                    System.out.println();
                }
                System.out.println("======================================================================================");
            }
            MPI.Finalize();
        }
    }

