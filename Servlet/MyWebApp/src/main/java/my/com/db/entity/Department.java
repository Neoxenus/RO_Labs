package my.com.db.entity;

import java.util.Objects;

public class Department {

	private int id;
	private String name;

	public Department(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public static Department createDepartment(String name){
		return new Department(0, name);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	public static Department createTeam(String name){
		return new Department(0, name);
	}

	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return "Department{" +
				"id=" + id +
				", name='" + name + '\'' +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Department team = (Department) o;
		return Objects.equals(name, team.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
	
}