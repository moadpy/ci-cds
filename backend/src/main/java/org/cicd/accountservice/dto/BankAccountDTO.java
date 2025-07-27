package org.cicd.accountservice.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BankAccountDTO {
    private Long id;
    private String accountNumber;
    private String accountHolderName;
    private BigDecimal balance;
}

