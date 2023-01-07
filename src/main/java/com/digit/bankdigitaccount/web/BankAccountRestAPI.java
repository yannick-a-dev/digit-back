package com.digit.bankdigitaccount.web;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.digit.bankdigitaccount.dtos.AccountHistoryDTO;
import com.digit.bankdigitaccount.dtos.AccountOperationDTO;
import com.digit.bankdigitaccount.dtos.BankAccountDTO;
import com.digit.bankdigitaccount.dtos.CreditDTO;
import com.digit.bankdigitaccount.dtos.DebitDTO;
import com.digit.bankdigitaccount.dtos.TransferRequestDTO;
import com.digit.bankdigitaccount.exceptions.BalanceNotSufficientException;
import com.digit.bankdigitaccount.exceptions.BankAccountNotFoundException;
import com.digit.bankdigitaccount.services.BankAccountService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
public class BankAccountRestAPI {

	private BankAccountService bankAccountService;
	
	@GetMapping("/accounts/{accountId}")
	public BankAccountDTO getBankAccount(@PathVariable String accountId) throws BankAccountNotFoundException {
		return bankAccountService.getBankAccount(accountId);	
	}
	@GetMapping("/accounts")
	public List<BankAccountDTO> listAccounts(){
		return bankAccountService.bankAccountList();
	}
	
	@GetMapping("/accounts/{accountId}/operations")
	public List<AccountOperationDTO> getHistory(@PathVariable String accountId){
		return bankAccountService.accountHistory(accountId);	
	}
	
	//pagination
	@GetMapping("/accounts/{accountId}/pageOperations")
	public AccountHistoryDTO getAccountHistory(@PathVariable String accountId,
			                                           @RequestParam(name = "page", defaultValue = "0") int page,
			                                           @RequestParam(name = "size", defaultValue = "5") int size) throws BankAccountNotFoundException{
		return bankAccountService.getAccountHistory(accountId,page,size);	
	}
	
	@PostMapping("/accounts/debit")
	public DebitDTO debit(@RequestBody DebitDTO debitDTO) throws BankAccountNotFoundException, BalanceNotSufficientException {
		this.bankAccountService.debit(debitDTO.getAccountId(), debitDTO.getAmount(), debitDTO.getDescription());
		return debitDTO;
	}
	@PostMapping("/accounts/credit")
	public CreditDTO debit(@RequestBody CreditDTO creditDTO) throws BankAccountNotFoundException, BalanceNotSufficientException{
		this.bankAccountService.debit(creditDTO.getAccountId(), creditDTO.getAmount(), creditDTO.getDescription());
		return creditDTO;
	}
	
	@PostMapping("/accounts/transfer")
	public void transfer(@RequestBody TransferRequestDTO transferRequestDTO) throws BankAccountNotFoundException, BalanceNotSufficientException{
		this.bankAccountService.transfer(transferRequestDTO.getAccountSource(),transferRequestDTO.getAccountDestination(),transferRequestDTO.getAmount());
	}
	
}
