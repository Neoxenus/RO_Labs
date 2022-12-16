package my.com.server.DAO;


import my.com.server.DAO.entity.Patient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DAO {
	private List<Patient> entry;
	public DAO(){
		entry = new ArrayList<>();
		init();
	}
	private void init(){
		Patient p1 = new Patient(1, "Petrov", "Petro", "Petrovych"
				, "Kyivska str.", "+390123456789", 123_456_789
				, "COVID-19"  );
		Patient p2 = new Patient(1, "Stepaniuk", "Stepan", "Stepanovych"
				, "Vinnytskia str.", "+390345678912", 345_456_789
				, "COVID-19"  );
		Patient p3 = new Patient(1, "Ivanov", "Ivan", "Ivanovych"
				, "Soborna str.", "+390234567891", 234_456_789
				, "Influenza"  );
		Patient p4 = new Patient(1, "Volodymyrhiuk", "Volodymyr", "Volodymyrovych"
				, "Hlibna str.", "+390456789123", 432_456_789
				, "COVID-19"  );
		entry.addAll(List.of(new Patient[]{p1, p2, p3, p4}));
	}
	public List<Patient> getPatientsWithDiagnosis(String diagnosis){
		return entry.stream().filter(e -> e.getDiagnosis().equals(diagnosis)).collect(Collectors.toList());
	}
	public List<Patient> getPatientsWithMedicineCardsInRange(int minMedicineCardNumber, int maxMedicineCardNumber){
		return entry.stream()
				.filter(e ->
						(e.getMedicalCardNumber() > minMedicineCardNumber
								&& e.getMedicalCardNumber() < maxMedicineCardNumber))
				.collect(Collectors.toList());
	}
}
