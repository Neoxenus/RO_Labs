package my.com.XML;

import my.com.Constants;
import my.com.XML.entity.Company;
import my.com.XML.entity.Department;
import my.com.XML.entity.Worker;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class DOMController {

	private final List<Department> departmentList;
	private final List<Worker> workerList;
	Connection connection;
	private static DOMController singleObject = null;

	private DOMController() {
		departmentList = new ArrayList<>();
		workerList = new ArrayList<>();
	}

	public static synchronized DOMController getInstance() {
		if (singleObject == null) {
			singleObject = new DOMController();
		}
		return singleObject;
	}

	public static Company parse(String path) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Company company = new Company();
		try {

			dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, false);
			dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false);

			DocumentBuilder db = dbf.newDocumentBuilder();
			dbf.setNamespaceAware(true);

			Document doc = db.parse(new File(path));

			doc.getDocumentElement().normalize();

			NodeList nodes = doc.getElementsByTagName("Department");

			for (int i = 0; i < nodes.getLength(); ++i) {
				Element departmentNode = (Element) nodes.item(i);
				Department department = new Department();
				department.setId(departmentNode.getAttribute("id"));
				department.setName(departmentNode.getAttribute("name"));
				company.addDepartment(department);

				for (int j = 0; j < departmentNode.getElementsByTagName("Worker").getLength(); j++) {
					Element workerNode = (Element) departmentNode.getElementsByTagName("Worker").item(j);
					Worker worker = new Worker();
					worker.setId(workerNode.getAttribute("id"));
					worker.setDepartmentId(departmentNode.getAttribute("id"));
					worker.setName(workerNode.getAttribute("name"));
					company.addWorker(worker);
				}

			}

		} catch (ParserConfigurationException | SAXException | IOException e) {
			Logger.getLogger(DOMController.class.getName()).log(Level.SEVERE, "error", e);
		}
		return company;
	}

	public static void write(Company company, String path) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setExpandEntityReferences(false);
		factory.setValidating(true);
		factory.setNamespaceAware(true);
		try{
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();
			Element root = doc.createElement("Company");
			doc.appendChild(root);

			Map<String, Department> departmentArray = company.getDepartmentArray();
			Map<String, Worker> workerArray = company.getWorkerArray();
			for(Map.Entry<String, Department> departmentEntry : departmentArray.entrySet()) {
				Element gnr = doc.createElement("Department");
				gnr.setAttribute("id", departmentEntry.getValue().getId());
				gnr.setAttribute("name", departmentEntry.getValue().getName());
				root.appendChild(gnr);
				List<Worker> workersOfDepartment = workerArray.values().stream().filter(e -> e.getDepartmentId().equals(departmentEntry.getValue().getId())).toList();
				for(Worker worker: workersOfDepartment) {
					Element mv = doc.createElement("Worker");
					mv.setAttribute("id", worker.getId());
					mv.setAttribute("name", worker.getName());
					gnr.appendChild(mv);
				}
			}
			StreamResult result = new StreamResult(new File(path));
			TransformerFactory tf = javax.xml.transform.TransformerFactory.newInstance();
			javax.xml.transform.Transformer t;
			t = tf.newTransformer();
			t.setOutputProperty(OutputKeys.INDENT, "yes");
			t.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "input.dtd");
			t.transform(new DOMSource(doc), result);
		} catch (TransformerException | ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
	}
}
