package my.com.client;

import lombok.SneakyThrows;
import my.com.server.RemoteController;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientRmiTaskN {
    @SneakyThrows
    public static void main(String[] args){
        Registry registry = LocateRegistry.getRegistry(4444);
        RemoteController server = (RemoteController) registry
                .lookup("DBController");
        server.addClient();
        System.out.println("RMI object found");
        System.out.println(server.getPatientsWithDiagnosis("COVID-19"));
        System.out.println(server.getPatientsWithMedicineCardsInRange(123-456-789, 345-456-789));
        Thread.sleep(10000);
    }
}