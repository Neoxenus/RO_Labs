package my.com.server.DAO.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Patient implements Serializable {
    @Serial
    private static final long serialVersionUID = 12345L;
    int id;
    String lastName;
    String firstName;
    String middleName;
    String address;
    String phoneNumber;
    int medicalCardNumber;
    String diagnosis;

}