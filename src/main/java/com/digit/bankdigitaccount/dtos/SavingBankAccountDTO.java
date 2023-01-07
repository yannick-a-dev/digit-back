package com.digit.bankdigitaccount.dtos;

import java.util.Date;

import com.digit.bankdigitaccount.enums.AccountStatus;

import lombok.Data;

@Data
public class SavingBankAccountDTO extends BankAccountDTO{

	private String id;
	private double balance;
	private Date createdAt;
	private AccountStatus status;
	private CustomerDTO customerDTO;
	private double interestRate;
}
