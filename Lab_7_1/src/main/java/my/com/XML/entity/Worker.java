package my.com.XML.entity;

import lombok.*;

import java.util.Objects;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Worker {

	private String id;
	private String name;

	private String departmentId;


	public Worker(String name, String departmentId) {
		this.name = name;
		this.departmentId = departmentId;
	}

	@Override
	public String toString() {
		return "Worker{" +
				"id='" + id + '\'' +
				", name='" + name + '\'' +
				", departmentId='" + departmentId + '\'' +
				'}';
	}
}