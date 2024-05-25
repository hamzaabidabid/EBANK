package emsi.java.ebanking.Entity;

import emsi.java.ebanking.Enum.AccountStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import javax.lang.model.element.Name;
import java.util.Date;
import java.util.List;
@Entity
@Inheritance(strategy =InheritanceType.SINGLE_TABLE )
@DiscriminatorColumn(name="TYPE" ,length = 4)
@Data
@AllArgsConstructor
@NoArgsConstructor
public  abstract class BankAccount {
    @Id
    private String id ;
    private double balance;
    private Date createdAt;
    private String currency;
    @Enumerated(EnumType.STRING)
    private AccountStatus status;
    @ManyToOne
    private  Customer customer;
    @OneToMany(mappedBy = "bankAccount",fetch = FetchType.LAZY)
    private  List<AccountOperation> accountOperations;
}
