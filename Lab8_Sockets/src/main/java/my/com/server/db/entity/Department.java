package my.com.server.db.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class Department implements Serializable {

	@Serial
	private static final long serialVersionUID = 234567L;

	@Getter
	private int id;
	@Getter
	@Setter
	private String name;

	public Department(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public static Department createDepartment(String name){
		return new Department(0, name);
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