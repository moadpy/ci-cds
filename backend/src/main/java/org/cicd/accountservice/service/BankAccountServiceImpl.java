package org.cicd.accountservice.service;

import org.cicd.accountservice.dto.BankAccountDTO;
import org.cicd.accountservice.exception.BankAccountNotFoundException;
import org.cicd.accountservice.mapper.BankAccountMapper;
import org.cicd.accountservice.model.BankAccount;
import org.cicd.accountservice.repository.BankAccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


// This is a test comment to trigger the pipeline
@Service
public class BankAccountServiceImpl implements BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final BankAccountMapper bankAccountMapper;

    public BankAccountServiceImpl(BankAccountRepository bankAccountRepository, BankAccountMapper bankAccountMapper) {
        this.bankAccountRepository = bankAccountRepository;
        this.bankAccountMapper = bankAccountMapper;
    }

    @Override
    public List<BankAccountDTO> getAllAccounts() {
        return bankAccountRepository.findAll().stream()
                .map(bankAccountMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public BankAccountDTO getAccountById(Long id) {
        BankAccount bankAccount = bankAccountRepository.findById(id)
                .orElseThrow(() -> new BankAccountNotFoundException("Bank account not found with id: " + id));
        return bankAccountMapper.toDto(bankAccount);
    }

    @Override
    public BankAccountDTO createAccount(BankAccountDTO bankAccountDTO) {
        BankAccount bankAccount = bankAccountMapper.toEntity(bankAccountDTO);
        BankAccount savedBankAccount = bankAccountRepository.save(bankAccount);
        return bankAccountMapper.toDto(savedBankAccount);
    }

    @Override
    public BankAccountDTO updateAccount(Long id, BankAccountDTO bankAccountDTO) {
        BankAccount existingAccount = bankAccountRepository.findById(id)
                .orElseThrow(() -> new BankAccountNotFoundException("Bank account not found with id: " + id));

        existingAccount.setAccountNumber(bankAccountDTO.getAccountNumber());
        existingAccount.setAccountHolderName(bankAccountDTO.getAccountHolderName());
        existingAccount.setBalance(bankAccountDTO.getBalance());

        BankAccount updatedAccount = bankAccountRepository.save(existingAccount);
        return bankAccountMapper.toDto(updatedAccount);
    }

    @Override
    public void deleteAccount(Long id) {
        if (!bankAccountRepository.existsById(id)) {
            throw new BankAccountNotFoundException("Bank account not found with id: " + id);
        }
        bankAccountRepository.deleteById(id);
    }
}
