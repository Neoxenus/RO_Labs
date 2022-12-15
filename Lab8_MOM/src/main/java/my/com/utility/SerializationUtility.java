package my.com.utility;

import java.io.*;
import java.util.Base64;

public class SerializationUtility {

    public static String serialize(Object o) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(o);
            oos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }
    public static Object deSerialize(String s)
            throws IOException, ClassNotFoundException {

        byte[] data = Base64.getDecoder().decode(s);
        ObjectInputStream ois = new ObjectInputStream(
                new ByteArrayInputStream(data));
        Object o = ois.readObject();
        ois.close();
        return o;
    }
}