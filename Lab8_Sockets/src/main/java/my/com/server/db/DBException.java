package my.com.server.db;

public class DBException extends Exception {
    public DBException() {
        super();
    }

    public DBException(Throwable cause) {
        super(cause);
    }

    @Override
    public void printStackTrace() {
        super.printStackTrace();
    }
}
