package my.com.server;

import my.com.server.DAO.DAO;
import my.com.utility.SerializationUtility;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketTask3 {
    private ServerSocket server = null;
    private Socket sock = null;
    private PrintWriter out = null;
    private BufferedReader in = null;
    private DAO daoManager = null;
    // головний метод
    public static void main(String[] args) {
        try {
            ServerSocketTask3 srv = new ServerSocketTask3();
            srv.start(3333);
        } catch (IOException e) {
            System.out.println("Возникла ошибка");
        }
    }
    public void start(int port) throws IOException {
        server = new ServerSocket(port);
        daoManager = new DAO();
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
                    Object object1 = SerializationUtility.deSerialize(fields[1]);
                    Object object2 = SerializationUtility.deSerialize(fields[2]);
                    Object notSerializableResult = switch (command) {
                        case 0 ->  daoManager.getPatientsWithDiagnosis((String) object1);
                        case 1 -> daoManager.getPatientsWithMedicineCardsInRange((Integer) object1, (Integer) object2);
                        default -> throw new IllegalStateException("Unexpected value: " + command);
                    };
                    result = SerializationUtility.serialize(notSerializableResult);
                } catch (NumberFormatException e) {
                    comp_code = 3; // невірний тип параметрів
                } catch (ClassNotFoundException e) {
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

