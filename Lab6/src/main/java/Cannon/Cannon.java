package Cannon;

import com.company.Matrix;
import mpi.MPI;
import mpi.Request;
import mpi.Status;

public class Cannon {

    /**
     * Generates a random matrix with numElements elements
     */
//    static void generateRandomMatrix(double[] matrix) {
//        Random random = new Random();
//        for (int i = 0; i < matrix.length; ++i) {
//            //values[i] = random.nextDouble();
//            matrix[i] = random.nextInt() % 100;
//        }
//    }

    /**
     * Returns the ID of the process in the mesh in column-major distribution according to
     * the coordinates provided and the size of the mesh.
     * The mesh has to be of size x size obligatorily.
     */
    static int getProcessNumber(int row, int column, int size) {
        if (row < 0)
            row += size;
        if (column < 0)
            column += size;
        if (row >= size)
            row -= size;
        if (column >= size)
            column -= size;

        return column * size + row;
    }

    /**
     * Calls the cannon function to solve a matrix-matrix multiplication problem
     *
     * Parameters:
     * p = number of processes
     * pr = process mesh row coordinate
     * pc = process mesh column coordinate
     * n = (real) size of matrices
     * C = final matrix to calculate
     * A = one of the matrices in the operation
     * B = the other matrix for the operation
     */
    static void cannon(int p, int pr, int pc, int n, Matrix C, Matrix A,
                       Matrix B) {

        /* Starting mesurements */
        int numLocalElements = n / p;
        Request[] requests = new Request[2];//0 - left; 1 - top
        Status rreq_bottom = new Status();
        Status rreq_right = new Status();
        int maxSteps = (int) Math.sqrt(p);

        //to exchange buffers
        //double[] aux;
        double[] auxA = new double[numLocalElements];
        double[] auxB = new double[numLocalElements];

        int left = getProcessNumber(pr, pc - 1, maxSteps);
        int right = getProcessNumber(pr, pc + 1, maxSteps);
        int top = getProcessNumber(pr - 1, pc, maxSteps);
        int bottom = getProcessNumber(pr + 1, pc, maxSteps);

        //for the first receiving
        int firstRecvA = getProcessNumber(pr, (pr + pc) % p, maxSteps);
        int firstRecvB = getProcessNumber((pr + pc) % p, pc, maxSteps);

        //for the first sending
        int distA = ((pr + pc) % p) - pc;
        distA = distA < 0 ? distA + maxSteps : distA;
        int firstSendA = getProcessNumber(pr, pc - distA, maxSteps);

        int distB = ((pr + pc) % p) - pr;
        distB = distB < 0 ? distB + maxSteps : distB;
        int firstSendB = getProcessNumber(pr - distB, pc, maxSteps);

        // sending my information of A
        requests[0] = MPI.COMM_WORLD.Isend(A.getValues(), 0, numLocalElements, MPI.DOUBLE,firstSendA,0);


        //receiving the information about A
        rreq_right = MPI.COMM_WORLD.Recv(auxA, 0, numLocalElements, MPI.DOUBLE, firstRecvA,0);

        //sending my information of B
        requests[1] = MPI.COMM_WORLD.Isend(B.getValues(), 0, numLocalElements, MPI.DOUBLE,firstSendB,0);

        //receiving the information about B
        rreq_bottom = MPI.COMM_WORLD.Recv(auxB, 0, numLocalElements, MPI.DOUBLE, firstRecvB,0);

        //we have to block after the sending operation!
        /* Note: for big values of n it starts to fail. It is necessary to determine
         * when the sending operation has finished. Otherwise the buffer is read and written
         * at the same time, producing errors that have been empirically found out looking at the
         * residual norm once the program was completed.
         */
        Request.Waitall(requests);

        //swapping buffers
        //aux = A.getValues();
        //A.setValues(auxA);
        auxA = A.getValues();

        //aux = B.getValues();
        //B.setValues(auxB);
        auxB = B.getValues();

        /* Finishing measurements */

        for (int step = 0; step < maxSteps; ++step) {
            for (int i = 0; i < A.getNRows(); i++) {
                for (int j = 0; j < B.getNCols(); j++) {
                    C.set(i, j, 0.0);
                    for (int k = 0; k < B.getNRows(); k++) {
                        C.incr(i, j, A.get(i, k) * B.get(k, j));
                    }
                }
            }
            //TESTING OUTPUT IN DIFFERENT ITERATIONS
//            File myObj = new File("output"+getProcessNumber(pr,pc,maxSteps)+".txt");
//            try {
//
//                    FileWriter myWriter = new FileWriter("output"+getProcessNumber(pr,pc,maxSteps)+".txt");
//                myWriter.write("======================================================================================\n");
//
//                for (int i = 0; i < C.getNRows(); i++) {
//                    for (int j = 0; j < C.getNCols(); j++) {
//                        myWriter.write(C.getValues()[i * C.getNCols() + j] + " ");
//                    }
//                    myWriter.write("\r\n");
//                }
//                myWriter.write("======================================================================================\n");
//                    myWriter.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            if (step < maxSteps - 1) {
                //t = clock();
                //System.out.println("ss");
                // sending my information of A
                requests[0] = MPI.COMM_WORLD.Isend(A.getValues(), 0, numLocalElements, MPI.DOUBLE, left, 0);
                //receiving the information about A
                rreq_right = MPI.COMM_WORLD.Recv(auxA, 0, numLocalElements, MPI.DOUBLE, right, 0);

                //sending my information of B
                requests[1] = MPI.COMM_WORLD.Isend(B.getValues(), 0, numLocalElements, MPI.DOUBLE, top, 0);
                //receiving the information about B
                rreq_bottom = MPI.COMM_WORLD.Recv(auxB, 0, numLocalElements, MPI.DOUBLE, bottom, 0);

                //waiting for the send to finish
                Request.Waitall(requests);

                //swapping buffers
               // aux = A.getValues();
                //A.setValues(auxA);
                //auxA = aux;

                //aux = B.getValues();
                //B.setValues(auxB);
                //auxB = aux;
            }
        }
    }
}
