package my.com.server.db;

import my.com.server.Constants;
import my.com.server.db.entity.Department;
import my.com.server.db.entity.Worker;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class DBManager {


	Connection connection;
	private static DBManager singleObject = null;
	private DBManager() {
		try {
			Properties properties = new Properties();
			properties.load(new FileInputStream(Constants.SETTINGS_FILE));
			connection = DriverManager.getConnection(properties.getProperty("connection.url"));
		} catch (IOException | SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public static synchronized DBManager getInstance() {
		if(singleObject == null){
			singleObject = new DBManager();
		}
		return singleObject;
	}
	public boolean insertDepartment(Department department) throws DBException {
		if (department == null)
			return false;
		try{
			PreparedStatement statement = connection.prepareStatement("INSERT INTO departments (name) VALUES(?)");
			statement.setString(1, department.getName());
			statement.executeUpdate();
			//department.setId(getDepartment(department.getName()).getId());
			return true;
		}catch (SQLException e){
			throw new DBException(e);
		}
	}
	public boolean insertWorker(Worker worker) throws DBException {
		if (worker == null)
			return false;
		try{
			PreparedStatement statement = connection.prepareStatement("INSERT INTO workers (name, department_id) VALUES(?,?)");
			statement.setString(1, worker.getName());
			statement.setInt(2, worker.getDepartmentId());
			statement.executeUpdate();
			return true;
		}catch (SQLException e){
			throw new DBException(e);
		}
	}
	public Worker getWorker(String name) throws DBException {
		try{
			PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM workers WHERE name = ?");
			preparedStatement.setString(1, name);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()){
				return new Worker(resultSet.getInt(Fields.WORKER_ID),
						resultSet.getString(Fields.WORKER_NAME),
						resultSet.getInt(Fields.WORKER_DEPARTMENT_ID));
				//return User.createUser(resultSet.getString(Fields.USER_LOGIN));
			}
			return null;
		}catch (SQLException e){
			throw new DBException(e);
		}
	}

	public Department getDepartment(String name) throws DBException {
		try{
			PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM departments WHERE name = ?");
			preparedStatement.setString(1, name);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()){
				return new Department(resultSet.getInt(Fields.DEPARTMENT_ID), resultSet.getString(Fields.DEPARTMENT_NAME));
				//return Team.createTeam(resultSet.getString(Fields.TEAM_NAME));
			}
			return null;
		}catch (SQLException e){
			throw new DBException(e);
		}
	}
	public List<Department> findAllDepartments() throws DBException {
		try{
			PreparedStatement statement = connection.prepareStatement("SELECT * FROM departments");
			ResultSet resultSet = statement.executeQuery();
			List<Department> userList = new ArrayList<>();

			while(resultSet.next()){
				Department buf = new Department(resultSet.getInt(Fields.DEPARTMENT_ID),
						resultSet.getString(Fields.DEPARTMENT_NAME)

				);
				userList.add(buf);
			}

			return userList;
		}catch (SQLException e){
			throw new DBException(e);
		}
	}
	public List<Worker> findAllWorkers() throws DBException {
		try{
			PreparedStatement statement = connection.prepareStatement("SELECT * FROM workers");
			return getWorkers(statement);
		}catch (SQLException e){
			throw new DBException(e);
		}
	}


	public boolean deleteWorkers(Worker... workers) throws DBException {
		try {
			PreparedStatement statement = connection.prepareStatement("DELETE FROM workers WHERE name=?");
			for (Worker worker : workers) {
				statement.setString(1, worker.getName());
				statement.executeUpdate();
			}
			return true;
		} catch (SQLException e) {
			throw new DBException(e);
		}
	}
//
//
//


	public List<Worker> getDepartmentWorkers(Department department) throws DBException {
		if(department == null)
			return null;
		try{
			PreparedStatement preparedStatement = connection.prepareStatement("SELECT w.id, w.name, w.department_id " +
					"FROM departments d " +
					"INNER JOIN workers w " +
					"ON d.id=w.department_id " +
					"WHERE d.name=?");
			preparedStatement.setString(1, department.getName());
			return getWorkers(preparedStatement);
		}catch (SQLException e){
			throw new DBException(e);
		}
	}

	private List<Worker> getWorkers(PreparedStatement preparedStatement) throws SQLException {
		ResultSet resultSet = preparedStatement.executeQuery();
		List<Worker> workerList = new ArrayList<>();
		while (resultSet.next()){
			workerList.add(new Worker(resultSet.getInt(Fields.WORKER_ID),
					resultSet.getString(Fields.WORKER_NAME),
					resultSet.getInt(Fields.WORKER_DEPARTMENT_ID)
					));
		}
		return workerList;
	}

	//
	public boolean deleteDepartments(Department department) throws DBException {
		try {
			PreparedStatement statement = connection.prepareStatement("DELETE FROM departments WHERE name=?");
			statement.setString(1, department.getName());
			statement.executeUpdate();
			return true;
		} catch (SQLException e) {
			throw new DBException(e);
		}
	}
//
	public boolean updateWorker(Worker worker) throws DBException {
		//worker = getTeam(worker.getName());
		try {
			PreparedStatement statement = connection.prepareStatement("UPDATE workers SET name=?, department_id=? WHERE id=?");
			statement.setString(1, worker.getName());
			statement.setInt(2, worker.getDepartmentId());
			statement.setInt(3, worker.getId());
			statement.executeUpdate();
			return true;
		} catch (SQLException e) {
			throw new DBException(e);
		}
	}
	public boolean updateDepartment(Department department) throws DBException {
		//department = getTeam(department.getName());
		try {
			PreparedStatement statement = connection.prepareStatement("UPDATE departments SET name=? WHERE id=?");
			statement.setString(1, department.getName());
			statement.setInt(2, department.getId());
			statement.executeUpdate();
			return true;
		} catch (SQLException e) {
			throw new DBException(e);
		}
	}


}
