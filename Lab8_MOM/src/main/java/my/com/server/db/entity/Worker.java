package my.com.server.db.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
public class Worker implements Serializable {

	@Serial
	private static final long serialVersionUID = 1234567L;

	@Getter
	private int id;

	@Getter
	@Setter
	private String name;

	@Getter
	@Setter
	private int departmentId;


	public static Worker createWorker(String name, int departmentId){
		return new Worker(0, name, departmentId);
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