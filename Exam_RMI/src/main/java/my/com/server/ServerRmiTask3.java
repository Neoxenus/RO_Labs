package my.com.server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ServerRmiTask3 {

    // головний метод
    public static void main(String[] args)
            throws RemoteException {
        RemoteController server = new RemoteControllerImpl();
        RemoteController stub = (RemoteController) UnicastRemoteObject
                .exportObject(server, 4444);
        Registry registry = LocateRegistry.createRegistry(4444);
        registry.rebind("DBController", stub);
        System.out.println("Server started!");
    }
}

