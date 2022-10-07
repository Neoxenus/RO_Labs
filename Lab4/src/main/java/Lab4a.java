import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class Lab4a {
    public static final String FILE_NAME = "records.txt";
    public static final String PHONE_NUMBER_REGEX = "^\\+?\\d{10,12}$";
    public static final String NAME_REGEX = "[a-zA-Z]+[0-9]*";

    public static final AtomicBoolean readLock = new AtomicBoolean(false);
    public static final AtomicBoolean writeLock = new AtomicBoolean(false);

    private void run() {
        boolean isWriter = getRole();
        while(true) {
            int choice = getChoice(isWriter);
            switch (choice) {
                case 1 -> {
                    System.out.println("Enter the name:");
                    String name = new Scanner(System.in).nextLine();
                    new Thread(() -> findByName(
                            name
                                    .replaceAll("\\s+", "")
                                    .toLowerCase()
                    )).start();
                }
                case 2 -> {
                    System.out.println("Enter the phone number:");
                    String phoneNumber = new Scanner(System.in).next();
                    new Thread(() -> findByPhoneNumber(phoneNumber)).start();
                }
                case 3 -> {
                    System.out.println("Enter the name of new record");
                    String name = new Scanner(System.in).nextLine();
                    String nameToCheck = name.replaceAll("\\s+", "");
                    if(!nameToCheck.matches(NAME_REGEX)) {
                        System.out.println("Wrong name input");
                        break;
                    }
                    System.out.println("Enter the phone number of new record:");
                    String phoneNumber = new Scanner(System.in).next();
                    if(!phoneNumber.matches(PHONE_NUMBER_REGEX)) {
                        System.out.println("Wrong phone number input");
                        break;
                    }
                    String record = name + " - " + phoneNumber + "\n";
                    new Thread(() -> addNewRecord(record)).start();
                }
                case 4 -> {
                    System.out.println("Enter the name, whose record you would like to delete:");
                    String nameToDelete = new Scanner(System.in).nextLine();
                    new Thread(() -> deleteByName(
                            nameToDelete
                                    .replaceAll("\\s+", "")
                                    .toLowerCase())).start();
                }
                case 0 -> System.exit(0);
            }
        }
    }

    private void findByName(String name) {

        readLock();

        try(BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line = br.readLine();
            StringBuilder sb = new StringBuilder();

            while (line != null) {
                String nameFromLine = getNameFromRecord(line);
                if (nameFromLine.equals(name)) {
                    int index = line.indexOf("-");
                    sb.append(line.substring(index + 2));
                    sb.append(System.lineSeparator());
                }
                line = br.readLine();
            }
            if(sb.isEmpty()) {
                System.out.println("No records found");
                return;
            }
            System.out.println(sb);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            readUnlock();
        }
    }

    private void findByPhoneNumber(String phoneNumber) {

        readLock();

        try(BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line = br.readLine();
            StringBuilder sb = new StringBuilder();

            while (line != null) {
                String phoneNumberFromRecord = getPhoneNumberFromRecord(line);
                if (phoneNumberFromRecord.equals(phoneNumber)) {
                    int index = line.indexOf("-");
                    sb.append(line, 0, index);
                    sb.append(System.lineSeparator());
                }
                line = br.readLine();
            }
            if(sb.isEmpty()) {
                System.out.println("No records found");
                return;
            }
            System.out.println(sb);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            readUnlock();
        }

    }


    private void addNewRecord(String record) {

        writeLock();

        try(Writer output = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            output.append(record);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            writeUnlock();
        }
    }


    private void deleteByName(String name) {

        writeLock();

        StringBuilder sb = new StringBuilder();
        try(BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line = br.readLine();

            while (line != null) {
                String nameFromLine = getNameFromRecord(line);
                if(!nameFromLine.equals(name)) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                }
                line = br.readLine();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try(Writer output = new BufferedWriter(new FileWriter(FILE_NAME))) {
            output.append(sb);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            writeUnlock();
        }

    }

    private String getNameFromRecord(String record) {
        String name = record.substring(0, record.indexOf("-"));
        return name.replaceAll("\\s+", "").toLowerCase();
    }

    private String getPhoneNumberFromRecord(String record) {
        return record.substring(record.indexOf("-") + 2);
    }


    private int getChoice(boolean isWriter) {
        while (true) {
            System.out.println("What do you want to do?\n1 - Find phone number by name\n2 - Find name by phone number");
            if (isWriter) {
                System.out.println("3 - Add new record\n4 - Delete record by name");
            }
            System.out.println("0 - Close the program");
            try {
                int choice = new Scanner(System.in).nextInt();
                if (!isWriter && (choice == 1 || choice == 2) ||
                        isWriter && choice >= 1 && choice <= 4 ||
                        choice == 0) {
                    return choice;
                } else {
                    System.out.println("Wrong input");
                }
            } catch (InputMismatchException ex) {
                System.out.println("Wrong input");
            }
        }
    }

    private boolean getRole() {
        while(true) {
            System.out.println("Are you a reader or a writer? Type R/W:");
            String roleInput = new Scanner(System.in).next();
            switch (roleInput) {
                case "R" -> {
                    return false;
                }
                case "W" -> {
                    return true;
                }
                default -> System.out.println("Wrong input");
            }
        }
    }

    private void readLock(){
        while(writeLock.get()){}
        readLock.set(true);
    }

    private void readUnlock(){
        readLock.set(false);
    }

    private void writeLock(){
        while(readLock.get()){}
        writeLock.set(true);
    }

    private void writeUnlock(){
        writeLock.set(false);
    }

    public static void main(String[] args) {
        new Lab4a().run();
    }
}
