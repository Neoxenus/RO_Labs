package my.com.server;


import my.com.server.DAO.entity.Patient;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RemoteController extends Remote {

    int getNumberClients() throws RemoteException;

    void addClient() throws RemoteException;

    List<Patient> getPatientsWithDiagnosis(String diagnosis) throws RemoteException;

    List<Patient> getPatientsWithMedicineCardsInRange(int minMedicineCardNumber, int maxMedicineCardNumber) throws RemoteException;

}