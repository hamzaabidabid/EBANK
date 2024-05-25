package emsi.java.ebanking.Entity;

import emsi.java.ebanking.Enum.AccountStatus;
import emsi.java.ebanking.Enum.OperationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.boot.beanvalidation.GroupsPerOperation;

import java.util.Date;
import java.util.List;
@Entity
@Data @AllArgsConstructor @NoArgsConstructor
public class AccountOperation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;
    private Date OperationDate;
    private double amount;
    private String description;
    @Enumerated(EnumType.STRING)
    private OperationType opType;
    @ManyToOne
    private BankAccount bankAccount;

    public boolean getType() {
        return false;
    }

    public void setType(OperationType operationType) {
        this.opType=operationType;
    }
}
