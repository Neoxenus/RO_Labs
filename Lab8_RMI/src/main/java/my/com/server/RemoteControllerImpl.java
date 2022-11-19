package my.com.server;

import lombok.SneakyThrows;
import lombok.Synchronized;
import my.com.server.db.manager.DBException;
import my.com.server.db.manager.DBManager;
import my.com.server.db.entity.Department;
import my.com.server.db.entity.Worker;

import java.rmi.RemoteException;
import java.util.List;

public class RemoteControllerImpl implements RemoteController {
    private final DBManager dbManager;
    private int numberOfClient = 0;

    public RemoteControllerImpl() throws RemoteException {
        this.dbManager = DBManager.getInstance();
    }

    @Override
    public int getNumberClients() {
        return numberOfClient;
    }

    @Override
    public void addClient() {
        numberOfClient++;
    }

    @Override
    @SneakyThrows
    @Synchronized("dbManager")
    public List<Department> findAllDepartments(){
        return dbManager.findAllDepartments();
    }


    @SneakyThrows
    @Override
    @Synchronized("dbManager")
    public List<Worker> findAllWorkers() {
        return dbManager.findAllWorkers();
    }

    @SneakyThrows
    @Override
    @Synchronized("dbManager")
    public List<Worker> getDepartmentWorkers(Department department) {
        return dbManager.getDepartmentWorkers(department);
    }
    @SneakyThrows
    @Override
    @Synchronized("dbManager")
    public Boolean insertDepartment(Department department){
        return dbManager.insertDepartment(department);
    }
    @SneakyThrows
    @Override
    @Synchronized("dbManager")
    public Boolean insertWorker(Worker worker){
        return dbManager.insertWorker(worker);
    }
    @SneakyThrows
    @Override
    @Synchronized("dbManager")
    public Worker getWorker(String name)  {
        return dbManager.getWorker(name);
    }
    @SneakyThrows
    @Override
    @Synchronized("dbManager")
    public Department getDepartment(String name) {
        return dbManager.getDepartment(name);
    }

    @SneakyThrows
    @Override
    @Synchronized("dbManager")
    public Boolean deleteWorkers(Worker... workers) {
        return dbManager.deleteWorkers(workers);
    }

    @SneakyThrows
    @Override
    @Synchronized("dbManager")
    public Boolean deleteDepartments(Department department)  {
        return dbManager.deleteDepartments(department);
    }

    @SneakyThrows
    @Override
    @Synchronized("dbManager")
    public Boolean updateWorker(Worker worker) {
        return dbManager.updateWorker(worker);
    }

    @SneakyThrows
    @Override
    @Synchronized("dbManager")
    public Boolean updateDepartment(Department department)  {
        return dbManager.updateDepartment(department);
    }
}
