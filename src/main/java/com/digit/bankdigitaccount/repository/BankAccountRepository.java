package com.digit.bankdigitaccount.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.digit.bankdigitaccount.entities.BankAccount;

public interface BankAccountRepository extends JpaRepository<BankAccount, String> {

}
