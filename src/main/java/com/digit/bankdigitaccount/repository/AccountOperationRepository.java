package com.digit.bankdigitaccount.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.digit.bankdigitaccount.entities.AccountOperation;

public interface AccountOperationRepository extends JpaRepository<AccountOperation, Long> {

	List<AccountOperation> findByBankAccountId(String accoundId);
	
	//pagination
	Page<AccountOperation> findByBankAccountIdOrderByOperationDateDesc(String accoundId,Pageable pageable);
}
