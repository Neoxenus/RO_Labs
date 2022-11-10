package my.com.db.entity;

import java.util.Objects;

public class Worker {

	private int id;

	public void setName(String name) {
		this.name = name;
	}

	private String name;



	private int departmentId;

	public Worker() {
	}

	public Worker(int id, String name, int departmentId) {
		this.id = id;
		this.name = name;
		this.departmentId = departmentId;
	}

	public int getId() {
		return id;
	}
	public int getDepartmentId() {
		return departmentId;
	}

	public static Worker createWorker(String name, int departmentId){
		return new Worker(0, name, departmentId);
	}
	public void setDepartmentId(int departmentId) {
		this.departmentId = departmentId;
	}
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "Worker{" +
				"id=" + id +
				", name='" + name + '\'' +
				", departmentId=" + departmentId +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Worker user = (Worker) o;
		return Objects.equals(name, user.name);
	}


	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

}