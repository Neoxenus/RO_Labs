package my.com.server;


import my.com.server.db.entity.Department;
import my.com.server.db.entity.Worker;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RemoteController extends Remote {

    int getNumberClients() throws RemoteException;

    void addClient() throws RemoteException;

    List<Department> findAllDepartments() throws RemoteException;

    List<Worker> findAllWorkers() throws RemoteException;

    List<Worker> getDepartmentWorkers(Department department) throws RemoteException;

    Boolean insertDepartment(Department department) throws RemoteException;

    Boolean insertWorker(Worker worker) throws RemoteException;

    Worker getWorker(String name) throws RemoteException;

    Department getDepartment(String name) throws RemoteException;

    Boolean deleteWorkers(Worker... workers) throws RemoteException;

    Boolean deleteDepartments(Department department) throws RemoteException;

    Boolean updateWorker(Worker worker) throws RemoteException;

    Boolean updateDepartment(Department department) throws RemoteException;

}