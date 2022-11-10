import mpi.MPI;
public class Lab6 {
    public static void main(String[] args) {
        MPI.Init(args);
        System.out.println("Hello, world!");
        MPI.Finalize();
    }
}
