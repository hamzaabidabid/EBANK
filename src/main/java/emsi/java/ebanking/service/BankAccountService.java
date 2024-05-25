package emsi.java.ebanking.service;

import emsi.java.ebanking.Entity.BankAccount;
import emsi.java.ebanking.Entity.CurrentAccount;
import emsi.java.ebanking.Entity.SavingAccount;
import emsi.java.ebanking.Exception.BankAccountNotFoundException;
import emsi.java.ebanking.Exception.BanlanceNotSufficientException;
import emsi.java.ebanking.Exception.CustomerNotFoundEXception;
import emsi.java.ebanking.dtos.AccountHistoryDTO;
import emsi.java.ebanking.dtos.AccountOperationDTO;
import emsi.java.ebanking.dtos.BankAccountDTO;
import emsi.java.ebanking.dtos.CustomerDTO;

import java.util.List;

public interface BankAccountService {
    CustomerDTO saveCustomer(CustomerDTO customer);
    CurrentAccount saveCurrentBankAccount(double initialBalance, double overDraf, Long customerId) throws CustomerNotFoundEXception;
    SavingAccount saveSavingBankAccount(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundEXception;
    List<CustomerDTO> listCustomer();
    BankAccount getBankAccount(String accountId) throws BankAccountNotFoundException;
    void debit(String accountId,double amount,String description) throws BankAccountNotFoundException, BanlanceNotSufficientException;
    void credit(String accountId,double amount,String description) throws BanlanceNotSufficientException, BankAccountNotFoundException;

    void transfer(String accountIdSource, String accountIdDestination, double amount) throws BankAccountNotFoundException, BanlanceNotSufficientException;



    List<BankAccountDTO> bankAccountList();

    CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundEXception;

    CustomerDTO UpdateCustomer(CustomerDTO customerDTO);

    void deleteCustomer(Long customerId);
    List<AccountOperationDTO> accountHistory(String accountId);

    AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException;

    List<CustomerDTO> searchCustomers(String keyword);


}
