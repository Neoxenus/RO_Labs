package com.company;

import mpi.MPI;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class Main {

    public static void main(String[] args) {
        //Specify the location of txt file to write the logs
        String pathToLogFile = "";
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        long start = 0;
        //Matrix a = new Matrix(1500, 1000);
        //Matrix b = new Matrix(1000, 500);
        //Matrix c = new Matrix(1500, 500);
        int Size = 1000;
        Matrix a = new Matrix(Size, Size);
        Matrix b = new Matrix(Size, Size);
        Matrix c = new Matrix(Size, Size);
        if (rank == 0) {
            a.init();
            b.init();
        }
        MPI.COMM_WORLD.Barrier();
        MPI.COMM_WORLD.Bcast(a.getValues(), 0, a.getValues().length, MPI.DOUBLE, 0);
        MPI.COMM_WORLD.Bcast(b.getValues(), 0, b.getValues().length, MPI.DOUBLE, 0);
        if (rank == 0) {
             start = System.currentTimeMillis();
        }
        MatrixMult.parallelMatrixMultiply(a, b, c);
        MPI.COMM_WORLD.Barrier();
        if (rank == 0) {
            long stop = System.currentTimeMillis();
//            try {
//                Files.writeString(Path.of(pathToLogFile), stop-start + "\n", StandardOpenOption.APPEND);
//            } catch (IOException ex) {
//                System.out.println(ex.getMessage());
//            }
            System.out.println("Time: " + (stop - start));

            //a.show();
            //b.show();
            //c.show();
        }
        MPI.Finalize();
    }
}
