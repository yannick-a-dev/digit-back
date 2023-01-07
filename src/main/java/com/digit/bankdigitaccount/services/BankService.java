package com.digit.bankdigitaccount.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digit.bankdigitaccount.entities.BankAccount;
import com.digit.bankdigitaccount.entities.CurrentAccount;
import com.digit.bankdigitaccount.entities.SavingAccount;
import com.digit.bankdigitaccount.repository.BankAccountRepository;

@Service
@Transactional
public class BankService {
	
	@Autowired
	private BankAccountRepository bankAccountRepository;

	public void consulter() {
		BankAccount bankAccount = bankAccountRepository.findById("060aec18-39b3-4e50-81eb-a9cb8aa77d93")
				.orElse(null);
		if (bankAccount != null) {
			System.out.println("***********************");
			System.out.println(bankAccount.getId());
			System.out.println(bankAccount.getBalance());
			System.out.println(bankAccount.getStatus());
			System.out.println(bankAccount.getCreatedAt());
			System.out.println(bankAccount.getCustomer().getName());
			System.out.println(bankAccount.getClass().getSimpleName());
			if (bankAccount instanceof CurrentAccount) {
				System.out.println("Over Draft=>" + ((CurrentAccount) bankAccount).getOverDraft());
			} else if (bankAccount instanceof SavingAccount) {
				System.out.println("Rate=>" + ((SavingAccount) bankAccount).getInterestRate());
			}
			bankAccount.getAccountOperations().forEach(op -> {
				System.out.println("=============================");
				System.out.println(op.getType());
				System.out.println(op.getOperationDate());
				System.out.println(op.getAmount());
			});
		}
	}
}
