package com.digit.bankdigitaccount.services;

import java.util.List;

import com.digit.bankdigitaccount.dtos.AccountHistoryDTO;
import com.digit.bankdigitaccount.dtos.AccountOperationDTO;
import com.digit.bankdigitaccount.dtos.BankAccountDTO;
import com.digit.bankdigitaccount.dtos.CurrentBankAccountDTO;
import com.digit.bankdigitaccount.dtos.CustomerDTO;
import com.digit.bankdigitaccount.dtos.SavingBankAccountDTO;
import com.digit.bankdigitaccount.exceptions.BalanceNotSufficientException;
import com.digit.bankdigitaccount.exceptions.BankAccountNotFoundException;
import com.digit.bankdigitaccount.exceptions.CustomerNotFoundException;


public interface BankAccountService {

	CustomerDTO saveCustomer(CustomerDTO customerDTO);
	CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance,double overDraft,Long customerId) throws CustomerNotFoundException;
	SavingBankAccountDTO saveSavingBankAccount(double initialBalance,double interestRate,Long customerId) throws CustomerNotFoundException;
	List<CustomerDTO> listCustomers();
	BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException;
	void debit(String accountId,double amount,String description) throws BankAccountNotFoundException, BalanceNotSufficientException;
	void credit(String accountId,double amount,String description) throws BankAccountNotFoundException;
	void transfer(String accountSource,String accountIdDestination,double amount) throws BankAccountNotFoundException, BalanceNotSufficientException;
	List<BankAccountDTO> bankAccountList();
	CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException;
	CustomerDTO updateCustomer(CustomerDTO customerDTO);
	void deleteCustomer(Long customerId);
	List<CustomerDTO> searchCustomers(String keyword);
	List<AccountOperationDTO> accountHistory(String accountId);
	AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException;
	
}
