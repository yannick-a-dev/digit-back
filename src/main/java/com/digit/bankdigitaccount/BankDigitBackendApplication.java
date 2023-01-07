package com.digit.bankdigitaccount;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import javax.management.RuntimeErrorException;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import com.digit.bankdigitaccount.dtos.BankAccountDTO;
import com.digit.bankdigitaccount.dtos.CurrentBankAccountDTO;
import com.digit.bankdigitaccount.dtos.SavingBankAccountDTO;
import com.digit.bankdigitaccount.dtos.CustomerDTO;
import com.digit.bankdigitaccount.entities.AccountOperation;
import com.digit.bankdigitaccount.entities.BankAccount;
import com.digit.bankdigitaccount.entities.CurrentAccount;
import com.digit.bankdigitaccount.entities.Customer;
import com.digit.bankdigitaccount.entities.SavingAccount;
import com.digit.bankdigitaccount.enums.AccountStatus;
import com.digit.bankdigitaccount.enums.OperationType;
import com.digit.bankdigitaccount.exceptions.BalanceNotSufficientException;
import com.digit.bankdigitaccount.exceptions.BankAccountNotFoundException;
import com.digit.bankdigitaccount.exceptions.CustomerNotFoundException;
import com.digit.bankdigitaccount.repository.AccountOperationRepository;
import com.digit.bankdigitaccount.repository.BankAccountRepository;
import com.digit.bankdigitaccount.repository.CustomerRepository;
import com.digit.bankdigitaccount.services.BankAccountService;
import com.digit.bankdigitaccount.services.BankService;

@SpringBootApplication
public class BankDigitBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankDigitBackendApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(BankAccountService bankAccountService) {
		return args -> {
			Stream.of("Yannick", "Francois", "Raphael").forEach(name -> {
				CustomerDTO customer = new CustomerDTO();
				customer.setName(name);
				customer.setEmail(name + "@gmail.com");
				bankAccountService.saveCustomer(customer);
			});

			bankAccountService.listCustomers().forEach(customer -> {
				  try {
					bankAccountService.saveCurrentBankAccount(Math.random()*90000, 90000, customer.getId());
				    bankAccountService.saveSavingBankAccount(Math.random(), 5.5,customer.getId());
				   
				  } catch (CustomerNotFoundException e) {
					e.printStackTrace();
				} 
			   });
			
			   List<BankAccountDTO> bankAccounts = bankAccountService.bankAccountList();
			    for(BankAccountDTO bankAccount: bankAccounts) {
			    	for(int i = 0; i < 10; i++) {
			    		String accountId;
			    		if(bankAccount instanceof SavingBankAccountDTO) {
			    		  accountId	= ((SavingBankAccountDTO) bankAccount).getId();
			    		}else {
			    		  accountId = ((CurrentBankAccountDTO) bankAccount).getId();
			    		}
			    		bankAccountService.credit(accountId, 10000+Math.random()*120000, "Credit");
			    		bankAccountService.debit(accountId, 1000+Math.random()*9000, "Debit");
			    	}
			    }
		};

	}

	// @Bean
	CommandLineRunner start(CustomerRepository customerRepository, BankAccountRepository bankAccountRepository,
			AccountOperationRepository accountOperationRepository) {

		return args -> {
			Stream.of("Yannick", "Francois", "Raphael").forEach(name -> {
				Customer customer = new Customer();
				customer.setName(name);
				customer.setEmail(name + "@gmail.com");
				customerRepository.save(customer);
			});
			customerRepository.findAll().forEach(cust -> {
				CurrentAccount currentAccount = new CurrentAccount();
				currentAccount.setId(UUID.randomUUID().toString());
				currentAccount.setBalance(Math.random() * 90000);
				currentAccount.setCreatedAt(new Date());
				currentAccount.setStatus(AccountStatus.CREATED);
				currentAccount.setCustomer(cust);
				currentAccount.setOverDraft(9000);
				bankAccountRepository.save(currentAccount);

				SavingAccount savingAccount = new SavingAccount();
				savingAccount.setId(UUID.randomUUID().toString());
				savingAccount.setBalance(Math.random() * 90000);
				savingAccount.setCreatedAt(new Date());
				savingAccount.setStatus(AccountStatus.CREATED);
				savingAccount.setCustomer(cust);
				savingAccount.setInterestRate(5.5);
				bankAccountRepository.save(savingAccount);
			});
			bankAccountRepository.findAll().forEach(acc -> {
				for (int i = 0; i < 10; i++) {
					AccountOperation accountOperation = new AccountOperation();
					accountOperation.setOperationDate(new Date());
					accountOperation.setAmount(Math.random() * 12000);
					accountOperation.setType(Math.random() > 0.5 ? OperationType.DEBIT : OperationType.CREDIT);
					accountOperation.setBankAccount(acc);
					accountOperationRepository.save(accountOperation);
				}
			});
		};

	}

}