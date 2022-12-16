package my.com.server;

import lombok.SneakyThrows;
import lombok.Synchronized;
import my.com.server.DAO.DAO;
import my.com.server.DAO.entity.Patient;

import java.rmi.RemoteException;
import java.util.List;

public class RemoteControllerImpl implements RemoteController {
    private final DAO daoManager;
    private int numberOfClient = 0;

    public RemoteControllerImpl() throws RemoteException {
        this.daoManager = new DAO();
    }

    @Override
    public int getNumberClients() {
        return numberOfClient;
    }

    @Override
    public void addClient() throws RemoteException {
        numberOfClient++;
    }
    @Override
    public void deleteClient() throws RemoteException {
        numberOfClient--;
    }

    @Override
    public List<Patient> getPatientsWithDiagnosis(String diagnosis) throws RemoteException {
        return daoManager.getPatientsWithDiagnosis(diagnosis);
    }

    @Override
    public List<Patient> getPatientsWithMedicineCardsInRange(int minMedicineCardNumber, int maxMedicineCardNumber) throws RemoteException {
        return daoManager.getPatientsWithMedicineCardsInRange(minMedicineCardNumber, maxMedicineCardNumber);
    }
}
