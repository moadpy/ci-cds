package org.cicd.accountservice.service;

import org.cicd.accountservice.dto.BankAccountDTO;
import org.cicd.accountservice.exception.BankAccountNotFoundException;
import org.cicd.accountservice.mapper.BankAccountMapper;
import org.cicd.accountservice.model.BankAccount;
import org.cicd.accountservice.repository.BankAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankAccountServiceImplTest {

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private BankAccountMapper bankAccountMapper;

    // this is the class we are trying to test, we inject mocks into this class
    @InjectMocks
    private BankAccountServiceImpl bankAccountService;

    private BankAccount account;
    private BankAccountDTO accountDTO;

    // before each test we will run this code
    @BeforeEach
    void setUp() {
        // Arrange: Set up the test. Create mocks, test data, etc.
        account = new BankAccount(1L, "12345", "Test Holder", new BigDecimal("1000.00"));
        accountDTO = new BankAccountDTO();
        accountDTO.setId(1L);
        accountDTO.setAccountNumber("12345");
        accountDTO.setAccountHolderName("Test Holder");
        accountDTO.setBalance(new BigDecimal("1000.00"));
    }

    // here we verify that the `getAllAccounts` method correctly retrieves all bank accounts and maps them to DTOs.
    // what we are really testing here is the method getAllAccounts it should correctly return the accounts that the accountRepository gives it
    @Test
    void getAllAccounts() {
        // bankAccountRepository is just a Mock we are not testing a method in that class
        when(bankAccountRepository.findAll()).thenReturn(Collections.singletonList(account));
        when(bankAccountMapper.toDto(any(BankAccount.class))).thenReturn(accountDTO);
        List<BankAccountDTO> accounts = bankAccountService.getAllAccounts();
        assertEquals(1, accounts.size());
        verify(bankAccountRepository, times(1)).findAll();
        verify(bankAccountMapper, times(1)).toDto(account);
    }

    @Test
    void getAccountById() {
        when(bankAccountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(bankAccountMapper.toDto(any(BankAccount.class))).thenReturn(accountDTO);
        BankAccountDTO foundAccount = bankAccountService.getAccountById(1L);
        assertNotNull(foundAccount);
        assertEquals("12345", foundAccount.getAccountNumber());
        verify(bankAccountRepository, times(1)).findById(1L);
    }

    // if the bank account repository returns an empty optional then getAccountById should return an exception
    @Test
    void getAccountById_whenNotFound_shouldThrowException() {
        when(bankAccountRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(BankAccountNotFoundException.class, () -> {
            bankAccountService.getAccountById(1L);
        });
    }

    @Test
    void createAccount() {
        when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(account);
        when(bankAccountMapper.toEntity(any(BankAccountDTO.class))).thenReturn(account);
        when(bankAccountMapper.toDto(any(BankAccount.class))).thenReturn(accountDTO);

        BankAccountDTO createdAccount = bankAccountService.createAccount(accountDTO);
        assertNotNull(createdAccount);
        assertEquals("12345", createdAccount.getAccountNumber());
        verify(bankAccountRepository, times(1)).save(account);
    }

    @Test
    void updateAccount() {
        BankAccountDTO updatedDetailsDTO = new BankAccountDTO();
        updatedDetailsDTO.setAccountNumber("54321");
        updatedDetailsDTO.setAccountHolderName("Updated Holder");
        updatedDetailsDTO.setBalance(new BigDecimal("2000.00"));

        when(bankAccountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(account);
        when(bankAccountMapper.toDto(any(BankAccount.class))).thenReturn(updatedDetailsDTO);

        BankAccountDTO updatedAccount = bankAccountService.updateAccount(1L, updatedDetailsDTO);
        assertNotNull(updatedAccount);
        assertEquals("54321", updatedAccount.getAccountNumber());
        verify(bankAccountRepository, times(1)).findById(1L);
        verify(bankAccountRepository, times(1)).save(any(BankAccount.class));
    }

    @Test
    void deleteAccount() {
        when(bankAccountRepository.existsById(1L)).thenReturn(true);
        doNothing().when(bankAccountRepository).deleteById(1L);
        bankAccountService.deleteAccount(1L);
        verify(bankAccountRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteAccount_whenNotFound_shouldThrowException() {
        when(bankAccountRepository.existsById(1L)).thenReturn(false);
        assertThrows(BankAccountNotFoundException.class, () -> {
            bankAccountService.deleteAccount(1L);
        });
    }
}
