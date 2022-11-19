package my.com;


import my.com.XML.DOMController;
import my.com.XML.entity.Company;
import my.com.XML.entity.Worker;

public class Demo {

	public static void main(String[] args) {
		Company company = DOMController.parse(Constants.XML_FILE_IN);
		company.outputAllContents();
		Worker w = new Worker("My name", company.getDepartmentList().get(0).getId());
		company.addWorker(w);
		w.setName("Not my name");
		company.updateWorker(w);
		System.out.println(company.getDepartmentsWorkers("d0"));
		company.removeWorker(w);

		DOMController.write(company, Constants.XML_FILE_OUT);
	}

}
