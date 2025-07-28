package org.cicd.accountservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cicd.accountservice.dto.BankAccountRequest;
import org.cicd.accountservice.dto.BankAccountResponse;
import org.cicd.accountservice.model.BankAccount;
import org.cicd.accountservice.repository.BankAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test for BankAccountController using H2 in-memory database
 * This test verifies the complete flow from controller to database layer
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("integration-test")
@Transactional
public class BankAccountControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        bankAccountRepository.deleteAll();
    }

    @Test
    void shouldCreateBankAccount() throws Exception {
        // Given
        BankAccountRequest request = new BankAccountRequest();
        request.setAccountHolderName("John Doe");
        request.setAccountNumber("123456789");
        request.setBalance(BigDecimal.valueOf(1000.00));

        // When & Then
        mockMvc.perform(post("/api/bank-accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountHolderName", is("John Doe")))
                .andExpect(jsonPath("$.accountNumber", is("123456789")))
                .andExpect(jsonPath("$.balance", is(1000.00)))
                .andExpect(jsonPath("$.id", notNullValue()));

        // Verify database state
        assert bankAccountRepository.count() == 1;
    }

    @Test
    void shouldGetAllBankAccounts() throws Exception {
        // Given
        BankAccount account1 = new BankAccount();
        account1.setAccountHolderName("John Doe");
        account1.setAccountNumber("123456789");
        account1.setBalance(BigDecimal.valueOf(1000.00));

        BankAccount account2 = new BankAccount();
        account2.setAccountHolderName("Jane Smith");
        account2.setAccountNumber("987654321");
        account2.setBalance(BigDecimal.valueOf(2000.00));

        bankAccountRepository.save(account1);
        bankAccountRepository.save(account2);

        // When & Then
        mockMvc.perform(get("/api/bank-accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].accountHolderName", is("John Doe")))
                .andExpected(jsonPath("$[1].accountHolderName", is("Jane Smith")));
    }

    @Test
    void shouldGetBankAccountById() throws Exception {
        // Given
        BankAccount account = new BankAccount();
        account.setAccountHolderName("John Doe");
        account.setAccountNumber("123456789");
        account.setBalance(BigDecimal.valueOf(1000.00));

        BankAccount savedAccount = bankAccountRepository.save(account);

        // When & Then
        mockMvc.perform(get("/api/bank-accounts/{id}", savedAccount.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedAccount.getId().intValue())))
                .andExpect(jsonPath("$.accountHolderName", is("John Doe")))
                .andExpect(jsonPath("$.accountNumber", is("123456789")))
                .andExpect(jsonPath("$.balance", is(1000.00)));
    }

    @Test
    void shouldUpdateBankAccount() throws Exception {
        // Given
        BankAccount account = new BankAccount();
        account.setAccountHolderName("John Doe");
        account.setAccountNumber("123456789");
        account.setBalance(BigDecimal.valueOf(1000.00));

        BankAccount savedAccount = bankAccountRepository.save(account);

        BankAccountRequest updateRequest = new BankAccountRequest();
        updateRequest.setAccountHolderName("John Updated");
        updateRequest.setAccountNumber("123456789");
        updateRequest.setBalance(BigDecimal.valueOf(1500.00));

        // When & Then
        mockMvc.perform(put("/api/bank-accounts/{id}", savedAccount.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountHolderName", is("John Updated")))
                .andExpect(jsonPath("$.balance", is(1500.00)));
    }

    @Test
    void shouldDeleteBankAccount() throws Exception {
        // Given
        BankAccount account = new BankAccount();
        account.setAccountHolderName("John Doe");
        account.setAccountNumber("123456789");
        account.setBalance(BigDecimal.valueOf(1000.00));

        BankAccount savedAccount = bankAccountRepository.save(account);

        // When & Then
        mockMvc.perform(delete("/api/bank-accounts/{id}", savedAccount.getId()))
                .andExpect(status().isNoContent());

        // Verify deletion
        assert bankAccountRepository.count() == 0;
    }

    @Test
    void shouldReturnNotFoundForNonExistentAccount() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/bank-accounts/{id}", 999L))
                .andExpect(status().isNotFound());
    }
}
