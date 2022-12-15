package my.com.XML.entity;

import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class Company {
    private Map<String, Department> departmentArray = new HashMap<>();
    private Map<String, Worker> workerArray = new HashMap<>();


    public void createId(Department department) {
        int id = departmentArray.size();
        String idToString = "d" + id;
        while(departmentArray.get(idToString) != null) {
            id++;
            idToString = "d" + id;
        }
        department.setId(idToString);
    }

    public void createId(Worker worker) {
        int id = workerArray.size();
        String idToString = "w" + id;
        while(departmentArray.get(idToString) != null) {
            id++;
            idToString = "w" + id;
        }
        worker.setId(idToString);
    }

    public boolean addDepartment(Department department) {
        createId(department);
        departmentArray.put(department.getId(), department);
        return true;
    }

    public boolean addWorker(Worker worker) {
        createId(worker);
        if(departmentArray.get(worker.getDepartmentId()) != null){
            workerArray.put(worker.getId(), worker);
            return true;
        }
        return false;
    }
    public List<Department> getDepartmentList(){
        return departmentArray.values().stream().toList();
    }
    public void removeDepartment(Department department) {
        departmentArray.remove(department.getId());
        workerArray.values().removeIf(e -> e.getDepartmentId().equals(department.getId()));
    }

    public void removeWorker(Worker worker) {
        workerArray.remove(worker.getId());
    }

    public Worker getWorker(String id) {
        if(id != null) {
            return workerArray.get(id);
        }
        return null;
    }

    public Department getDepartment(String id) {
        if(id != null) {
            return departmentArray.get(id);
        }
        return null;
    }

    public List<Worker> getDepartmentsWorkers(String id){
        if(id != null)
            return workerArray.values().stream().filter(e -> e.getDepartmentId().equals(id)).toList();
        return null;
    }

    public void updateWorker(Worker worker) {
        workerArray.remove(worker.getId());
        workerArray.put(worker.getId(), worker);
    }

    public void updateDepartment(Department department) {
        departmentArray.remove(department.getId());
        departmentArray.put(department.getId(), department);
    }
    public void outputAllContents(){
        for (String id : departmentArray.keySet()){
            System.out.println("\n================================");
            System.out.println(departmentArray.get(id).toString());
            List<Worker> workerList = getDepartmentsWorkers(id);
            System.out.println("--------------------------------");
            for (Worker worker : workerList) System.out.println(worker.toString());

        }
    }
}
