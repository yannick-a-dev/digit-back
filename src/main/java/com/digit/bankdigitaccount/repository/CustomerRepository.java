package com.digit.bankdigitaccount.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.digit.bankdigitaccount.entities.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

	List<Customer> findByNameContains(String keyword);
}
