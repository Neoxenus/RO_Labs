package my.com.client;


import my.com.server.DAO.entity.Patient;
import my.com.utility.SerializationUtility;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ClientSocketTask3 {
    private final Socket socket;
    private final PrintWriter out;
    private final BufferedReader in;
    // конструктор
    public ClientSocketTask3(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        in = new BufferedReader(new
                InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }


    public static void main(String[] args) {
        try {
            ClientSocketTask3 client = new ClientSocketTask3("localhost", 3333);
            System.out.println(client.getPatientsWithDiagnosis("COVID-19"));
            System.out.println(client.getPatientsWithMedicineCardsInRange(123-456-789, 345-456-789));
            client.disconnect();
        } catch (IOException e) {
            System.out.println("Виникла помилка");
            e.printStackTrace();
        }
    }

    private void disconnect() throws IOException {
        socket.close();
    }

    private List<Patient> sendQuery(int operation, Serializable value1, Serializable value2) throws
            IOException {
        String query = operation + "#" + SerializationUtility.serialize(value1)+"#"+SerializationUtility.serialize(value2);
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
                return (List<Patient>) result;
            else throw new IOException("Error while processing query");
        } catch (NumberFormatException e) {
            throw new IOException("Invalid response from server");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public List<Patient> getPatientsWithDiagnosis(String diagnosis) throws IOException {
        return sendQuery(0, diagnosis, "");
    }
    public List<Patient> getPatientsWithMedicineCardsInRange(int minMedicineCardNumber, int maxMedicineCardNumber) throws IOException {
        return sendQuery(1, minMedicineCardNumber, maxMedicineCardNumber);
    }


}