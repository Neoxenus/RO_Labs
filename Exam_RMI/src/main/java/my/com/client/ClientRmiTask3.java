package my.com.client;

import lombok.SneakyThrows;
import my.com.server.RemoteController;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientRmiTask3 {
    @SneakyThrows
    public static void main(String[] args){
        Registry registry = LocateRegistry.getRegistry(4444);
        RemoteController server = (RemoteController) registry
                .lookup("DBController");
        server.addClient();
        System.out.println("Client number: " + server.getNumberClients());
        System.out.println("RMI object found");
        System.out.println(server.getPatientsWithDiagnosis("COVID-19"));
        System.out.println(server.getPatientsWithMedicineCardsInRange(123_456_789, 345_456_789));
        Thread.sleep(10000);
        server.deleteClient();
    }
}