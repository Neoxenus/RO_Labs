package my.com.server;

import my.com.server.DAO.DAO;
import my.com.utility.SerializationUtility;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class ServerSocketTask3 {
    private DAO daoManager = null;
    // головний метод
    public static void main(String[] args) {
        ServerSocketTask3 srv = new ServerSocketTask3();
        srv.start(3333);
    }
    public void start(int port) {
        ServerSocket server = null;
        try {
            server = new ServerSocket(port);
            server.setReuseAddress(true);
            daoManager = new DAO();
            while (true) {
                Socket client = server.accept();
                System.out.println("New client connected " + client.getInetAddress().getHostAddress());
                ClientHandler clientSock = new ClientHandler(client);
                new Thread(clientSock).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
          if(server != null) {
              try {
                  server.close();
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }
        }
    }
    private class ClientHandler implements Runnable{
        private final Socket clientSocket;
        public ClientHandler(Socket socket){
            clientSocket = socket;
        }
        public void run() {
            while (true){
                String result = ""; // Результат операції
                int comp_code = 0; // Код завершення

                try{
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                    String query = in.readLine();
                    if (query == null) throw new IOException("Query is null");
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
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                    System.out.println("Connection with client " + clientSocket.getInetAddress().getHostAddress() + " was closed");
                    break;
                }
            }

        }
    }

}

