package com.digit.bankdigitaccount.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digit.bankdigitaccount.dtos.AccountHistoryDTO;
import com.digit.bankdigitaccount.dtos.AccountOperationDTO;
import com.digit.bankdigitaccount.dtos.BankAccountDTO;
import com.digit.bankdigitaccount.dtos.CurrentBankAccountDTO;
import com.digit.bankdigitaccount.dtos.CustomerDTO;
import com.digit.bankdigitaccount.dtos.SavingBankAccountDTO;
import com.digit.bankdigitaccount.entities.AccountOperation;
import com.digit.bankdigitaccount.entities.BankAccount;
import com.digit.bankdigitaccount.entities.CurrentAccount;
import com.digit.bankdigitaccount.entities.Customer;
import com.digit.bankdigitaccount.entities.SavingAccount;
import com.digit.bankdigitaccount.enums.OperationType;
import com.digit.bankdigitaccount.exceptions.BalanceNotSufficientException;
import com.digit.bankdigitaccount.exceptions.BankAccountNotFoundException;
import com.digit.bankdigitaccount.exceptions.CustomerNotFoundException;
import com.digit.bankdigitaccount.mappers.BankAccountMapperImpl;
import com.digit.bankdigitaccount.repository.AccountOperationRepository;
import com.digit.bankdigitaccount.repository.BankAccountRepository;
import com.digit.bankdigitaccount.repository.CustomerRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class BankAccountServiceImpl implements BankAccountService{

	private CustomerRepository customerRepository;
	private BankAccountRepository bankAccountRepository;
	private AccountOperationRepository accountOperationRepository;
	private BankAccountMapperImpl dtoMapper;
	
	@Override
	public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
		log.info("Saving new Customer");
		Customer customer = dtoMapper.fromCustomerDTO(customerDTO);
		Customer savedCustomer = customerRepository.save(customer);
		return dtoMapper.fromCustomer(savedCustomer);
	}
	
	@Override
	public CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId)
			throws CustomerNotFoundException {
		Customer customer = customerRepository.findById(customerId).orElse(null);
		if(customer==null) {
			throw new CustomerNotFoundException("Customer not found");
		}
		CurrentAccount currentAccount = new CurrentAccount();
		currentAccount.setId(UUID.randomUUID().toString());
		currentAccount.setCreatedAt(new Date());
		currentAccount.setBalance(initialBalance);
		currentAccount.setOverDraft(overDraft);
		currentAccount.setCustomer(customer);
		CurrentAccount savedBankAccount = bankAccountRepository.save(currentAccount);
		return dtoMapper.fromCurrentBankAccount(savedBankAccount);
	}



	@Override
	public SavingBankAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerId)
			throws CustomerNotFoundException {
		Customer customer = customerRepository.findById(customerId).orElse(null);
		if(customer==null) {
			throw new CustomerNotFoundException("Customer not found");
		}
		SavingAccount savingAccount = new SavingAccount();
		
		savingAccount.setId(UUID.randomUUID().toString());
		savingAccount.setCreatedAt(new Date());
		savingAccount.setBalance(initialBalance);
		savingAccount.setInterestRate(interestRate);
		savingAccount.setCustomer(customer);
		SavingAccount savedBankAccount = bankAccountRepository.save(savingAccount);
		return dtoMapper.fromSavingBankAccount(savedBankAccount);
	}

	@Override
	public List<CustomerDTO> listCustomers() {
		
		List<Customer> customers = customerRepository.findAll();
		List<CustomerDTO> customerDTOs = customers.stream()
				.map(customer->dtoMapper.fromCustomer(customer))
				.collect(Collectors.toList());
		return customerDTOs;
		
		/*List<CustomerDTO> customerDTOs=new ArrayList<>();
		for(Customer customer:customers) {
			CustomerDTO customerDTO=dtoMapper.fromCustomer(customer);
			customerDTOs.add(customerDTO);
		}
		return customerDTOs;
		*/
	}

	@Override
	public BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException {
		BankAccount bankAccount = bankAccountRepository.findById(accountId)
				.orElseThrow(()->new BankAccountNotFoundException("BankAccount not found"));
		
		if(bankAccount instanceof SavingAccount) {
			SavingAccount savingAccount = (SavingAccount) bankAccount;
			return dtoMapper.fromSavingBankAccount(savingAccount);
		}else {
			CurrentAccount currentAccount = (CurrentAccount) bankAccount;
			return dtoMapper.fromCurrentBankAccount(currentAccount);
		}
	}

	@Override
	public void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException {
		BankAccount bankAccount = bankAccountRepository.findById(accountId)
				.orElseThrow(()->new BankAccountNotFoundException("BankAccount not found"));
		if(bankAccount.getBalance()< amount) {
			throw new BalanceNotSufficientException("Balance not sufficient");
		}
		AccountOperation accountOperation = new AccountOperation();
		accountOperation.setType(OperationType.DEBIT);
		accountOperation.setAmount(amount);
		accountOperation.setDescription(description);
		accountOperation.setOperationDate(new Date());
		accountOperation.setBankAccount(bankAccount);
		accountOperationRepository.save(accountOperation);
		bankAccount.setBalance(bankAccount.getBalance()-amount);
		bankAccountRepository.save(bankAccount);
	}

	@Override
	public void credit(String accountId, double amount, String description) throws BankAccountNotFoundException {
		BankAccount bankAccount = bankAccountRepository.findById(accountId)
				.orElseThrow(()->new BankAccountNotFoundException("BankAccount not found"));
				
		AccountOperation accountOperation = new AccountOperation();
		accountOperation.setType(OperationType.CREDIT);
		accountOperation.setAmount(amount);
		accountOperation.setDescription(description);
		accountOperation.setOperationDate(new Date());
		accountOperation.setBankAccount(bankAccount);
		accountOperationRepository.save(accountOperation);
		bankAccount.setBalance(bankAccount.getBalance()+ amount);
		bankAccountRepository.save(bankAccount);
		
	}

	@Override
	public void transfer(String accountSource,String accountDestination, double amount) throws BankAccountNotFoundException, BalanceNotSufficientException {
		debit(accountSource,amount,"Transfert to "+accountDestination);
		credit(accountDestination,amount, "Transfert from"+accountSource);
		
	}
	@Override
	public List<BankAccountDTO> bankAccountList(){
	   List<BankAccount> bankAccounts = bankAccountRepository.findAll();
	 List<BankAccountDTO> bankAccountDTOS = bankAccounts.stream().map(bankAccount -> {
		   if(bankAccount instanceof SavingAccount) {
			   SavingAccount savingAccount = (SavingAccount) bankAccount;
			   return dtoMapper.fromSavingBankAccount(savingAccount);
		   }else{
			   CurrentAccount currentAccount = (CurrentAccount) bankAccount;
			   return dtoMapper.fromCurrentBankAccount(currentAccount);
		   }
	   }).collect(Collectors.toList());
	return bankAccountDTOS;
	}
	
	@Override
	public CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException {
		Customer customer = customerRepository.findById(customerId)
		.orElseThrow(()->new CustomerNotFoundException("Customer not found"));
		return dtoMapper.fromCustomer(customer);
	}
	@Override
	public CustomerDTO updateCustomer(CustomerDTO customerDTO) {
		log.info("Saving new Customer");
		Customer customer = dtoMapper.fromCustomerDTO(customerDTO);
		Customer savedCustomer = customerRepository.save(customer);
		return dtoMapper.fromCustomer(savedCustomer);
	}
	@Override
	public void deleteCustomer(Long customerId) {
		customerRepository.deleteById(customerId);
	}

	@Override
	public List<CustomerDTO> searchCustomers(String keyword) {
		List<Customer> customers = customerRepository.findByNameContains(keyword);
		List<CustomerDTO> customerDTOS = customers.stream().map(cust->dtoMapper.fromCustomer(cust)).collect(Collectors.toList());
		return customerDTOS;
	}
	
	@Override
	public List<AccountOperationDTO> accountHistory(String accountId){
	   List<AccountOperation> accountOperations = accountOperationRepository.findByBankAccountId(accountId);
	   return accountOperations.stream().map(op ->dtoMapper.fromAccountOperation(op)).collect(Collectors.toList());
	}

	//pagination
	@Override
	public AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException {
		BankAccount bankAccount = bankAccountRepository.findById(accountId).orElse(null);
		if(bankAccount==null) throw new BankAccountNotFoundException("Account not found");
		Page<AccountOperation> accountOperations = accountOperationRepository.findByBankAccountIdOrderByOperationDateDesc(accountId,PageRequest.of(page, size));
		AccountHistoryDTO accountHistoryDTO = new AccountHistoryDTO();
		List<AccountOperationDTO> accountOperationDTOS = accountOperations.getContent().stream().map(op->dtoMapper.fromAccountOperation(op)).collect(Collectors.toList());
		accountHistoryDTO.setAccountOperationDTOs(accountOperationDTOS);
		accountHistoryDTO.setAccountId(bankAccount.getId());
		accountHistoryDTO.setBalance(bankAccount.getBalance());
		accountHistoryDTO.setCurrentPage(page);
		accountHistoryDTO.setPageSize(size);
		accountHistoryDTO.setTotalPages(accountOperations.getTotalPages());
		return accountHistoryDTO;
	}
}
