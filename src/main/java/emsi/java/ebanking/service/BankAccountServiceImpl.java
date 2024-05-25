package emsi.java.ebanking.service;

import emsi.java.ebanking.Entity.*;
import emsi.java.ebanking.Enum.OperationType;
import emsi.java.ebanking.Exception.BankAccountNotFoundException;
import emsi.java.ebanking.Exception.BanlanceNotSufficientException;
import emsi.java.ebanking.Exception.CustomerNotFoundEXception;
import emsi.java.ebanking.Repository.AccountOperationRepository;
import emsi.java.ebanking.Repository.BankAccountRepository;
import emsi.java.ebanking.Repository.CustomerRepository;
import emsi.java.ebanking.dtos.AccountHistoryDTO;
import emsi.java.ebanking.dtos.AccountOperationDTO;
import emsi.java.ebanking.dtos.BankAccountDTO;
import emsi.java.ebanking.dtos.CustomerDTO;
import emsi.java.ebanking.mappers.BankAccountMappeImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class BankAccountServiceImpl implements BankAccountService {

    private CustomerRepository customerRepository;

    private BankAccountRepository bankAccountRepository;


    private AccountOperationRepository accountOperationRepository;

private BankAccountMappeImpl bankAccountMappe;

    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        log.info("Saving new Customer");
        Customer customer=bankAccountMappe.fromCustomerDTO(customerDTO);
        Customer savedCustomer =customerRepository.save(customer);
        return bankAccountMappe.fromCustomer(savedCustomer);
    }

    @Override
    public CurrentAccount saveCurrentBankAccount(double initialBalance,  double overDraf, Long customerId) throws CustomerNotFoundEXception {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null) {
            throw new CustomerNotFoundEXception("Customer not found");
        }
        CurrentAccount currentAccount= new CurrentAccount();
        currentAccount.setId(UUID.randomUUID().toString());
        currentAccount.setCreatedAt(new Date());
        currentAccount.setBalance(initialBalance);
        currentAccount.setCustomer(customer);
        currentAccount.setOverDraft(overDraf);
        CurrentAccount savedBankAccount=bankAccountRepository.save(currentAccount);

        return savedBankAccount;
    }

    @Override
    public SavingAccount saveSavingBankAccount(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundEXception {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null) {
            throw new CustomerNotFoundEXception("Customer not found");
        }
       SavingAccount savingAccount=new SavingAccount();
        savingAccount.setId(UUID.randomUUID().toString());
        savingAccount.setCreatedAt(new Date());
        savingAccount.setBalance(initialBalance);
        savingAccount.setCustomer(customer);
        savingAccount.setInterestRate(interestRate);
        SavingAccount savedBankAccount=bankAccountRepository.save(savingAccount);

        return savedBankAccount;

    }


    @Override
    public List<CustomerDTO> listCustomer() {
        List<Customer> customers= customerRepository.findAll();
        List<CustomerDTO>  customerDTO =   customers.stream().map(cust ->bankAccountMappe.fromCustomer(cust)).collect(Collectors.toList());
        return customerDTO;
    }

    @Override
    public BankAccount getBankAccount(String accountId) throws BankAccountNotFoundException {
        BankAccount bankAccount=bankAccountRepository.findById(accountId).
                orElseThrow(()->new BankAccountNotFoundException("BankAccount not found "));
        return bankAccount;
    }

    @Override
    public void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, BanlanceNotSufficientException {
BankAccount bankAccount =getBankAccount(accountId);
if(bankAccount.getBalance()<amount)
    throw  new BanlanceNotSufficientException("Banlance not found");
    AccountOperation accountOperation =new AccountOperation();
    accountOperation.setOpType(OperationType.DEBIT);
    accountOperation.setAmount(amount);
    accountOperation.setOperationDate(new Date());
    accountOperation.setDescription(description);
   accountOperationRepository.save(accountOperation);
   bankAccount.setBalance(bankAccount.getBalance()-amount);
   bankAccountRepository.save(bankAccount);


    }

    @Override
    public void credit(String accountId, double amount, String description) throws  BankAccountNotFoundException {
        BankAccount bankAccount =getBankAccount(accountId);

        AccountOperation accountOperation =new AccountOperation();
        accountOperation.setOpType(OperationType.CREDIT);
        accountOperation.setAmount(amount);
        accountOperation.setOperationDate(new Date());
        accountOperation.setDescription(description);
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance()+amount);
        bankAccountRepository.save(bankAccount);


    }



    @Override
    public void transfer(String accountIdSource, String accountIdDestination, double amount) throws BankAccountNotFoundException, BanlanceNotSufficientException {
        debit(accountIdSource,amount,"Transfer to "+accountIdDestination);
        credit(accountIdDestination,amount,"Transfer from "+accountIdSource);
    }


    @Override
    public List<BankAccountDTO> bankAccountList(){
        List<BankAccount> bankAccounts = bankAccountRepository.findAll();
        List<BankAccountDTO> bankAccountDTOS = bankAccounts.stream().map(bankAccount -> {
            if (bankAccount instanceof SavingAccount) {
                SavingAccount savingAccount = (SavingAccount) bankAccount;
                return bankAccountMappe.fromSavingBankAccount(savingAccount);
            } else {
                CurrentAccount currentAccount = (CurrentAccount) bankAccount;
                return bankAccountMappe.fromCurrentBankAccount(currentAccount);
            }
        }).collect(Collectors.toList());
        return bankAccountDTOS;
    }
    @Override
    public CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundEXception {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundEXception("Customer Not found"));
        return bankAccountMappe.fromCustomer(customer);
    }

    @Override
    public CustomerDTO UpdateCustomer(CustomerDTO customerDTO) {
        log.info("Saving new Customer");
        Customer customer=bankAccountMappe.fromCustomerDTO(customerDTO);
        Customer savedCustomer =customerRepository.save(customer);
        return bankAccountMappe.fromCustomer(savedCustomer);
    }




    @Override
    public void deleteCustomer(Long customerId){
        customerRepository.deleteById(customerId);
    }
    @Override
    public List<AccountOperationDTO> accountHistory(String accountId){
        List<AccountOperation> accountOperations = accountOperationRepository.findByBankAccountId(accountId);
        return accountOperations.stream().map(op->bankAccountMappe.fromAccountOperation(op)).collect(Collectors.toList());
    }

    @Override
    public AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException {
        BankAccount bankAccount=bankAccountRepository.findById(accountId).orElse(null);
        if(bankAccount==null) throw new BankAccountNotFoundException("Account not Found");
        Page<AccountOperation> accountOperations = accountOperationRepository.findByBankAccountIdOrderByOperationDateDesc(accountId, PageRequest.of(page, size));
        AccountHistoryDTO accountHistoryDTO=new AccountHistoryDTO();
        List<AccountOperationDTO> accountOperationDTOS = accountOperations.getContent().stream().map(op -> bankAccountMappe.fromAccountOperation(op)).collect(Collectors.toList());
        accountHistoryDTO.setAccountOperationDTOS(accountOperationDTOS);
        accountHistoryDTO.setAccountId(bankAccount.getId());
        accountHistoryDTO.setBalance(bankAccount.getBalance());
        accountHistoryDTO.setCurrentPage(page);
        accountHistoryDTO.setPageSize(size);
        accountHistoryDTO.setTotalPages(accountOperations.getTotalPages());
        return accountHistoryDTO;
    }

    @Override
    public List<CustomerDTO> searchCustomers(String keyword) {
        List<Customer> customers=customerRepository.searchCustomer(keyword);
        List<CustomerDTO> customerDTOS = customers.stream().map(cust -> bankAccountMappe.fromCustomer(cust)).collect(Collectors.toList());
        return customerDTOS;
    }
}
