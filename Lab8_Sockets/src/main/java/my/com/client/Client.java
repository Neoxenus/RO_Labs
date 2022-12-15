package my.com.client;

import my.com.server.db.entity.Department;
import my.com.server.db.entity.Worker;
import my.com.utility.SerializationUtility;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class Client {
    private final Socket socket;
    private final PrintWriter out;
    private final BufferedReader in;
    // конструктор
    public Client(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        in = new BufferedReader(new
                InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }


    public static void main(String[] args) {
        try {
            Client client = new Client("localhost", 3333);
            System.out.println(client.findAllDepartments());
            System.out.println(client.findAllWorkers());
            Worker newWorker = Worker.createWorker("worker1", 1);
            client.insertWorker(newWorker);
            System.out.println(client.findAllWorkers());
            client.disconnect();
        } catch (IOException e) {
            System.out.println("Виникла помилка");
            e.printStackTrace();
        }
    }
    private Object sendQuery(int operation, Serializable value) throws
            IOException {
        String query = operation + "#" + SerializationUtility.serialize(value);
        out.println(query);
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        String response = in.readLine();
        String[] fields = response.split("#");
        try {
            int comp_code = Integer.parseInt(fields[0]); // Код завершення
            Object result = SerializationUtility.deSerialize(fields[1]); // Результат операції
            if (comp_code == 0)
                return result;
            else throw new IOException("Error while processing query");
        } catch (NumberFormatException e) {
            throw new IOException("Invalid response from server");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public List<Department> findAllDepartments() throws IOException {
        return (List<Department>) sendQuery(0, "");
    }
    public List<Worker> findAllWorkers() throws IOException {
        return (List<Worker>) sendQuery(1, "");
    }
    public List<Worker> getDepartmentWorkers(Department department) throws IOException {
        return (List<Worker>) sendQuery(2, department);
    }
    public Boolean insertDepartment(Department department) throws IOException {
        return (Boolean) sendQuery(3, department);
    }
    public Boolean insertWorker(Worker worker) throws IOException {
        return (Boolean) sendQuery(4, worker);
    }
    public Worker getWorker(String name) throws IOException {
        return (Worker) sendQuery(5, name);
    }
    public Department getDepartment(String name) throws IOException {
        return (Department) sendQuery(6, name);
    }
    public Boolean deleteWorkers(Worker... workers) throws IOException {
        return (Boolean) sendQuery(7, workers);
    }
    public Boolean deleteDepartments(Department department) throws IOException {
        return (Boolean) sendQuery(8, department);
    }
    public Boolean updateWorker(Worker worker) throws IOException {
        return (Boolean) sendQuery(9, worker);
    }
    public Boolean updateDepartment(Department department) throws IOException {
        return (Boolean) sendQuery(10, department);
    }

    public void disconnect() throws IOException {
        socket.close();
    }

}