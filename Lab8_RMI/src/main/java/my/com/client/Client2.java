package my.com.client;

import lombok.SneakyThrows;
import my.com.server.RemoteController;
import my.com.server.db.entity.Department;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client2 {
    @SneakyThrows
    public static void main(String[] args){
        Registry registry = LocateRegistry.getRegistry(2222);
        RemoteController server = (RemoteController) registry
                .lookup("DBController");
        server.addClient();

        System.out.println("RMI object found");
        while (true)
            if(server.getNumberClients() == 2 ){
                int nameSuffix = 0;
                for (int i = 0; i < 3; i++) {
                    Department tmpDepartment = Department.createDepartment("dep"+(nameSuffix++));
                    System.out.println(server.insertDepartment(tmpDepartment));
                }
                break;
            }
    }
}