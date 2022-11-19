package my.com.client;

import lombok.SneakyThrows;
import my.com.server.RemoteController;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client1 {
    @SneakyThrows
    public static void main(String[] args){
        Registry registry = LocateRegistry.getRegistry(2222);
        RemoteController server = (RemoteController) registry
                .lookup("DBController");
        server.addClient();
        System.out.println("RMI object found");
        while (true) {
            if(server.getNumberClients() == 2 ){
                System.out.println(server.findAllDepartments());
                Thread.sleep(100);
                System.out.println(server.findAllDepartments());
                break;
            }
        }
    }
}