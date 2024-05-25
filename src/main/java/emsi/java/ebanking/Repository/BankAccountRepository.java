package emsi.java.ebanking.Repository;

import emsi.java.ebanking.Entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccount,Long> {


    Optional<BankAccount> findById(String uuid);


}