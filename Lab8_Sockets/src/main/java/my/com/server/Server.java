package my.com.server;

import my.com.server.db.DBException;
import my.com.server.db.DBManager;
import my.com.server.db.entity.Department;
import my.com.server.db.entity.Worker;
import my.com.utility.SerializationUtility;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket server = null;
    private Socket sock = null;
    private PrintWriter out = null;
    private BufferedReader in = null;
    private DBManager dbManager = null;
    // головний метод
    public static void main(String[] args) {
        try {
            Server srv = new Server();
            srv.start(3333);
        } catch (IOException e) {
            System.out.println("Возникла ошибка");
        }
    }
    public void start(int port) throws IOException {
        server = new ServerSocket(port);
        dbManager = DBManager.getInstance();
        while (true) {
            sock = server.accept();
            in = new BufferedReader(new
                    InputStreamReader(sock.getInputStream()));
            out = new PrintWriter(sock.getOutputStream(), true);
            while (processQuery());
        }
    }
    private boolean processQuery() {
        String result = ""; // Результат операції
        int comp_code = 0; // Код завершення
        try {
            String query = in.readLine();
            if (query == null) return false;
            String[] fields = query.split("#");
                try {
                    int command = Integer.parseInt(fields[0]);
                    Object object = SerializationUtility.deSerialize(fields[1]);
                    Object notSerializableResult = switch (command) {
                        case 0 ->  dbManager.findAllDepartments();
                        case 1 -> dbManager.findAllWorkers();
                        case 2 ->
                                 (dbManager.getDepartmentWorkers((Department) object));
                        case 3 ->
                                 (dbManager.insertDepartment((Department) object));
                        case 4 -> (dbManager.insertWorker((Worker) object));
                        case 5 -> (dbManager.getWorker((String) object));
                        case 6 -> (dbManager.getDepartment((String) object));
                        case 7 -> (dbManager.deleteWorkers((Worker[]) object));
                        case 8 ->
                                (dbManager.deleteDepartments((Department) object));
                        case 9 -> (dbManager.updateWorker((Worker) object));
                        case 10 ->
                                (dbManager.updateDepartment((Department) object));
                        default -> throw new IllegalStateException("Unexpected value: " + command);
                    };
                    result = SerializationUtility.serialize(notSerializableResult);
                } catch (NumberFormatException e) {
                    comp_code = 3; // невірний тип параметрів
                } catch (DBException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            String response = comp_code + "#" + result;
            out.println(response);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}

