package org.cicd.accountservice.service;

import org.cicd.accountservice.dto.BankAccountDTO;

import java.util.List;

public interface BankAccountService {
    List<BankAccountDTO> getAllAccounts();
    BankAccountDTO getAccountById(Long id);
    BankAccountDTO createAccount(BankAccountDTO bankAccountDTO);
    BankAccountDTO updateAccount(Long id, BankAccountDTO bankAccountDTO);
    void deleteAccount(Long id);
}
