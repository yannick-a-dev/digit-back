package com.digit.bankdigitaccount.dtos;

import java.util.Date;

import com.digit.bankdigitaccount.enums.OperationType;

import lombok.Data;

@Data
public class AccountOperationDTO {

	private Long id;
	private Date operationDate;
	private double amount;
	private OperationType type;
	private String description;
}
