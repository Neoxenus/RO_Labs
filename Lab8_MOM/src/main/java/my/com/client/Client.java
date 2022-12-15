package my.com.client;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import my.com.server.db.entity.Department;
import my.com.server.db.entity.Worker;
import my.com.utility.SerializationUtility;

import java.io.*;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

public class Client implements AutoCloseable {
    private final Connection connection;
    private final Channel channel;
    private static final String requestQueueName = "rpcqname";

    public Client() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        connection = factory.newConnection();
        channel = connection.createChannel();
    }

    public static void main(String[] argv) {
        try ( Client client = new Client()){

            System.out.println(client.findAllDepartments());
            System.out.println(client.findAllWorkers());
            Worker newWorker = Worker.createWorker("worker1", 1);
            client.insertWorker(newWorker);
            System.out.println(client.findAllWorkers());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() throws IOException {
        connection.close();
    }
    private Object sendQuery(int operation, Serializable value)  {
        final String corrId = UUID.randomUUID().toString();
        try {
            String replyQueueName = channel.queueDeclare().getQueue();
            AMQP.BasicProperties props = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(corrId)
                    .replyTo(replyQueueName)
                    .build();

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            String query = operation + "#" + SerializationUtility.serialize(value);
            oos.writeObject(query);
            channel.basicPublish("", requestQueueName, props, bos.toByteArray());

            final CompletableFuture<String> futureResponse = new CompletableFuture<>();

            String ctag = channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
                if (delivery.getProperties().getCorrelationId().equals(corrId)) {
                    ByteArrayInputStream bis = new ByteArrayInputStream(delivery.getBody());
                    ObjectInputStream ois = new ObjectInputStream(bis);
                    try {
                        futureResponse.complete((String) ois.readObject());

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }, consumerTag -> {
            });
            String response = futureResponse.get();
            channel.basicCancel(ctag);
            String[] fields = response.split("#");
                int comp_code = Integer.parseInt(fields[0]); // Код завершення
                Object result = SerializationUtility.deSerialize(fields[1]); // Результат операції
                if (comp_code == 0)
                    return result;
                else throw new IOException("Error while processing query");
        } catch (IOException | ExecutionException | InterruptedException | ClassNotFoundException e) {
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

}