package my.com;

import my.com.db.DBException;
import my.com.db.DBManager;
import my.com.db.entity.Department;
import my.com.db.entity.Worker;

public class Demo {

	public static void main(String[] args) throws DBException {
		// users  ==> [ivanov, petrov]
		// teams  ==> [teamA teamB teamC]
		// teamA contains the following users: ivanov, petrov 
		//read
		DBManager dbManager = DBManager.getInstance();
		System.out.println(dbManager.getWorker("petrov"));
		System.out.println(dbManager.getDepartment("teamA"));
		//create
		Worker w1 = Worker.createWorker("iliusha", 2);
		System.out.println(dbManager.insertWorker(w1));
		System.out.println(dbManager.getWorker(w1.getName()));
		Department d1 = Department.createDepartment("kiriusha");
		System.out.println(dbManager.insertDepartment(d1));
		System.out.println(dbManager.getDepartment(d1.getName()));
		//d1.setName("kirusha");
		System.out.println(dbManager.findAllDepartments());
		//delete
		System.out.println(dbManager.deleteDepartments(d1));
		System.out.println(dbManager.findAllDepartments());
		System.out.println(dbManager.getDepartmentWorkers(dbManager.getDepartment("teamB")));
		System.out.println(dbManager.deleteWorkers(w1));
		System.out.println(dbManager.getDepartmentWorkers(dbManager.getDepartment("teamB")));
		//update
		Worker tmp = dbManager.getWorker("petrov");
		tmp.setName("Petrov");
		dbManager.updateWorker(tmp);
		System.out.println(dbManager.findAllWorkers());

		Department tmpDep = dbManager.getDepartment("teamC");
		tmpDep.setName("teamD");
		System.out.println(dbManager.updateDepartment(tmpDep));
		System.out.println(dbManager.findAllDepartments());
	}

}
