package emsi.java.ebanking.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import emsi.java.ebanking.Entity.BankAccount;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
public class CustomerDTO {

    private  Long id;
    private String name;
    private String email;


}
