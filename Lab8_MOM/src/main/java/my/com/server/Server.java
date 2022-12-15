package my.com.server;

import com.rabbitmq.client.*;
import my.com.server.db.DBException;
import my.com.server.db.DBManager;
import my.com.server.db.entity.Department;
import my.com.server.db.entity.Worker;
import my.com.utility.SerializationUtility;

import java.io.*;
import java.util.concurrent.TimeoutException;

public class Server
{
    private static final String RPC_QUEUE_NAME ="rpcqname";
    private final DBManager dbManager;

    public Server() {
        dbManager = DBManager.getInstance();
    }
    public static void main(String[]args) throws IOException, TimeoutException {
        Server server = new Server();
        server.start();
    }

    public void start() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);
        channel.queuePurge(RPC_QUEUE_NAME);

        channel.basicQos(1);

        System.out.println("Awaiting RPC requests");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(delivery.getProperties().getCorrelationId())
                    .build();

            try {
                ByteArrayInputStream bis = new ByteArrayInputStream(delivery.getBody());
                ObjectInput in = new ObjectInputStream(bis);

                processQuery((String)in.readObject(),channel,delivery,replyProps);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        channel.basicConsume(RPC_QUEUE_NAME, false, deliverCallback, (consumerTag -> {}));
    }

    private void processQuery(String query, Channel channel, Delivery delivery, AMQP.BasicProperties replyProps ) {
        String result = "";
        int comp_code = 0;
        try {
            if (query == null) throw new IllegalStateException("Unexpected value: " + "null");
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
                comp_code = 3;
            } catch (DBException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            String response = comp_code + "#" + result;
            sendResponse(response,channel,delivery,replyProps);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendResponse(String response,
                              Channel channel,
                              Delivery delivery,
                              AMQP.BasicProperties replyProps) throws IOException
    {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(response);
        channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, bos.toByteArray());
        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
    }

}